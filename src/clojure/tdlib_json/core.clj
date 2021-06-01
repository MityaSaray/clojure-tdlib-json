(ns tdlib-json.core
  (:require [cheshire.core :as che]
            [clojure.core.async :as async])
  (:import [tdlib_json TgClient]))

(def client (atom nil))

(defn create-client [path-to-lib timeout]
  (TgClient. path-to-lib timeout))

(def message-queue (atom nil))

(defn jsonify [messageMap] (che/generate-string messageMap))

(defn json-parse [string]
  (che/parse-string string true))

(defn client-execute
  ([messageMap] (. @client execute (jsonify messageMap))))

(defn client-receive
  []
  (json-parse (. @client receive)))

(defn client-destroy []
  (reset! message-queue nil)
  (reset! client nil))

(defn client-send
  [messageMap]
  (. @client send (jsonify messageMap)))

(defn client-start
  ([path-to-lib timeout verbosity-level]
   (client-start path-to-lib timeout verbosity-level 1024))
  ([path-to-lib timeout verbosity-level buffer-size]
   (reset! message-queue (async/chan (async/buffer buffer-size)))
   (reset! client (create-client path-to-lib timeout))
   (. @client startClient verbosity-level)))

(defn init-reader-loop []
  (async/go-loop []
    (when (and @message-queue @client)
      (let [message (client-receive)]
        (when-not
          (nil? message)
          (async/>! @message-queue message))
        (recur)))))
