(ns jsm13.views.plan 
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
        resource-path (str "/plans/" id)
        delete-action (str "@delete(\"" resource-path "\")")]
    (h/html [:li 
             [:a {:href resource-path} name]
             [:button 
              {:data-on:click delete-action} 
              "x"]])))

(defn plans-list [plans]
  (h/html
   [:ul
    (map list-item plans)]))

(defn plan-index-resource [plans]
  (h/html
   [:main {:data-init "@get('/plans')" :id "main"}
    [:h1 "Plans"]
    (if (seq plans)
      (plans-list plans)
      [:p "No current plans"])
    form]))

(defn plan-show-resource [plan]
  (let [{:plans/keys [name id]} plan
        plan-url (str "/plans/" id)
        get-plan-action (str "@get('" plan-url "')")]
    (h/html
     [:main {:id "main" :data-init get-plan-action}
      [:h1 name]])))