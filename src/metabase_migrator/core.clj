(ns metabase-migrator.core
  (:require [metabase-migrator.auth :as auth]
            [metabase-migrator.datasource :as datasource])
  (:gen-class))


(defn- init-state
  "Setup required startup state"
  []
  (do
    (auth/login)
    (datasource/init)))

(defn -main
  "Main"
  [& args]
  (init-state))