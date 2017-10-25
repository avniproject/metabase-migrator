(ns metabase-migrator.migrator
  (:require [environ.core :refer [env]]
            [clojure.string :refer [join]]))

(def ^:private state (atom {:uri             (env :metabase-uri "http://locahost:3000")
                            :username        (env :metabase-username "admin@openchs.org")
                            :password        (env :metabase-password "passw0rd")
                            :token           nil
                            :main            (env :metabase-main-datasource "OpenCHS Main")
                            :datasources     []
                            :main-datasource nil}))


(defn- set-state [key val]
  (swap! state assoc key val)
  nil)

(def credentials (select-keys @state [:username :password]))

(defn uri
  [& args]
  (join "/" (concat [(:uri @state) "api"] args)))

(defn set-token
  [token]
  (set-state :token token))

(defn token
  []
  (:token @state))

(defn token-exists?
  []
  (-> @state
      :token
      nil?
      not))

(defn add-dss
  [dss]
  (set-state :datasources dss)
  dss)

(defn dss
  []
  (:datasources @state))

(defn main-ds
  []
  (:main @state))

(defn set-source
  [ds]
  (set-state :main-datasource ds)
  ds)