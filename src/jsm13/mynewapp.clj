(ns jsm13.mynewapp
  (:require [jsm13.system :as system])
  (:gen-class))

(defn -main
  "I start the system"
  [& args]
  (let [running-system (system/start-system args)]
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(system/stop-system running-system)))))
