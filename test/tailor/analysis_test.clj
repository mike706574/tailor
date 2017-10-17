(ns tailor.analysis-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.test :refer [deftest is testing]]
            [tailor.analysis :as analysis]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(deftest transform
  (is (= {:valid? false,
          :count 2,
          :valid-count 1,
          :invalid-count 1,
          :error-tally
          #{{:in [:number], :pred `string?, :count 1}},
          :valid [{:number 1}],
          :invalid
          [{:number "A",
            :data-errors
            [{:in [:number], :pred `string?, :val "A"}]}]}

         (analysis/categorize-and-tally
          (map identity)
          [{:number 1}
           {:number "A"
            :data-errors [{:in [:number] :pred `string? :val "A"}]}]))))
