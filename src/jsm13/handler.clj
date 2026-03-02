(ns jsm13.handler 
  (:require
   [reitit.ring :as ring]
   [reitit.ring.middleware.parameters :as parameters-middleware]
   [ring.middleware.session :as session]
   [ring.middleware.keyword-params :as keyword-params]
   [jsm13.controllers.welcome :as welcome-controller]
   [jsm13.controllers.session :as session-controller]
   [jsm13.controllers.plans :as plan-controller]
   [jsm13.middleware :as middleware]))


;; Borrowing heavily from 
;; https://github.com/prestancedesign/usermanager-reitit-example/blob/main/src/usermanager/handler.clj

(defn app
  [db]
  (ring/ring-handler
   (ring/router
    [["/" {:handler welcome-controller/show}]
     ["/plans" {:get {:handler plan-controller/index}
                :post {:handler plan-controller/create}}]
     ["/plans/:plan-id" {:delete {:handler plan-controller/delete}}]
     ["/login" {:post {:handler session-controller/create}}]
     ["/assets/*" (ring/create-resource-handler)]]
    {:data {:db db
            :middleware [parameters-middleware/parameters-middleware
                         keyword-params/wrap-keyword-params
                         middleware/ensure-response
                         middleware/db]}})
   (ring/create-default-handler)
   ;; wrap-session to be added as ring middleware rather than
   ;; reitit middleware to avoid creating a memory store per route
   ;; https://github.com/metosin/reitit/issues/205
   {:middleware [session/wrap-session]}))
