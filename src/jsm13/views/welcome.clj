(ns jsm13.views.welcome 
  (:require
   [hiccup2.core :as h]))

(def link-to-plans
  (h/html [:a {:href "/plans"} "See plans"]))

(def welcome-page
  (h/html
   [:h1 "Welcome"]
   [:div
    [:form {:data-on:submit "@post('/login', { contentType: 'form' })"}
     [:label "Enter a username to continue:"
      [:input {:type "text" :name "username"}]]]
    link-to-plans]))

(defn user-greeting-page [username]
  (h/html
   [:h1 (str "Welcome " username)]
   link-to-plans))