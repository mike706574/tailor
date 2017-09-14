(ns tailor.validation-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer [deftest is testing]]
            [tailor.validation :as validation]
            [tailor.parsers :as parsers]
            [tailor.conformers :refer :all]
            [tailor.predicates :refer :all]))

(s/def ::id (s/and string? not-blank? to-trimmed?))
(s/def ::rate (s/and double? to-double?))
(s/def ::date (s/and date? (to-date? "yyyyMMdd")))

(s/def ::item (s/keys :req [::id ::rate ::date]))

(def iso-date (parsers/date "yyyyMMdd"))

(deftest valid
  (is (= {::id "foo"
          ::rate 2.5
          ::date (iso-date "19950112")}
         (validation/validate-item ::item {::id "  foo  "
                                           ::rate "2.5"
                                           ::date "19950112"}))))

(deftest invalid?
  (is (= {:tailor.validation-test/id " ",
          :tailor.validation-test/rate "x",
          :tailor.validation-test/date "01121995",
          :data-error
          [{:pred "not-blank?", :val " ", :in :tailor.validation-test/id}
           {:pred "double?", :val "x", :in :tailor.validation-test/rate}
           {:pred "to-date?",
            :val "01121995",
            :in :tailor.validation-test/date}]}
         (validation/validate-item ::item {::id " "
                                           ::rate "x"
                                           ::date "01121995"}))))
