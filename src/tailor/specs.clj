(ns tailor.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [tailor.parsers :as parsers]))

(def to-double
  "Conformer for double coercion."
  (let [to-double (fn [x] (or (parsers/double x) ::s/invalid))]
    (s/conformer to-double)))

(def to-long
  "Conformer for long coercion."
  (let [to-long (fn [x] (or (parsers/long x) ::s/invalid))]
    (s/conformer to-long)))

(def to-value
  "Conformer for selecting the value from a conformed tuple."
  (let [to-value (fn [[tag x]] x)]
    (s/conformer to-value)))

(def to-trimmed
  "Conformer for trimming a string."
  (let [to-trimmed str/trim]
    (s/conformer to-trimmed)))

(defn to-date
  [pattern]
  (let [parse (parsers/date pattern)
        to-date (fn [x] (or (parse x) ::s/invalid))]
    (s/conformer to-date)))

(let [to-basic-iso-date (fn [x] (or (parsers/basic-iso-date x) ::s/invalid))]
  (def to-basic-iso-date
     "Conformer for basic ISO java.util.Date coercion."
    (s/conformer to-basic-iso-date)))

(defn to-date-8
  [pattern]
  (let [parse (parsers/date-8 pattern)
        to-date-8 (fn [x] (or (parse x) ::s/invalid))]
    (s/conformer to-date-8)))

(let [to-basic-iso-date-8 (fn [x] (or (parsers/basic-iso-date-8 x) ::s/invalid))]
  (def to-basic-iso-date-8
     "Conformer for basic ISO java.time.LocalDate coercion."
    (s/conformer to-basic-iso-date-8)))

(s/def :tailor/to-double to-double)
(s/def :tailor/to-long to-long)
(s/def :tailor/to-value to-value)
(s/def :tailor/to-trimmed to-trimmed)
(s/def :tailor/to-basic-iso-date to-basic-iso-date)
(s/def :tailor/to-basic-iso-date-8 to-basic-iso-date-8)
