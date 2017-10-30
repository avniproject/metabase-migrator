(ns metabase-migrator.table
  (:require [metabase-migrator.utils :refer [find-in-coll]]
            [print.foo :refer [look]]))



(defn- id->name
  [fields id]
  (->> id
       (find-in-coll fields :id)
       :name))

(defn- name->id
  [fields name]
  (->> name
       (find-in-coll fields :name)
       :id))

(defn- id<->name-fn
  [source dest]
  (fn
    [source-id]
    (->> source-id
         (id->name source)
         (name->id dest))))

(defn- field-id->column-id-fn
  [fields]
  (fn
    [field-id]
    (->> field-id
         (find-in-coll fields :id)
         :raw_column_id)))

(defn- migrate-dimension
  [source field->id<->name table->id<->name field-id->column-id]
  (let [{fid :field_id hrfid :human_readable_field_id} source
        tfid (field->id<->name fid)
        thrfid (field->id<->name fid)]
    (assoc source :field_id (field->id<->name fid)
                  :human_readable_field_id thrfid)))


(defn- migrate-target
  [source field->id<->name table->id<->name field-id->column-id]
  (let [{tid :table_id fid :id} source
        tfid (field->id<->name fid)
        ttid (table->id<->name tid)]
    (assoc source :table_id ttid
                  :id (field->id<->name fid)
                  :raw_column_id (field-id->column-id tfid))))

(defn- migrate-field
  [field->id<->name table->id<->name field-id->column-id]
  (fn
    [source dest]
    (let [{source-special-type :special_type source-fk-target-field-id :fk_target_field_id source-dimension :dimensions source-target :target} source
          {dest-special-type :special_type dest-fk-target-field-id :fk_target_field_id dest-dimension :dimensions dest-target :target} dest]
      (assoc dest :fk_target_field_id (field->id<->name source-fk-target-field-id)
                  :special_type source-special-type
                  (when (not-empty source-target)
                    :target (migrate-target source-target field->id<->name table->id<->name field-id->column-id))
                  (when (not-empty source-dimension)
                    :dimensions (migrate-dimension source-dimension field->id<->name table->id<->name field-id->column-id))))))

(defn migrate-table
  [source-tables dest-tables]
  (fn
    [source dest]
    (let [source-fields (:fields source)
          dest-fields (:fields dest)
          field->id<->name (id<->name-fn source-fields dest-fields)
          field-id->column-id (field-id->column-id-fn dest-fields)
          table->id<->name (id<->name-fn source-tables dest-tables)]
      (->> (pmap (migrate-field field->id<->name table->id<->name field-id->column-id) source-fields dest-fields)
           (assoc dest :fields)))))