(ns tailor.aggregation-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.test :refer [deftest is testing]]
            [tailor.aggregation :as aggregation]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(deftest aggregate
  (is (= {:valid? false,
          :count 2,
          :valid [{:number 1}],
          :valid-count 1,
          :invalid
          [{:number "A",
            :data-errors [{:in :number, :pred "number?", :val "A"}]}],
          :invalid-count 1,
          :error-tally {[:number "number?"] 1}}
         (aggregation/aggregate identity [{:number 1}
                                          {:number "A"
                                           :data-errors [{:in :number :pred "number?" :val "A"}]}]))))
