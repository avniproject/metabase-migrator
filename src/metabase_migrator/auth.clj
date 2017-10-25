(ns metabase-migrator.auth
  (:require [clj-http.client :as client]
            [metabase-migrator.mclient :as mclient]
            [metabase-migrator.migrator :refer [uri credentials set-token token-exists? token]]))

(defn is-logged-in?
  [& _]
  (token-exists?))

(defn login
  []
  (-> (client/post (uri "session")
                   {:form-params  credentials
                    :content-type :json
                    :as           :json})
      :body
      :id
      set-token
      is-logged-in?))

(defn logout
  []
  (do (mclient/delete (uri "session")
                      {:form-params {:session_id (token)}})
      (set-token nil)))