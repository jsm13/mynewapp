(ns jsm13.db
  (:require [next.jdbc.connection :as connection])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn create-connection-pool [config]
  (connection/->pool HikariDataSource config))

(defn disconnect-connection-pool [^HikariDataSource connection]
  (.close connection))
