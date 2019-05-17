(defproject tdlib_json_clojure_wrapper "0.1.0-SNAPSHOT"
  :description "tdlib_json_clojure_wrapper"
  :url "https://github.com/MityaSaray/clojure-tdlib-json-wrapper"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.sun.jna/jna "3.0.9"]
                 [cheshire "5.8.1"]
                 [org.clojure/core.async "0.4.490"]]
  :main tdlib-json-clojure-wrapper.core
  :plugins [[lein-shell "0.5.0"]]
  :aliases {"buildTg"
            ["shell" "./tdlib_install.sh"]})
