(ns tailor.parsers
  (:refer-clojure :exclude [double long])
  (:require [clojure.core :as core]))

(defn double [x]
  (try
    (cond
      (number? x) (core/double x)
      (string? x) (Double/parseDouble x)
      :else nil)
    (catch NumberFormatException e nil)))

(defn long [x]
  (try
     (cond
       (number? x) (core/long x)
       (string? x) (Long/parseLong x)
       :else nil)
     (catch NumberFormatException e nil)))
