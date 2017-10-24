(ns metabase-migrator.datasource
  (:require [metabase-migrator.mclient :as client]
            [metabase-migrator.migrator :refer :all]))


(defn- ds-exists?
  [ds-name]
  (->> (client/get (uri "database") client/opts)
       :body
       (some (fn [{name :name}] (= name ds-name)))
       some?))

(defn- source-ds
  [ds-name]
  (-> ds-name
      (client/get (uri "database") client/opts)
      :body))

(defn- create-ds
  [name {host :host port :port user :user password :password :as details}]
  )

(defn- load-all-ds
  []
  (-> (client/get (uri "database") client/opts)
      :body))