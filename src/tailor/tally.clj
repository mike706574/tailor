(ns tailor.tally)

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
  (reduce tally-error tally data-errors))

(defn ack-valid-item
  [result item]
  (-> result
      (update :count inc)
      (update :valid-count inc)))

(defn ack-invalid-item
  [result item]
  (-> result
      (assoc :valid? false)
      (update :count inc)
      (update :invalid-count inc)
      (update :error-tally tally-errors (:data-errors item))))


(defn collect-item
  ([result] result)
  ([result item]
   (-> (if-let [data-errors (:data-errors item)]
         (ack-invalid-item result item)
         (ack-valid-item result item))
       (update :items conj item))))

(def initial-collection {:valid? true
                         :count 0
                         :valid-count 0
                         :invalid-count 0
                         :error-tally #{}
                         :items []})

(defn tally
  ([data]
   (reduce collect-item initial-collection data))
  ([xf data]
   (transduce xf collect-item initial-collection data)))
