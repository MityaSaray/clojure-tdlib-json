(ns tdlib-json-clojure-wrapper.tg-connector.handlers
  (:require [clojure.core.async :as async]
            [tdlib-json-clojure-wrapper.tg-connector.core :as c]
            [clojure.pprint :as pp]))

(defn mq-handler []
  (async/go-loop []
    (let [message (async/<! c/message-queue)]
      (pp/pprint message))
    (recur)))
