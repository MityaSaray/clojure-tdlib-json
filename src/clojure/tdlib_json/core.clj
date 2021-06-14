(ns tdlib-json.core
  (:require [clojure.core.async :as async])
  (:import [tdlib_json TgJsonClient]))


(def client (atom nil))

(def message-queue (atom nil))

(defn create-client [path-to-lib verbosity]
  (TgJsonClient. path-to-lib verbosity))

(defn client-send [message]
  (.send ^TgJsonClient @client message))

(defn client-execute [message]
  (.execute ^TgJsonClient @client message))

(defn client-receive [timeout]
  (.receive ^TgJsonClient @client timeout))

(defn client-destroy
  "This function should be called on auth state authorizationStateClosed authorizationStateLoggingOut"
  []
  (.destroy ^TgJsonClient @client)
  (reset! message-queue nil)
  (reset! client nil))

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
