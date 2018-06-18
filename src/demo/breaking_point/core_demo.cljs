(ns breaking-point.core_demo
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [breaking-point.events :as events]
            [breaking-point.routes :as routes]
            [breaking-point.views :as views]
            [breaking-point.config :as config]
            [breaking-point.core :as rb]
            ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [::rb/set-breakpoints
                           {:breakpoints [:mobile
                                          768
                                          :tablet
                                          992
                                          :small-monitor
                                          1200
                                          :large-monitor]
                            ;; :debounce-ms 166
                            }])
  (routes/app-routes)
  (dev-setup)
  (mount-root))
