(ns jsm13.controllers.session
  (:require
   [jsm13.views.welcome :as welcome-views]))

(defn create [req]
  (let [params (:params req)
        username (:username params)] 
    (println params) 
    (println (str "username: " username))
    {:body (welcome-views/user-greeting-page username)
     :session {:username username}}))
