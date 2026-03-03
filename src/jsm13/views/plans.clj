(ns jsm13.views.plans 
  (:require
   [hiccup2.core :as h]))

(def form
  (h/html
   [:form {:data-on:submit "@post('/plans', {contentType: 'form'})"
           :data-on:datastar-fetch "evt.detail.type === 'finished' && el.reset()"}
    [:h2 "New plan form"]
    [:label "Name:"
     [:input {:type "text" :name "name" :required true}]]
    [:button "Submit"]]))

(defn list-item [plan]
  (let [{:plans/keys [name id]} plan
        delete-path (str "/plans/" id)
        delete-action (str "@delete(\"" delete-path "\")")]
    (h/html [:li name
             [:button 
              {:data-on:click delete-action} 
              "x"]])))

(defn plans-list [plans]
  (h/html
   [:ul
    (map list-item plans)]))

(defn plans-index-page [plans]
  (h/html
   [:h1 "Plans"]
   (if (seq plans)
     (plans-list plans)
     [:p "No current plans"])
   form))
