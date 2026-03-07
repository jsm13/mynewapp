(ns jsm13.models.section 
  (:require
   [next.jdbc.sql :as sql]))

(defn create-plan-section [db plan-id description]
  (sql/insert! db :sections 
               ;; TODO: add REQUIRED constraint to section plan_id column
               {:plan_id (parse-uuid plan-id)
                :description description}))