(ns metabase-migrator.datasource
  (:require [metabase-migrator.mclient :as client]
            [metabase-migrator.utils :refer [find-in-coll]]
            [metabase-migrator.migrator :refer :all]))


(defn- ds-exists?
  [ds-name]
  (->> (client/get (uri "database") (client/make-opts))
       :body
       (some (fn [{name :name}] (= name ds-name)))
       some?))

(defn- load-all-ds
  []
  (->> (client/make-opts {:query-params {"include_tables" true}})
       (client/get (uri "database"))
       :body
       (pmap #(:body (client/get (uri "database" (str (:id %)) "metadata") (client/make-opts))))
       add-dss))

(defn- source-ds
  []
  (-> (dss)
      (find-in-coll :name (main-ds))
      (set-source)))

(defn init
  []
  (do
    (load-all-ds)
    (source-ds)))