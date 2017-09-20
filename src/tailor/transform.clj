(ns tailor.transform)

(defn tally-error [tally k]
  (if (contains? tally k)
    (update tally k inc)
    (assoc tally k 1)))

(defn tally-data-errors [tally data-errors]
  (->> data-errors
       (map #(vector (:in %) (:pred %)))
       (reduce tally-error tally)))

(def initial-summary {:valid? true
                      :count 0
                      :valid-count 0
                      :invalid-count 0
                      :error-tally {}})

(defn ack-valid-position
  [result position]
  (-> result
      (update :count inc)
      (update :valid-count inc)))

(defn ack-invalid-position
  [result position]
  (-> result
      (assoc :valid? false)
      (update :count inc)
      (update :invalid-count inc)
      (update :error-tally tally-data-errors (:data-errors position))))

(defn summarize-position [result position]
  (if-let [data-errors (:data-errors position)]
    (ack-invalid-position result position)
    (ack-valid-position result position)))

(defn summarize [xf data]
  (reduce (xf summarize-position) initial-summary data))

(defn collect-position [result position]
  (if-let [data-errors (:data-errors position)]
    (-> (ack-invalid-position result position)
        (update :invalid conj position))
    (-> (ack-valid-position result position)
        (update :valid conj position))))

(def initial-collection (assoc initial-summary :valid [] :invalid []))

(defn collect [xf data]
  (reduce (xf collect-position) initial-collection data))
