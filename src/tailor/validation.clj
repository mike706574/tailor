(ns tailor.validation  (:require [clojure.spec.alpha :as s]))

(defn item-error
  ([id problem]
   (assoc (item-error problem) :in id))
  ([problem]
   (-> problem
       (select-keys [:pred :val :in])
       (update :pred (fn [pred]
                       (let [sym (if (sequential? pred)
                                   (last pred)
                                   pred)]
                         (name sym)))))))

(defn process-value
  [spec id value]
  (let [conformed (s/conform spec value)]
    (if (= conformed ::s/invalid)
      {:data-errors (->> (s/explain-data spec value)
                         (::s/problems)
                         (map (partial item-error id)))}
      {:val value})))

(defn process-entry
  [item [id spec]]
  (if-not spec
    item
    (let [value (get item id)
          {:keys [val data-errors]} (process-value spec id value)]
      (if data-errors
        (update item :data-errors #(into % data-errors))
        (assoc item id val)))))

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
