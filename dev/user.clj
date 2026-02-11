(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-state]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [jsm13.system :as system])
  (:import (com.zaxxer.hikari HikariDataSource)))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))

(comment
  (ig-repl/reset)
  (let [^HikariDataSource ds (:app/datasource ig-state/system)]
    (.close (jdbc/get-connection ds))
    (sql/query ds ["SELECT * FROM plans"]))
  )
