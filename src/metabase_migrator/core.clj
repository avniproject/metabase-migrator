(ns metabase-migrator.core
  (:gen-class))


(defn- init-state
  "Setup required startup state"
  []
  (do
    (metabase-migrator.auth/login)
    (metabase-migrator.datasource/init)))

(defn -main
  "Main"
  [& args]
  (init-state))
