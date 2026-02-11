(ns jsm13.handler 
  (:require
   [ring.util.response :as response]
   [hiccup2.core :as h]
   [hiccup.page :as page]
   [next.jdbc.sql :as sql]
   [reitit.ring :as ring]))

(defn layout
  [body]
  (h/html 
   (page/doctype :html5) 
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:title "Hello world"]]
    [:body body]]))


;; Borrowing heavily from 
;; https://github.com/prestancedesign/usermanager-reitit-example/blob/main/src/usermanager/handler.clj

(defn make-response [body]
  (-> (response/response (str (layout body)))
      (response/content-type "text/html")))

(defn ensure-response-middleware
  "This middleware runs before and after every request
   If the handler returns an HTTP response (like a redirect),
   the result is returned directly.
   Otherwise the handlers result is passed to the page renderer"
  [handler]
  (fn [req]
    (let [result (handler req)]
      (if (response/response? result)
        result
        (make-response result)))))

(def middleware-db
  {:name ::db
   :compile (fn [{:keys [db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :db db)))))})

(defn make-list [list-items]
  (h/html
   [:ul
    (map (fn [item] [:li item]) 
         list-items)]))

(defn plans-list [plans]
  (if (seq plans)
    (list 
     [:h1 "Plans"]
     (make-list (map (fn [plan] (:plans/name plan)) plans)))
    (list
     [:h1 "Plans"]
     [:p "No current plans"])))

(defn root-route-handler [req]
  (let [db (:db req)
        plans (sql/query db ["SELECT * FROM plans"])]
    (plans-list plans)))

(defn app
  [db]
  (ring/ring-handler
   (ring/router
    [["/" {:handler root-route-handler}]]
    {:data {:db db
            :middleware [ensure-response-middleware middleware-db]}})))
