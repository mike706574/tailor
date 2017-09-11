(ns tailor.conformers
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [tailor.parsers :as parsers]))

(def to-double? (s/conformer #(or (parsers/double %) ::s/invalid)))
(def to-long? (s/conformer #(or (parsers/long %) ::s/invalid)))
(def to-value? (s/conformer (fn [[tag x]] x)))
(def to-trimmed? (s/conformer str/trim))
