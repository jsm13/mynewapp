(ns jsm13.middleware 
  (:require
   [jsm13.views.common :as common-views]
   [ring.util.response :as response]))

(defn make-page-response [{:keys [body options session]}]
  (-> (response/response (str (common-views/layout body options))) 
      (assoc :session session)
      (response/content-type "text/html")))

(defn ensure-response
  "This middleware runs before and after every request
   If the handler returns an HTTP response (like a redirect),
   the result is returned directly.
   Otherwise the handlers result is passed to the page renderer"
  [handler]
  (fn [req]
    (let [result (handler req)]
      (if (response/response? result)
        result
        (make-page-response result)))))

(def db
  {:name ::db
   :compile (fn [{:keys [db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :db db)))))})