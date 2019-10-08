(ns main.tdlib_json.core
  (:require [cheshire.core :as che]
            [clojure.core.async :as async])
  (:import [main.java TgClient]))

(def client (atom nil))

(defn create-client [path-to-lib timeout]
  (TgClient. path-to-lib timeout))

(def message-queue (atom nil))

(defn jsonify [hash] (che/generate-string hash))

(defn json-parse [string]
  (che/parse-string string true))

(defn client-execute
  ([hash] (. @client execute (jsonify hash))))

(defn client-receive
  []
  (json-parse (. @client receive)))

(defn client-destroy []
  (reset! message-queue nil)
  (reset! client nil))

(defn client-send
  "If we want to logout we send additional argument that will delete pointer to tdlib client"
  [hash]
  (. @client send (jsonify hash)))

(defn client-start [path-to-lib timeout verbosity-level]
  (reset! message-queue (async/chan))
  (reset! client (create-client path-to-lib timeout))
  (. @client startClient verbosity-level))

(defn init-reader-loop []
  (async/go-loop []
    (when (and @message-queue @client)
      (let [message (client-receive)]
        (when-not
          (nil? message)
          (async/>! @message-queue message))
        (recur)))))


