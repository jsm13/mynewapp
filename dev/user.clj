(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-state]
            [next.jdbc :as jdbc]
            [jsm13.system :as system])
  (:import (com.zaxxer.hikari HikariDataSource)))

(ig-repl/set-prep! #(ig/expand system/system (ig/deprofile [:dev])))

(comment
  (ig-repl/go)
  (let [^HikariDataSource ds (:app/datasource ig-state/system)]
    (.close (jdbc/get-connection ds))
    (jdbc/execute! ds ["SELECT * FROM plans"]))
  )
