(ns jsm13.controllers.plans
  (:require [ring.util.response :as resp]
            [jsm13.models.plans :as plan-model]
            [jsm13.views.plans :as plan-views]))

(defn index [req]
  (let [{:keys [db session]} req
        plans (plan-model/find-many db)]
    (println session)
    {:body (plan-views/plans-index-page plans)
     :options {:title "Plans"}
     :session {:message "Hello"}}))

(defn create [req]
  (let [{:keys [params db]} req
        {:keys [name]} params
        result (plan-model/create db name)]
    (println "Plan created")
    (println result)
    (resp/redirect "/plans" :see-other)))

(defn delete [req]
  (let [{:keys [path-params db]} req
        {:keys [plan-id]} path-params]
    (plan-model/delete db plan-id)
    ;; TODO: respond w/ datastar response to update plan list
    ;; TODO: check headers if request is from datastar?
    (resp/status 204)))