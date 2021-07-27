(ns tdlib-json.core
  (:require [clojure.core.async :as async])
  (:import [tdlib_json TgJsonClient]))


(def client (atom nil))

(def message-queue (atom nil))

(defn create-client [path-to-lib verbosity]
  (TgJsonClient. path-to-lib verbosity))

(defn client-send
  "Sends a JSON message to underlying client. Return type is void"
  ([message]
   (.send ^TgJsonClient @client message))
  ([^TgJsonClient client message]
   (.send client message)))

(defn client-execute
  "Sends a JSON message to underlying client. Return type is JSON string.
  Only a few requests can be executed synchronously."
  ([message]
   (.execute ^TgJsonClient @client message))
  ([^TgJsonClient client message]
   (.send client message)))

(defn client-receive
  "Call a receive method of underlying client. Returns JSON or null after timeout and no data.
  Should not be called from 2 different threads at the same time."
  ([timeout]
   (.receive ^TgJsonClient @client timeout))
  ([^TgJsonClient client timeout]
   (.receive client timeout)))

(defn client-destroy
  "This function should be called on auth state authorizationStateClosed authorizationStateLoggingOut
  Optionally accepts client if you want to manage it yourself."
  ([]
   (.destroy ^TgJsonClient @client)
   (reset! message-queue nil)
   (reset! client nil))
  ([^TgJsonClient client]
   (.destroy client)))

(defn client-start
  "path-to-lib: absolute path to libtdjson.so file
   verbosity-level: log level"
  ([path-to-lib verbosity-level]
   (client-start path-to-lib verbosity-level 1024))
  ([path-to-lib verbosity-level buffer-size]
   (reset! message-queue (async/chan (async/buffer buffer-size)))
   (reset! client (create-client path-to-lib verbosity-level))))

(defn init-reader-loop
  "Timeout that is being used by underlying libtdjson"
  [timeout]
  (async/go-loop []
    (when (and @message-queue @client)
      (let [message (client-receive timeout)]
        (when-not (nil? message)
          (async/>! @message-queue message))
        (recur)))))
