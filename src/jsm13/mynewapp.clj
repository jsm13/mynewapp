(ns jsm13.mynewapp
  (:require [jsm13.system :as system])
  (:gen-class))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [running-system (system/start-system args)]
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(system/stop-system running-system)))))

(comment
  )