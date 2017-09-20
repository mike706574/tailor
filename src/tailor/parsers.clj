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

;; java.util.Date
(defn ^:private parse-date
  [formatter x]
  (cond
    (instance? java.util.Date x) x
    (string? x) (try
                  (.parse formatter x)
                  (catch java.text.ParseException ex nil))))

(defn date
  "Coerces strings to java.util.Date instances using the given pattern. Returns a parser when no value is given."
  ([pattern]
   (partial parse-date (java.text.SimpleDateFormat. pattern)))
  ([pattern x]
   (parse-date (java.text.SimpleDateFormat. pattern) x)))

(def ^:private basic-iso-date-format (java.text.SimpleDateFormat. "yyyyMMdd"))

(defn basic-iso-date
  "Coerces strings to java.util.Date instances using the basic ISO date pattern."
  [x]
  (parse-date basic-iso-date-format x))

;; java.time.LocalDate
(defn ^:private parse-date-8
  [formatter x]
  (cond
    (instance? java.time.LocalDate x) x
    (string? x) (try
                  (java.time.LocalDate/parse x formatter)
                  (catch java.time.format.DateTimeParseException ex nil))))

(defn date-8
  "Coerces strings to java.time.LocalDate instances using the given pattern. Returns a parser when no value is given."
  ([pattern]
   (partial parse-date (java.time.format.DateTimeFormatter/ofPattern pattern)))
  ([pattern x]
   (parse-date (java.time.format.DateTimeFormatter/ofPattern pattern) x)))

(defn basic-iso-date-8
  "Coerces strings to java.time.LocalDate instances using the basic ISO date pattern."
  [x]
  (parse-date java.time.format.DateTimeFormatter/BASIC_ISO_DATE x))
