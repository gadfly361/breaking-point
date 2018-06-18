(ns breaking-point.events
  (:require
   [re-frame.core :as rf]
   [breaking-point.db :as db]
   ))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(rf/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))
