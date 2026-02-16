(ns jsm13.handler 
  (:require
   [ring.util.response :as response]
   [hiccup2.core :as h]
   [hiccup.page :as page]
   [next.jdbc.sql :as sql]
   [starfederation.datastar.clojure.api :as d*]
   [starfederation.datastar.clojure.adapter.ring :as ds-ring]
   [reitit.ring :as ring]))

(defn layout
  [body options]
  (h/html 
   (page/doctype :html5) 
   [:html {:lang "en"}
    [:head
     [:meta {:charset "utf-8"}]
     [:title (or (:title options) "NPP")]
     [:link {:href "/assets/style.css" :rel "stylesheet"}]
     [:script {:type "module" :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@1.0.0-RC.7/bundles/datastar.js"}]]
    [:body body]]))


;; Borrowing heavily from 
;; https://github.com/prestancedesign/usermanager-reitit-example/blob/main/src/usermanager/handler.clj

(defn make-page-response [{:keys [body options]}]
  (-> (response/response (str (layout body options)))
      (response/content-type "text/html")))

(defn make-patch-response [body]
  (-> (response/response body)
      (response/content-type "text/event-stream")))

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
        (make-page-response result)))))

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

(def button [:button {:data-on:click "@get('/datastar')"} "d*"])
(defn message-area [message] (h/html [:div {:id "message-area"} message]))
(def button-area [:div
                  button
                  (message-area "")])

(defn plans-list [plans]
  (h/html
   [:h1 "Plans"]
   (if (seq plans)
     (make-list (map (fn [plan] (:plans/name plan)) plans))
     [:p "No current plans"])
   button-area))

(defn root-route-handler [req]
  (let [db (:db req)
        plans (sql/query db ["SELECT * FROM plans"])]
    {:body (plans-list plans)
     :options {:title "Plans"}}))

(defn datastar-handler [req]
  (ds-ring/->sse-response
   req
   {ds-ring/on-open
    (fn [sse-gen]
      (println (message-area "Hello, datastar"))
      (d*/patch-elements! sse-gen (str (message-area "Hello, datastar")))
      (Thread/sleep 2000)
      (d*/patch-elements! sse-gen (str (message-area "")))
      (d*/close-sse! sse-gen))}))

(defn app
  [db]
  (ring/ring-handler
   (ring/router
    [["/" {:handler root-route-handler}]
     ["/datastar" {:handler datastar-handler}]
     ["/assets/*" (ring/create-resource-handler)]]
    {:data {:db db
            :middleware [ensure-response-middleware
                         middleware-db]}})
   (ring/create-default-handler)))
