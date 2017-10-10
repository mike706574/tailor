(ns tailor.validation
  (:require [clojure.spec.alpha :as s]))

(defn tag [x]
  (cond (qualified-ident? x) (str (namespace x) "/" (name x))
                      (ident? x) (name x)
                      :else (str x)))
(defn value-error
  [id spec value problem]
  (merge problem {:key id
                  :tag (tag spec)
                  :val value}))

(defn process-value
  [spec id value]
  (let [conformed (s/conform spec value)]
    (if (= conformed ::s/invalid)
      (let [spec-problems (::s/problems (s/explain-data spec value))
            data-errors (mapv (partial value-error id spec value) spec-problems)]
        {:data-errors data-errors})
      {:value value})))

(defn process-entry
  [item [id spec]]
  (let [value (get item id)
        {:keys [value data-errors]} (process-value spec id value)]
    (if data-errors
      (update item :data-errors #(into % data-errors))
      (assoc item id value))))

(defn validate-item-with-specs
  [specs item]
  (if (:data-errors item)
    item
    (reduce process-entry item specs)))

(defn validate-with-specs
  ([specs]
   (map (partial validate-item-with-specs specs)))
  ([specs items]
   (map (partial validate-item-with-specs specs) items)))

(defn item-error
  [problem]
  (let [{:keys [in via]} problem]
    (assoc problem :key (last in) :tag (tag (last via)))))

(defn validate-item
  [spec item]
  (if (:data-errors item)
    item
    (let [conformed (s/conform spec item)]
      (if (= conformed ::s/invalid)
        (let [spec-problems (::s/problems (s/explain-data spec item))
              item-errors (mapv item-error spec-problems)]
          (assoc item :data-errors item-errors))
        conformed))))

(defn validate
  ([spec]
   (map (partial validate-item spec)))
  ([spec items]
   (map (partial validate-item spec) items)))
