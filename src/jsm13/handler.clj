(ns jsm13.handler 
  (:require
   [hiccup.page :as page]
   [hiccup2.core :as h]
   [next.jdbc.sql :as sql]
   [reitit.ring :as ring]
   [reitit.ring.middleware.parameters :as parameters-middleware]
   [ring.middleware.session :as session]
   [ring.middleware.keyword-params :as keyword-params]
   [ring.util.request :as request]
   [ring.util.response :as response] 
   [starfederation.datastar.clojure.adapter.ring :as ds-ring]
   [starfederation.datastar.clojure.api :as d*]))

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

(defn make-page-response [{:keys [body options session]}]
  (-> (response/response (str (layout body options))) 
      (assoc :session session)
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

(defn plans-route-handler [req]
  (let [{:keys [db session]} req
        plans (sql/query db ["SELECT * FROM plans"])]
    (println session)
    {:body (plans-list plans)
     :options {:title "Plans"}
     :session {:message "Hello"}}))

(def welcome-page
  (h/html
   [:h1 "Welcome"]
   [:div
    [:form {:data-on:submit "@post('/login', { contentType: 'form' })"}
     [:label "Enter a username to continue:"
      [:input {:type "text" :name "username"}]]]]))

(defn user-greeting-page [username]
  (h/html
   [:h1 (str "Welcome " username)]))

(defn root-route-handler [req]
  (let [{:keys [session]} req
        username (:username session)]
    (println session)
    {:body (if username (user-greeting-page username) welcome-page)
     :options {:title "Welcome"}}))

(defn datastar-handler [req]
  (ds-ring/->sse-response
   req
   {ds-ring/on-open
    (fn [sse-gen]
      (println (message-area "Hello, datastar"))
      (d*/patch-elements! sse-gen (str (message-area "Hello, datastar!!")))
      (Thread/sleep 2000)
      (d*/patch-elements! sse-gen (str (message-area "")))
      (d*/close-sse! sse-gen))}))

(defn login-route-handler [req]
  (let [params (:params req)
        username (:username params)] 
    (println params) 
    (println (str "username: " username))
    {:body (user-greeting-page username)
     :session {:username username}}))

(defn app
  [db]
  (ring/ring-handler
   (ring/router
    [["/" {:handler root-route-handler}]
     ["/plans" {:handler plans-route-handler}]
     ["/login" {:post {:handler login-route-handler}}]
     ["/datastar" {:handler datastar-handler}]
     ["/assets/*" (ring/create-resource-handler)]]
    {:data {:db db
            :middleware [parameters-middleware/parameters-middleware
                         keyword-params/wrap-keyword-params
                         ensure-response-middleware
                         middleware-db]}})
   (ring/create-default-handler)
   {:middleware [session/wrap-session]}))
