(ns tailor.transform-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.test :refer [deftest is testing]]
            [tailor.transform :as transform]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(deftest transform
  (is (= {:valid? false,
          :count 2,
          :valid [{:number 1}],
          :valid-count 1,
          :invalid
          [{:number "A",
            :data-errors [{:key :number, :pred "number?", :val "A"}]}],
          :invalid-count 1,
          :error-tally #{{:key :number, :pred "number?", :count 1}}}
         (transform/collect
          (map identity)
          [{:number 1}
           {:number "A"
            :data-errors [{:key :number :pred "number?" :val "A"}]}]))))
