(ns tailor.conform-and-validate-test
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]
            [tailor.validation :as validation]))

(stest/instrument)

(def populated? (complement str/blank?))
(defn trimmed? [s] (= (count s) (count (str/trim s))))

(s/def :domain/id (s/and string? populated?))
(s/def :domain/category #{"A" "B"})
(s/def :domain/rate double?)
(s/def :domain/date inst?)

(defn valid-rate-for-category? [item]
  (case (:domain/category item)
    "A" (> (:domain/rate item) 5.0)
    "B" (< (:domain/rate item) 2.0)))

(s/def :domain/item (s/and (s/keys :req [:domain/id :domain/rate :domain/date])
                           valid-rate-for-category?))

(def value-specs {:domain/id :tailor/to-trimmed
                  :domain/rate :tailor/to-double
                  :domain/date :tailor/to-basic-iso-date})

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

(deftest both-coercion-and-validation-error-for-field
  (is (= [{:domain/id "1",
           :domain/category "B",
           :domain/rate "1.X0",
           :domain/date #inst "1995-05-12T05:00:00.000-00:00",
           :data-errors
           [{:path [],
             :pred '(clojure.spec.alpha/conformer tailor.specs/to-double),
             :val "1.X0",
             :via [:tailor/to-double],
             :in [:domain/rate]}
            {:path [:domain/rate],
             :pred 'clojure.core/double?,
             :val "1.X0",
             :via [:domain/item :domain/rate],
             :in [:domain/rate]}]}
          (validation/conform-and-validate :domain/item value-specs [{:domain/id "1"
                                                                      :domain/category "B"
                                                                      :domain/rate "1.X0"
                                                                      :domain/date "19950512"}])])))

(deftest invalid-category
  (is (= [{:domain/id "1",
           :domain/category "C",
           :domain/rate 1.0,
           :domain/date #inst "1995-05-12T05:00:00.000-00:00",
           :data-errors
           [{:path [:domain/category],
             :pred #{"B" "A"},
             :val "C",
             :via [:domain/item :domain/category],
             :in [:domain/category]}]}]
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
             :in []}]}]
         (validation/conform-and-validate :domain/item value-specs [{:domain/id "1"
                                                                     :domain/category "B"
                                                                     :domain/rate "10.0"
                                                                     :domain/date "19950512"}]))))
