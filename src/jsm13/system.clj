(ns jsm13.system
  (:require [integrant.core :as ig]
            [jsm13.config :as config]
            [jsm13.db :as db]
            [jsm13.handler :as handler]
            [jsm13.server :as server]))


(def system 
  {:app/config {}
   :app/datasource {:config (ig/ref :app/config)}
   :adapter/jetty {:config (ig/ref :app/config) 
                   :handler (ig/ref :app/handler)}
   :app/handler {:db (ig/ref :app/datasource)}})

(defmethod ig/init-key :app/config [_ _]
  (config/load-config))

;; TODO: config could supply values to the system so dependencies
;; could be expressed in terms of specific values instead of requiring
;; and needing to unpack all of config
(defmethod ig/init-key :app/datasource [_ {:keys [config]}]
  (let [{:keys [postgres]} config
        {:keys [username password database-name host]} postgres
        pool-conf {:dbtype "postgres"
                   :host host
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

(defmethod ig/init-key :app/handler [_ {:keys [db]}]
  (handler/app db))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defn start-system [args] 
  (println "Starting system")
  (when (seq args) (println (str "With args: " args " (ignored)")))
  (ig/init system))

(defn stop-system [system] (ig/halt! system))
