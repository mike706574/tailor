(ns tailor.validation
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::id (s/or :keyword keyword?
                  :string (s/and string? (complement str/blank?))))
(s/def ::spec qualified-keyword?)
(s/def ::value-specs (s/map-of ::id ::spec))

(defn ^:private data-errors
  [spec item]
  (when spec
    (when-let [explain-data (s/explain-data spec item)]
      (mapv #(assoc % :key (last (:in %))) (::s/problems explain-data)))))

(defn ^:private process-value
  [spec id value]
  (let [conformed-value (s/conform spec value)]
    (if (= conformed-value ::s/invalid)
      (let [data-errors (mapv #(assoc % :key id :val value) (data-errors spec value))]
        {:data-errors data-errors})
      {:value conformed-value})))

(defn ^:private process-entry
  [item [id spec]]
  (let [value (get item id)
        {:keys [value data-errors]} (process-value spec id value)]
    (if data-errors
      (update item :data-errors #(into % data-errors))
      (assoc item id value))))

(defn validate-item
  [spec item]
  (if (:data-errors item)
    item
    (if-let [data-errors (data-errors spec item)]
      (assoc item :data-errors data-errors)
      item)))

(defn validate-item
  [spec item]
  (if (:data-errors item)
    item
    (if-let [data-errors (data-errors spec item)]
      (assoc item :data-errors data-errors)
      item)))
(defn conform-and-validate-item
  [item-spec value-specs item]
  (if (:data-errors item)
    item
    (let [conformed-item (reduce process-entry item value-specs)]
      (if (:data-errors conformed-item)
        conformed-item
        (if item-spec
          (validate-item item-spec conformed-item)
          conformed-item)))))

(defn validate
  ([spec]
   (map (partial validate-item spec)))
  ([spec items]
   (map (partial validate-item spec) items)))

(s/fdef validate
  :args (s/or :xform (s/cat :spec ::spec)
              :items (s/cat :spec ::spec :items (s/coll-of map?)))
  :ret (s/or :xform fn?
             :result (s/coll-of map?)))

(defn conform-and-validate
  ([item-spec value-specs]
   (map (partial conform-and-validate-item item-spec value-specs)))
  ([item-spec value-specs items]
   (map (partial conform-and-validate-item item-spec value-specs) items)))

(s/fdef conform-and-validate
  :args (s/or :xform (s/cat :item-spec ::spec :value-specs ::value-specs)
              :items (s/cat :item-spec ::spec :value-specs ::value-specs :items (s/coll-of map?)))
  :ret (s/or :xform fn?
             :result (s/coll-of map?)))
