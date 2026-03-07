(ns jsm13.controllers.section 
  (:require
   [ring.util.response :as resp]
   [jsm13.models.section :as section-model]))

(defn delete [req]
  (let [{:keys [db path-params]} req
        {:keys [section-id]} path-params]
    (section-model/delete db section-id)
    (resp/status 204)))

(defn create [req]
  (let [{:keys [db path-params params]} req
        {:keys [plan-id]} path-params
        {:keys [description]} params]
    (section-model/create-plan-section db plan-id description)
    (resp/status 201)))