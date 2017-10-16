(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application."
  (:require
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.set :as set]
   [clojure.string :as str]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [com.nm.mi.swpi.validation.main :as main]))

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  [])

(defn start
  "Starts the system."
  [])

(defn stop
  "Stops the system if it is currently running.
  #'system."
  [])

(defn go
  "Initializes and starts the system."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh :after `go))

(defn restart
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (go))

(comment
  (s/def ::keyword-or-populated-string?
    (s/or :keyword keyword?
          :string (s/and string? (complement str/blank?))))

  (s/def :tailor/spec qualified-ident?)
  (s/def :tailor/coercion :tailor/spec)
  (s/def :tailor/values (s/map-of :opt-un [:tailor/spec :tailor/coercion]))
  (s/def :tailor/schema (s/keys :opt-un [:tailor/validation
                                         :tailor/coercion
                                         :tailor/values])))
