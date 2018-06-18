(ns breaking-point.core
  (:require
   [re-frame.core :as rf]
   [re-frame-fx.dispatch]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PUBLIC API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(rf/reg-event-fx
 ::set-breakpoints
 (fn [_ [_ {:keys [breakpoints ;; required
                   debounce-ms ;; optional
                   ]
            :as   opts}]]
   {::set-breakpoints opts}))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; COFX

(rf/reg-cofx
 ::screen-width
 (fn [coeffect]
   (let [screen-width (or (.-innerWidth js/window)
                          (-> js/document
                              .-documentElement
                              .-clientWidth)
                          (-> js/document
                              .-body
                              .-clientWidth))]
     (assoc coeffect :screen-width screen-width))))


;; Events

(defn set-screen-width
  [{:keys [db
           screen-width]} _]
  {:db (assoc-in db
                 [::breakpoints :screen-width]
                 screen-width)})

(rf/reg-event-fx ::set-screen-width
                 [(rf/inject-cofx ::screen-width)]
                 set-screen-width)

(rf/reg-event-fx ::set-screen-width-debounced
                 (fn [_ [_ debounce-ms]]
                   {:dispatch-debounce [{:id      ::calcaulate-width-after-resize
                                         :timeout debounce-ms
                                         :action  :dispatch
                                         :event   [::set-screen-width]}]}))


;; Subs

(defn get-screen-width [db _]
  (get-in db [::breakpoints :screen-width]))


(defn ->get-screen [breakpoints]
  (fn get-screen
    [screen-width _]
    (when screen-width
      (reduce
       (fn [prev-breakpoint [screen-key breakpoint]]
         (if (or (nil? breakpoint)
                 (and (< screen-width breakpoint)
                      (>= screen-width prev-breakpoint)))
           (reduced screen-key)
           breakpoint))
       0
       (partition-all 2 breakpoints)))))


(defn register-subs [breakpoints]
  (rf/reg-sub ::screen-width get-screen-width)

  (rf/reg-sub ::screen
              :<- [::screen-width]
              (->get-screen breakpoints))

  (rf/reg-sub ::screen
              :<- [::screen-width]
              (->get-screen breakpoints))

  (let [screen-keys (some->> breakpoints
                             (map-indexed vector)
                             (filter (fn [[i k]]
                                       (even? i)))
                             (mapv second))]
    (doseq [screen-key screen-keys]
      (rf/reg-sub (keyword "breaking-point.core"
                           (str (name screen-key) "?"))
                  :<- [::screen]
                  (fn [screen _]
                    (= screen
                       screen-key))))))


;; FX

(defn set-breakpoints [{:keys [breakpoints
                               debounce-ms]
                        :as   opts}]
  (register-subs breakpoints)
  (rf/dispatch [::set-screen-width])
  (.addEventListener js/window "resize"
                     #(if debounce-ms
                        (rf/dispatch [::set-screen-width-debounced debounce-ms])
                        (rf/dispatch [::set-screen-width]))
                     true))

(rf/reg-fx
 ::set-breakpoints
 set-breakpoints)
