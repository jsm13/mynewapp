(ns jsm13.config
  (:require [clojure.java.io :as io]
            [aero.core :as aero]))


(defn load-config
  []
  (-> "config.edn"
      (io/resource)
      (aero/read-config)))