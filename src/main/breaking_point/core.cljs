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
 ::screen-dimensions
 (fn [coeffect]
   (let [screen-width  (or (some-> js/window
                                   .-innerWidth)
                           (some-> js/document
                                   .-documentElement
                                   .-clientWidth)
                           (some-> js/document
                                   .-body
                                   .-clientWidth))
         screen-height (or (some-> js/window
                                   .-innerHeight)
                           (some-> js/document
                                   .-documentElement
                                   .-clientHeight)
                           (some-> js/document
                                   .-body
                                   .-clientHeight))]
     (assoc coeffect
            :screen-width screen-width
            :screen-height screen-height))))


;; Events

(defn set-screen-dimensions
  [{:keys [db
           screen-width
           screen-height]} _]
  {:db (-> db
           (assoc-in [::breakpoints :screen-width] screen-width)
           (assoc-in [::breakpoints :screen-height] screen-height))})

(rf/reg-event-fx ::set-screen-dimensions
                 [(rf/inject-cofx ::screen-dimensions)]
                 set-screen-dimensions)

(rf/reg-event-fx ::set-screen-dimensions-debounced
                 (fn [_ [_ debounce-ms]]
                   {:dispatch-debounce [{:id      ::calcaulate-width-after-resize
                                         :timeout debounce-ms
                                         :action  :dispatch
                                         :event   [::set-screen-dimensions]}]}))


;; Subs

(defn get-screen-width [db _]
  (get-in db [::breakpoints :screen-width]))

(defn get-screen-height [db _]
  (get-in db [::breakpoints :screen-height]))


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


(defn get-orientation
  [[screen-width
    screen-height] _]
  (if (> screen-height
         screen-width)
    :portrait
    :landscape))


(defn register-subs [breakpoints]
  (rf/reg-sub ::screen-width get-screen-width)
  (rf/reg-sub ::screen-height get-screen-height)

  (rf/reg-sub ::screen
              :<- [::screen-width]
              (->get-screen breakpoints))

  (rf/reg-sub ::orientation
              :<- [::screen-width]
              :<- [::screen-height]
              get-orientation)

  (rf/reg-sub ::portrait?
              :<- [::orientation]
              (fn [orientation _]
                (= orientation :portrait)))

  (rf/reg-sub ::landscape?
              :<- [::orientation]
              (fn [orientation _]
                (= orientation :landscape)))

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
  (rf/dispatch [::set-screen-dimensions])
  (.addEventListener js/window "resize"
                     #(if debounce-ms
                        (rf/dispatch [::set-screen-dimensions-debounced debounce-ms])
                        (rf/dispatch [::set-screen-dimensions]))
                     true))

(rf/reg-fx
 ::set-breakpoints
 set-breakpoints)
