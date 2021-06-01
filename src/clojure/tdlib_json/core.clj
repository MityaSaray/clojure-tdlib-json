(ns tdlib-json.core
  (:require
   [cheshire.core :as json]
   [clojure.core.async :as async])
  (:import [tdlib_json TgJsonClient]))


(defn jsonify [messageMap]
  (json/generate-string messageMap))

(defn json-parse [string]
  (json/parse-string string true))


(def client (atom nil))

(def message-queue (atom nil))


(defn create-client [path-to-lib verbosity]
  (TgJsonClient. path-to-lib verbosity))

(defn client-send [messageMap]
  (.send @client (jsonify messageMap)))

(defn client-execute [messageMap]
  (.execute @client (jsonify messageMap)))

(defn client-receive [timeout]
  (json-parse (.receive @client timeout)))

(defn client-destroy []
  (.destroy @client)
  (reset! message-queue nil)
  (reset! client nil))

(defn client-start
  ([path-to-lib verbosity-level]
   (client-start path-to-lib verbosity-level 1024))
  ([path-to-lib verbosity-level buffer-size]
   (reset! message-queue (async/chan (async/buffer buffer-size)))
   (reset! client (create-client path-to-lib verbosity-level))))


(defn init-reader-loop [timeout]
  (async/go-loop [t timeout]
    (when (and @message-queue @client)
      (let [message (client-receive t)]
        (when-not (nil? message)
          (async/>! @message-queue message))
        (recur t)))))
