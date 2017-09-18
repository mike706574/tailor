(ns tailor.validation
  (:require [clojure.spec.alpha :as s]))

(defn item-error [problem]
  (-> problem
      (select-keys [:in :pred :val])
      (update :in first)
      (update :pred (fn [pred]
                      (let [sym (if (sequential? pred)
                                  (last pred)
                                  pred)]
                        (name sym))))))

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
