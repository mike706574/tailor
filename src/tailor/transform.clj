(ns tailor.transform)

(defn tally-error [tally {:keys [in pred]}]
  (letfn [(same-error? [other]
            (and (= in (:in other) )
                 (= pred (:pred other))))]
    (set
     (if-let [item (first (filter same-error? tally))]
       (->> tally
            (remove same-error?)
            (cons (update item :count inc)))
       (conj tally {:in in :pred pred :count 1})))))

(defn tally-errors [tally data-errors]
  (reduce tally-error #{} data-errors))

(def initial-summary {:valid? true
                      :count 0
                      :valid-count 0
                      :invalid-count 0
                      :error-tally #{}})

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
      (update :error-tally tally-errors (:data-errors position))))

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
