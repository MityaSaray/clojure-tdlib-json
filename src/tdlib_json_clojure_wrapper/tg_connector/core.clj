(ns tdlib-json-clojure-wrapper.tg-connector.core
  (:require [cheshire.core :as che]
            [clojure.core.async :as async])
  (:import (tdlib_json_clojure_wrapper.tg_connector TgClient)))

(def client (TgClient.))

;; Telegram uses this format of keys
(def ttype (keyword "@type"))

(def telegram-ready-state (atom false))

(def message-queue (async/chan))

(defn jsonify [hash] (che/generate-string hash))

(defn json-parse [string]
  (che/parse-string string))

(defn client-destroy [] (. client destroyClient))

(defn client-execute
  ([type] (. client execute (jsonify {ttype type})))
  ;; if has some option to command
  ([type key command]
   (. client execute
      (jsonify
        {ttype type (keyword key) command}))))

(defn client-receive
  "argument is timeout in seconds and it has to be double"
  [] (json-parse (. client receive 0.3)))

(defn client-destroy []
  (reset! telegram-ready-state false)
  (. client destroyClient))

(defn client-send
  ([type]
   (. client send (jsonify {ttype type})))
  ;; if we want to log out there is slightly different behaviour
  ;; it does not just stop server, it clears our data from telegram and you will need to log in next time
  ([type logout] (. client send (jsonify {ttype type}) logout))
  ([type key message]
   (. client send
      (jsonify
        {ttype type (keyword key) message}))))

(defn send-phone [phone-number]
  (client-send "setAuthenticationPhoneNumber" "phone_number" phone-number))

(defn get-and-send-phone []
  (println "Enter your phone number")
  (let [phone (read-line)]
    (send-phone phone)))

(defn send-auth-code [code]
  (client-send "checkAuthenticationCode" "code" code))

(defn get-and-send-code []
  (println "Enter your code")
  (let [code (read-line)]
    (send-auth-code code)))

(defn client-start []
  (. client startClient))

(defn init-reader-loop []
  (async/go-loop []
    (let [message
          (try
            (client-receive)
            (catch Exception e (str "caught " (.getMessage e) "exception")))]
      (if-not
        (nil? message)
        (async/>! message-queue message))
      (recur))))

(defn start-service []
  (reset! telegram-ready-state true)
  (init-reader-loop))

(defn start-telegram []
  (client-start)
  (loop [message nil]
    (let [state (get-in message ["authorization_state", "@type"])]
      (if-not (= state "authorizationStateReady")
        (cond
          (= state "authorizationStateWaitTdlibParameters")
          (do
            (. client send (slurp "./config.json"))
            (recur (client-receive)))
          (= state "authorizationStateWaitEncryptionKey")
          (do
            (client-send "checkDatabaseEncryptionKey")
            (recur (client-receive)))
          (= state "authorizationStateWaitPhoneNumber")
          (do (get-and-send-phone)
              (recur (client-receive)))
          (= state "authorizationStateWaitCode")
          (do (get-and-send-code)
              (recur (client-receive)))
          :else
          (recur (client-receive)))
        (start-service)))))