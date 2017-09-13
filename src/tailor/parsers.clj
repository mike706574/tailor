(ns tailor.parsers
  (:refer-clojure :exclude [double long])
  (:require [clojure.core :as core]))

(defn double
  "Coerces numbers and strings to double. Returns nil if coercion fails."
  [x]
  (try
    (cond
      (number? x) (core/double x)
      (string? x) (Double/parseDouble x)
      :else nil)
    (catch NumberFormatException e nil)))

(defn long [x]
  "Coerces numbers and strings to long. Returns nil if coercion fails."
  (try
     (cond
       (number? x) (core/long x)
       (string? x) (Long/parseLong x)
       :else nil)
     (catch NumberFormatException e nil)))

(defn ^:private parse-date
  [formatter x]
  (cond
    (instance? java.time.LocalDate x) x
    (string? x) (try
                  (java.time.LocalDate/parse x formatter)
                  (catch java.text.ParseException ex nil))))

(defn date
  "Coerces strings to java.time.LocalDate instances using the given pattern. Returns a parser when no value is given."
  ([pattern]
   (partial parse-date (java.time.format.DateTimeFormatter/ofPattern pattern)))
  ([pattern x]
   (parse-date (java.time.format.DateTimeFormatter/ofPattern pattern) x)))
