(ns tailor.validate-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is testing]]
            [tailor.validation :as validation]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(s/def ::id (s/and string? populated? to-trimmed))
(s/def ::rate (s/and double? to-double))
(s/def ::date (s/and basic-iso-date? (to-date "yyyyMMdd")))

(s/def ::item (s/keys :req [::id ::rate ::date]))

(def iso-date (parsers/date "yyyyMMdd"))

(deftest valid
  (is (= [{::id "foo"
           ::rate 2.5
           ::date (iso-date "19950112")}]
         (validation/validate ::item [{::id "  foo  "
                                       ::rate "2.5"
                                       ::date "19950112"}]))))

(deftest invalid?
  (is (= [{:tailor.validate-test/id " ",
           :tailor.validate-test/rate "x",
           :tailor.validate-test/date "01x21995",
           :data-errors
           [{:path [:tailor.validate-test/id],
             :pred 'tailor.predicates/populated?,
             :val " ",
             :via [:tailor.validate-test/item :tailor.validate-test/id],
             :in [:tailor.validate-test/id],
             :key :tailor.validate-test/id}
            {:path [:tailor.validate-test/rate],
             :pred 'tailor.predicates/double?,
             :val "x",
             :via [:tailor.validate-test/item :tailor.validate-test/rate],
             :in [:tailor.validate-test/rate],
             :key :tailor.validate-test/rate}
            {:path [:tailor.validate-test/date],
             :pred 'tailor.predicates/basic-iso-date?,
             :val "01x21995",
             :via [:tailor.validate-test/item :tailor.validate-test/date],
             :in [:tailor.validate-test/date],
             :key :tailor.validate-test/date}]}]
         (validation/validate ::item [{::id " "
                                       ::rate "x"
                                       ::date "01x21995"}]))))
