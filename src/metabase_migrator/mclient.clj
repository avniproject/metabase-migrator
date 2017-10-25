(ns metabase-migrator.mclient
  (:require [clj-http.client :as client]
            [metabase-migrator.migrator :refer [get-token]]))

(defn- auth-request
  [req]
  (-> req
      (assoc-in [:headers "X-METABASE-SESSION"] (get-token))))

(defn- wrap-auth
  "Middleware converting the :token option into an Metabase Authorization header."
  [client]
  (fn
    ([req]
     (client (auth-request req)))
    ([req respond raise]
     (client (auth-request req) respond raise))))

(defn- wrapped-method
  [method]
  (fn
    [& args]
    (client/with-additional-middleware [wrap-auth]
                                       (apply method args))))

(def get (wrapped-method client/get))
(def post (wrapped-method client/post))
(def delete (wrapped-method client/delete))
(def put (wrapped-method client/put))
(def as->json {:as :json})

(def default-opts (merge as->json))

(defn make-opts
  [& opts]
  (apply merge default-opts opts))