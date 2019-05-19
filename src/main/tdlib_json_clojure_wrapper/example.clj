(ns main.tdlib_json_clojure_wrapper.example
  (:require [main.tdlib-json-clojure-wrapper.core :as c]
            [clojure.core.async :as async]
            [clojure.pprint :as pp])
  (:import (java.util.concurrent CountDownLatch)))
(def config {(keyword "@type") "setTdlibParameters"
             :parameters       {:api_id               "your application api",
                                :api_hash             "your application hash"
                                :application_version  "0.1"
                                :system_version       "Ubuntu 18.04",
                                :system_language_code "en",
                                :device_model         "PC",
                                :database_directory   "tg-db"}})

(defn mq-handler
  "Loops through all incoming messages and applies your logic"
  []
  (async/go-loop []
    (let [message (async/<! c/message-queue)]
      (pp/pprint message))
    (recur)))

(defn start-tg
  "Start client connection and message handling"
  [path-to-lib]
  (let [signal (CountDownLatch. 1)]
    (c/start-telegram path-to-lib config)
    (mq-handler)
    (.await signal)))

(defn get-auth-state [] (c/client-send "getAuthorizationState"))

(defn log-out
  "logout is a very special case, its the only function that provides client-send with 2 arguments"
  [] (c/client-send "logOut" true))
