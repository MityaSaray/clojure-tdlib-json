(defproject tdlib-json-wrapper "0.3.1"
  :description "tdlib_json"
  :url "https://github.com/MityaSaray/clojure-tdlib-json"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.sun.jna/jna "3.0.9"]
                 [org.clojure/core.async "0.4.490"]]
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/clojure "1.10.0"]]}})
