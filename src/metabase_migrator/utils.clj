(ns metabase-migrator.utils)

(defn find-in-coll
  [coll k v]
  (->> coll
       (filter #(= v (k %)))
       first))