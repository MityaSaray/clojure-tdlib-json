(ns tdlib-json-clojure-wrapper.core
  (:require [tdlib-json-clojure-wrapper.tg-connector.core :as conn]
            [tdlib-json-clojure-wrapper.tg-connector.handlers :as handlers])
  (:import (java.util.concurrent CountDownLatch))
  (:gen-class))

(defn -main
  "i start client connection and message handling"
  [& args]
  (let [signal (CountDownLatch. 1)]
   (println "Disable autostart if you are encountering problems")
   (conn/start-telegram)
   (handlers/mq-handler)
   (.await signal)))