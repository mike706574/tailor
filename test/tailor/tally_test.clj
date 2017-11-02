(ns tailor.tally-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.test :refer [deftest is testing]]
            [tailor.tally :as tally]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(deftest transform
  (is (= {:valid? false,
          :count 3,
          :valid-count 1,
          :invalid-count 2,
          :error-tally #{{:in [:number], :pred `int?, :count 2}},
          :items
          [{:number 1}
           {:number "A",
            :data-errors [{:in [:number], :pred `int?, :val "A"}]}
           {:number "B",
            :data-errors
            [{:in [:number], :pred `int?, :val "B"}]}]}

         (tally/tally
          (map identity)
          [{:number 1}
           {:number "A"
            :data-errors [{:in [:number] :pred `int? :val "A"}]}
           {:number "B"
            :data-errors [{:in [:number] :pred `int? :val "B"}]}]))))
