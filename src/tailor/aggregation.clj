(ns tailor.aggregation)

(defn tally-error [tally k]
  (if (contains? tally k)
    (update tally k inc)
    (assoc tally k 1)))

(defn tally-data-errors [tally data-errors]
  (->> data-errors
       (map #(vector (:in %) (:pred %)))
       (reduce tally-error tally)))

(def initial-result {:valid? true
                     :count 0
                     :valid []
                     :valid-count 0
                     :invalid []
                     :invalid-count 0
                     :error-tally {}})

(defn process [result position]
  (if-let [data-errors (:data-errors position)]
    (-> result
        (assoc :valid? false)
        (update :count inc)
        (update :invalid conj position)
        (update :invalid-count inc)
        (update :error-tally tally-data-errors data-errors))
    (-> result
        (update :count inc)
        (update :valid conj position)
        (update :valid-count inc))))

(defn aggregate [xf data]
  (reduce (xf process) initial-result data))
