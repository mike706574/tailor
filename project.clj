(defproject org.clojars.mike706574/tailor "0.0.11"
  :description "Potentially useful string predicates and conformers."
  :url "https://github.com/mike706574/tailor"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :profiles {:dev {:source-paths ["dev"]
                   :target-path "target/dev"
                   :dependencies [[org.clojure/clojure "1.9.0-alpha20"]
                                  [org.clojure/spec.alpha "0.1.123"]
                                  [org.clojure/tools.namespace "0.2.11"]]}}
  :repl-options {:init-ns user})
