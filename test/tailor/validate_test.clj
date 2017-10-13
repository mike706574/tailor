(ns tailor.validate-test
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [tailor.validation :as validation]
            [tailor.parsers :as parsers]
            [tailor.specs :refer :all]))

(s/def ::id (s/and string? (complement str/blank?)))
(s/def ::rate double?)
(s/def ::date inst?)

(s/def ::item (s/keys :req [::id ::rate ::date]))

(def iso-date (parsers/date "yyyyMMdd"))

(deftest valid
  (is (= [{::id "foo"
           ::rate 2.5
           ::date (iso-date "19950112")}]
         (validation/validate ::item [{::id "foo"
                                       ::rate 2.5
                                       ::date (iso-date "19950112")}]))))

(deftest invalid?
  (is (= [{:tailor.validate-test/id " ",
            :tailor.validate-test/rate "x",
            :tailor.validate-test/date "01x21995",
            :data-errors
            [{:path [:tailor.validate-test/id],
              :pred `(complement str/blank?)
              :val " ",
              :via [:tailor.validate-test/item :tailor.validate-test/id],
              :in [:tailor.validate-test/id],
              :key :tailor.validate-test/id}
             {:path [:tailor.validate-test/rate],
              :pred `double?,
              :val "x",
              :via [:tailor.validate-test/item :tailor.validate-test/rate],
              :in [:tailor.validate-test/rate],
              :key :tailor.validate-test/rate}
             {:path [:tailor.validate-test/date],
              :pred `inst?,
              :val "01x21995",
              :via [:tailor.validate-test/item :tailor.validate-test/date],
              :in [:tailor.validate-test/date],
              :key :tailor.validate-test/date}]}]
         (validation/validate ::item [{::id " "
                                       ::rate "x"
                                       ::date "01x21995"}]))))
