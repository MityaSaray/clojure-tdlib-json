(ns main.tdlib_json.core
  (:require [cheshire.core :as che]
            [clojure.core.async :as async])
  (:import [main.java TgClient]))

(def client (atom nil))

(defn create-client [path-to-lib timeout]
  (TgClient. path-to-lib timeout))

(def message-queue (async/chan))

(defn jsonify [hash] (che/generate-string hash))

(defn json-parse [string]
  (che/parse-string string true))

(defn client-execute
  ([hash] (. @client execute (jsonify hash))))

(defn client-receive
  "argument is timeout in seconds and it has to be double"
  []
  (json-parse (. @client receive)))

(defn client-destroy []
  (. @client destroyClient)
  (reset! client nil))

(defn client-send
  "If we want to logout we send additional argument that will delete pointer to tdlib client"
  ([hash]
   (. @client send (jsonify hash) false))
  ([hash logout]
   (. @client send (jsonify hash) logout)))

(defn client-start [path-to-lib timeout verbosity-level]
  (reset! client (create-client path-to-lib timeout))
  (. @client startClient verbosity-level))

(defn init-reader-loop []
  (async/go-loop []
    (let [message (client-receive)]
      (when-not
        (nil? message)
        (async/>! message-queue message))
      (recur))))


