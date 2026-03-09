(ns jsm13.views.plan 
  (:require
   [hiccup2.core :as h]
   [starfederation.datastar.clojure.api :as dsapi]))

(def form
  (h/html
   [:form {:id "new-plan-form"
           :data-on:submit "@post('/plans', {contentType: 'form'})"
           :data-indicator "fetching"}
    [:h2 "New plan form"]
    [:label "Name:"
     [:input {:type "text" :name "name" :required true}]]
    [:button "Submit"]
    [:div {:data-show "$fetching"}
     [:p "Loading..."]]]))

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
  (let [{:keys [name id sections]} plan
        plan-url (str "/plans/" id)
        plan-section-url (str plan-url "/sections")]
    (h/html
     [:main {:id "main" :data-init (dsapi/sse-get plan-url)}
      [:h1 name]
      [:ul (map 
            (fn [{:keys [id description]}]
              (let [section-url (str "/sections/" id)]
                [:li description
                 [:button {:data-on:click (dsapi/sse-delete section-url)} "x"]])) 
            sections)]
      [:div
       [:h2 "Add section"]
       [:form {:id "new-section-form"
               :data-on:submit (dsapi/sse-post plan-section-url "{contentType: 'form'}")}
        [:label "Description"
         [:input {:type "text "
                  :name "description"
                  :required true}]]
        [:button "Submit"]]]])))