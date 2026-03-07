(ns jsm13.models.plan 
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]))

(defn find-many [db]
  (sql/query db ["SELECT * FROM plans"]))

(defn find-by-id [db id]
  (sql/get-by-id db :plans (parse-uuid id)))

(defn find-by-id-with-sections [db id]
  (let [results (jdbc/execute! db ["
                          select * from plans p
                          left join sections s 
                          on p.id  = s.plan_id
                          where p.id = ?", (parse-uuid id)])
        {:plans/keys [id name created_at]
         section_id :sections/id}  (first results)
        plan {:id id
              :name name
              :create-at created_at}]
    (assoc plan :sections (if (nil? section_id) [] 
                              (map (fn [section] 
                                     (let [{:sections/keys [id description created_at]} section] 
                                       {:id id 
                                        :description description 
                                        :created-at created_at}))
                                   results)))))

(defn create [db name]
  (sql/insert! db :plans {:name name}))

(defn delete [db id]
  (sql/delete! db :plans {:id (parse-uuid id)}))