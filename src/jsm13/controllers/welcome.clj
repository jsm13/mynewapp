(ns jsm13.controllers.welcome
  (:require 
   [jsm13.views.welcome :as welcome-views]))

(defn show [req]
  (let [{:keys [session]} req
        username (:username session)]
    (println session)
    {:body (if username (welcome-views/user-greeting-page username) welcome-views/welcome-page)
     :options {:title "Welcome"}}))

