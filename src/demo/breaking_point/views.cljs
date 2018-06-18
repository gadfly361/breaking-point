(ns breaking-point.views
  (:require
   [re-frame.core :as rf]
   [breaking-point.subs :as subs]
   [breaking-point.events :as events]
   [breaking-point.core :as rb]
   ))



;; home

(defn home-panel []
  (let [name    @(rf/subscribe [::subs/name])
        screen-width @(rf/subscribe [::rb/screen-width])
        screen @(rf/subscribe [::rb/screen])
        mobile? @(rf/subscribe [::rb/mobile?])
        tablet? @(rf/subscribe [::rb/tablet?])
        small-monitor? @(rf/subscribe [::rb/small-monitor?])
        large-monitor? @(rf/subscribe [::rb/large-monitor?])
        ]
    [:div
     #_(str "Hello from " name ". This is the Home Page.")

     [:h1 (str "screen-width " screen-width)]
     [:h1 (str "screen " screen)]
     [:h1 (str "mobile? " mobile?)]
     [:h1 (str "tablet? " tablet?)]
     [:h1 (str "small-monitor? " small-monitor?)]
     [:h1 (str "large-monitor? " large-monitor?)]


     #_[:div [:a {:href "#/about"} "go to About Page"]]
     ]))


;; about

(defn about-panel []
  [:div "This is the About Page."
   [:div [:a {:href "#/"} "go to Home Page"]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :about-panel [about-panel]
    [:div]))


(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
