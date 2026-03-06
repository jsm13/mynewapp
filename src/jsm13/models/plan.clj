(ns jsm13.models.plan 
  (:require
   [next.jdbc.sql :as sql]))

(defn find-many [db]
  (sql/query db ["SELECT * FROM plans"]))

(defn find-by-id [db id]
  (sql/get-by-id db :plans (parse-uuid id)))

(defn create [db name]
  (sql/insert! db :plans {:name name}))

(defn delete [db id]
  (sql/delete! db :plans {:id (parse-uuid id)}))