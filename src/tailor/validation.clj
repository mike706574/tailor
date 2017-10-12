(ns tailor.validation
  (:require [clojure.spec.alpha :as s]))

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
    (let [conformed (s/conform spec item)]
      (if (= conformed ::s/invalid)
        (assoc item :data-errors (data-errors spec item))
        conformed))))

(defn conform-item
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

(defn conform
  ([item-spec value-specs]
   (map (partial conform-item item-spec value-specs)))
  ([item-spec value-specs items]
   (map (partial conform-item item-spec value-specs) items)))
