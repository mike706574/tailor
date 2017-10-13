(ns tailor.conform-and-validate-test
  (:refer-clojure :exclude [double?])
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer [deftest is testing]]
            [tailor.validation :as validation]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.predicates :refer :all]))

(stest/instrument)

(s/def :domain/id (s/and string? populated? to-trimmed))
(s/def :domain/category #{"A" "B"})
(s/def :domain/rate (s/and double? to-double))
(s/def :domain/date (s/and basic-iso-date? (to-date "yyyyMMdd")))

(defn valid-rate-for-category? [item]
  (case (:domain/category item)
    "A" (> (:domain/rate item) 5.0)
    "B" (< (:domain/rate item) 2.0)))

(s/def :domain/item (s/and (s/keys :req [:domain/id :domain/rate :domain/date])
                           valid-rate-for-category?))

(def value-specs {:domain/id :domain/id
                  :domain/category :domain/category
                  :domain/rate :domain/rate
                  :domain/date :domain/date})

(def iso-date (parsers/date "yyyyMMdd"))

(deftest conforming-valid-item
  (is (= [{:domain/id "1"
           :domain/category "B"
           :domain/rate 1.0
           :domain/date (iso-date "19950512")}]
         (validation/conform-and-validate :domain/item value-specs [{:domain/id "1"
                                                                     :domain/category "B"
                                                                     :domain/rate "1.0"
                                                                     :domain/date "19950512"}]))))

(deftest invalid-category
  (is (= [{:domain/id "1"
           :domain/category "C"
           :domain/rate 1.0
           :domain/date (iso-date "19950512")
           :data-errors [{:path [],
                          :pred #{"B" "A"},
                          :val "C",
                          :in []
                          :via [:domain/category],
                          :key :domain/category}]}]
         (validation/conform-and-validate :domain/item value-specs [{:domain/id "1"
                                                                     :domain/category "C"
                                                                     :domain/rate "1.0"
                                                                     :domain/date "19950512"}]))))

(deftest invalid-rate
  (is (= [{:domain/id "1",
           :domain/category "B",
           :domain/rate 10.0,
           :domain/date (iso-date "19950512")
           :data-errors
           [{:path [],
             :pred 'tailor.conform-and-validate-test/valid-rate-for-category?,
             :val #:domain{:id "1",
                           :category "B",
                           :rate 10.0,
                           :date (iso-date "19950512")},
             :via [:domain/item],
             :in [],
             :key nil}]}]
         (validation/conform-and-validate :domain/item value-specs [{:domain/id "1"
                                                                     :domain/category "B"
                                                                     :domain/rate "10.0"
                                                                     :domain/date "19950512"}]))))
