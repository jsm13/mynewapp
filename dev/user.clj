(ns user
  (:require [hikari-cp.core :as hc]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection])
  (:import (io.github.cdimascio.dotenv Dotenv)))

(def datasource-options {:adapter "postgresql"
                         :database-name "postgres"
                         :username "postgres"
                         :password "postgres"})

(defn start-db
  []
  (connection/->pool 'hikari-cp datasource-options))

(defn stop-db
  [datasource]
  (hc/close-datasource datasource))

(defn start-env
  []
  (Dotenv/load))

(comment
  (start-env)
  (with-open [pool (start-db)
              ds (jdbc/get-connection pool)] 
    (let [rows (jdbc/execute! ds ["SELECT 1 + 3"])]
      (println rows)))
  )
