(ns jsm13.system
  (:require [ring.util.response :as response]
            [jsm13.config :as config]
            [jsm13.server :as server]
            [jsm13.db :as db]
            [integrant.core :as ig]))


(def system 
  {:app/config {}
   :app/datasource {:config (ig/ref :app/config)}
   :adapter/jetty {:config (ig/ref :app/config) 
                   :handler (ig/ref :handler/greet)}
   :handler/greet {:name "jsm"}})

(defmethod ig/init-key :app/config [_ _]
  (config/load-config))

(defmethod ig/init-key :app/datasource [_ {:keys [config]}]
  (let [{:keys [postgres]} config
        {:keys [username password database-name]} postgres
        pool-conf {:dbtype "postgres"
                   :dbname database-name
                   :username username
                   :password password}]
    (db/create-connection-pool pool-conf)))

(defmethod ig/halt-key! :app/datasource [_ connection-pool]
  (db/disconnect-connection-pool connection-pool))

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler config] :as opts}] 
  (server/start-server handler (-> opts
                               (dissoc :handler)
                               (assoc :port (:port config))
                               (assoc :join? false))))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (fn [_] (response/response (str "Hello " name))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defn start-system [args] 
  (println "Starting system")
  (when (seq args) (println (str "With args: " args " (ignored)")))
  (ig/init system))

(defn stop-system [system] (ig/halt! system))
