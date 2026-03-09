(ns jsm13.controllers.plan
  (:require [starfederation.datastar.clojure.api :as d*]
            [starfederation.datastar.clojure.adapter.ring :as dsa]
            [ring.util.response :as resp]
            [jsm13.models.plan :as plan-model]
            [jsm13.views.plan :as plan-views]))

(defn is-datastar-req [req]
  (-> req 
      (:headers) 
      (get "datastar-request" "false") 
      (parse-boolean)))

(defn index [req]
  (let [{:keys [db]} req
        is-ds-req (is-datastar-req req)]
    (if-not is-ds-req
      (let [plans (plan-model/find-many db)
            plans-index-resource (plan-views/plan-index-resource plans)]
        {:body plans-index-resource
         :options {:title "Plans"}
         :session {:message "Hello"}})
      (dsa/->sse-response req {dsa/on-open (fn [sse-gen]
                                             (println "Open SSE stream for plan index")
                                             (loop []
                                               (let [plans (plan-model/find-many db)
                                                     plans-resource (str (plan-views/plan-index-resource plans))]
                                                 (when-not (Thread/interrupted)
                                                   (d*/patch-elements! sse-gen plans-resource)
                                                   (Thread/sleep 250)
                                                   (recur)))))
                               dsa/on-close (fn [] (println "Plans Controller Index Action SSE Connection closed"))}))))

(defn show [req]
  (let [{:keys [db path-params]} req
        {:keys [plan-id]} path-params
        is-ds-req (is-datastar-req req)]
    (if-not is-ds-req
      (let [plan (plan-model/find-by-id-with-sections db plan-id)
            plans-show-resource (plan-views/plan-show-resource plan)]
        {:body plans-show-resource
         :options {:title (:plans/name plan)}})
      (dsa/->sse-response req {dsa/on-open (fn [sse-gen]
                                             (loop []
                                               (let [plan (plan-model/find-by-id-with-sections db plan-id)
                                                     plan-show-resource (str (plan-views/plan-show-resource plan))]
                                                 (when-not (Thread/interrupted)
                                                   (d*/patch-elements! sse-gen plan-show-resource)
                                                   (Thread/sleep 250)
                                                   (recur)))))
                               dsa/on-close (fn [] (println "Plans Controller Show Action SSE Connection closed"))}))))

(defn create [req]
  (let [{:keys [params db]} req
        {:keys [name]} params
        plan (plan-model/create db name)]
    (println "Plan created")
    (println plan)
    (-> (resp/response (str "Plan " (:plans/id plan) " created"))
        (resp/header "datastar-selector" "#new-plan-form")
        (resp/header "Content-Type" "text/html"))))

(defn delete [req]
  (let [{:keys [path-params db]} req
        {:keys [plan-id]} path-params]
    (plan-model/delete db plan-id)
    (resp/status 204)))