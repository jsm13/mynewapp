(ns jsm13.server
  (:require [ring.adapter.jetty :as jetty]))

(defn start-server [handler opts]
  (jetty/run-jetty handler opts))