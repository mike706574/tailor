(ns tailor.predicates
  (:refer-clojure :exclude [double?])
  (:require [clojure.string :as str]
            [tailor.parsers :as parsers]))

(def alpha-trimmed? #(boolean (and (string? %)
                                   (re-matches #"^[a-zA-Z]+$" (str/trim %))) ))
(def numeric? #(boolean (and (string? %)
                             (re-matches #"^[0-9]+$" %))))

(def alphanumeric? #(boolean (and (string? %)
                              (re-matches #"^[a-zA-Z0-9]+$" %))))

(def blank? str/blank?)

(def populated? (complement blank?))

(def double? (comp boolean parsers/double))

(def long? (comp boolean parsers/long))

(defn date?
  ([pattern]
   (comp boolean (parsers/date pattern)))
  ([pattern x]
   (boolean (parsers/date pattern x))))

(def basic-iso-date? (comp boolean parsers/basic-iso-date))
