(ns tailor.validation
  (:require [clojure.spec.alpha :as s]))

(defn data-problem [problem]
  (-> problem
      (dissoc :path :via)
      (update :in first)
      (update :pred (fn [pred]
                      (let [sym (if (sequential? pred)
                                  (last pred)
                                  pred)]
                        (name sym))))))

(defn data-error [spec data]
  (let [problems (::s/problems (s/explain-data spec data))]
    (mapv data-problem problems)))

(defn validate
  ([spec]
   (partial validate spec))
  ([spec data]
   (if (:format-error data)
     data
     (let [conformed (s/conform spec data)]
       (if (= conformed ::s/invalid)
         (assoc data :data-error (data-error spec data))
         conformed)))))
