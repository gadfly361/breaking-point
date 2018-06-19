(ns breaking-point.views
  (:require
   [re-frame.core :as rf]
   [breaking-point.subs :as subs]
   [breaking-point.events :as events]
   [breaking-point.core :as rb]
   ))



;; home

(defn home-panel []
  (let [name           @(rf/subscribe [::subs/name])
        screen-width   @(rf/subscribe [::rb/screen-width])
        screen-height  @(rf/subscribe [::rb/screen-height])
        orientation    @(rf/subscribe [::rb/orientation])
        screen         @(rf/subscribe [::rb/screen])
        portrait?      @(rf/subscribe [::rb/portrait?])
        landscape?     @(rf/subscribe [::rb/landscape?])
        mobile?        @(rf/subscribe [::rb/mobile?])
        tablet?        @(rf/subscribe [::rb/tablet?])
        small-monitor? @(rf/subscribe [::rb/small-monitor?])
        large-monitor? @(rf/subscribe [::rb/large-monitor?])
        ]
    [:div {:style {:display "flex"}}
     [:div
      [:h2 (str "screen-width " screen-width)]
      [:h2 (str "screen " screen)]
      [:h2
       {:style {:color (when mobile? "green")}}
       (str "mobile? " mobile?)]
      [:h2
       {:style {:color (when tablet? "green")}}
       (str "tablet? " tablet?)]
      [:h2
       {:style {:color (when small-monitor? "green")}}
       (str "small-monitor? " small-monitor?)]
      [:h2
       {:style {:color (when large-monitor? "green")}}
       (str "large-monitor? " large-monitor?)]
      ]

     [:div {:style {:margin-left "200px"}}
      [:h2 (str "screen-height " screen-height)]
      [:h2 (str "orientation " orientation)]
      [:h2
       {:style {:color (when landscape? "green")}}
       (str "landscape? " landscape?)]
      [:h2
       {:style {:color (when portrait? "green")}}
       (str "portrait? " portrait?)]
]
     ]))


;; about

(defn about-panel []
  [:div "This is the About Page."
   [:div [:a {:href "#/"} "go to Home Page"]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel  [home-panel]
    :about-panel [about-panel]
    [:div]))


(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (rf/subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
