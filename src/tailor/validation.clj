(ns tailor.validation
  (:require [clojure.spec.alpha :as s]))

(defn item-problem [problem]
  (-> problem
      (dissoc :path :via)
      (update :in first)
      (update :pred (fn [pred]
                      (let [sym (if (sequential? pred)
                                  (last pred)
                                  pred)]
                        (name sym))))))

(defn item-error [spec item]
  (let [problems (::s/problems (s/explain-data spec item))]
    (mapv item-problem problems)))

(defn validate-item
  [spec item]
  (if (:format-error item)
    item
    (let [conformed (s/conform spec item)]
      (if (= conformed ::s/invalid)
        (assoc item :item-error (item-error spec item))
        conformed))))

(defn validate
  ([spec]
   (map (partial validate-item spec)))
  ([spec items]
   (map (partial validate-item spec) items)))
