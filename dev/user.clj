(ns user
  (:require [hikari-cp.core :as hc]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]))

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


(comment
  (def db (start-db))
  (stop-db db))
