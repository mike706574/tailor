(ns tailor.conformers
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [tailor.parsers :as parsers]))

(def to-double?
  "Conformer for double coercion."
  (s/conformer #(or (parsers/double %) ::s/invalid)))

(def to-long?
  "Conformer for long coercion."
  (s/conformer #(or (parsers/long %) ::s/invalid)))

(def to-value?
  "Conformer for selecting the value from a conformed tuple."
  (s/conformer (fn [[tag x]] x)))

(def to-trimmed?
  "Conformer for trimming a string."
  (s/conformer str/trim))
