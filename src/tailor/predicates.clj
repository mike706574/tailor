(ns tailor.predicates
  (:require [clj-time.format :as time-format]
            [clojure.string :as str]
            [tailor.parsers :as parsers]))

(def alpha-trimmed? #(boolean (and (string? %)
                                   (re-matches #"^[a-zA-Z]+$" (str/trim %))) ))
(def numeric? #(boolean (and (string? %)
                             (re-matches #"^[0-9]+$" %))))

(def alphanumeric? #(boolean (and (string? %)
                              (re-matches #"^[a-zA-Z0-9]+$" %))))

(def blank? str/blank?)

(def not-blank? (complement blank?))

(def double? (comp boolean parsers/double))

(def long? (comp boolean parsers/long))
