(ns main.tdlib_json.example
  (:require [main.tdlib_json.core :as c]
            [clojure.core.async :as async]
            [clojure.pprint :as pp])
  (:import (java.util.concurrent CountDownLatch)))


(def ttype (keyword "@type"))

(def config {ttype       "setTdlibParameters"
             :parameters {:api_id               "your api id",
                          :api_hash             "your api hash"
                          :application_version  "0.1"
                          :system_version       "Ubuntu 18.04",
                          :system_language_code "en",
                          :device_model         "PC",
                          :database_directory   "tg-db"}})


(defn get-auth-state [] (c/client-send {ttype "getAuthorizationState"}))

(defn get-contacts []
  (c/client-send {ttype "getContacts"}))

(defn send-phone [phone-number]
  (c/client-send {ttype "setAuthenticationPhoneNumber" :phone_number phone-number}))

(defn get-and-send-phone []
  (println "Enter your phone number")
  (let [phone (read-line)]
    (send-phone phone)))

(defn send-auth-code [code]
  (c/client-send {ttype "checkAuthenticationCode" :code code}))

(defn get-and-send-code []
  (println "Enter your code")
  (let [code (read-line)]
    (send-auth-code code)))

(defn get-chats []
  (c/client-send {ttype "getChats" :limit 1}))

(defn send-text-message
  ;; wrap anything you want like this
  [chat-id message]
  (c/client-send {ttype                  "sendMessage"
                  :chat_id               chat-id
                  :input_message_content {ttype "inputMessageText"
                                          :text {ttype "formattedText"
                                                 :text message}}}))

(defn log-out
  [] (c/client-send {ttype "logOut"} true))

(defn resolve-auth [message]
  (let [state (get-in message [:authorization_state, ttype])]
    (cond
      (= state "authorizationStateWaitTdlibParameters")
      (c/client-send config)
      (= state "authorizationStateWaitEncryptionKey")
      (c/client-send {ttype "checkDatabaseEncryptionKey"})
      (= state "authorizationStateWaitPhoneNumber")
      ((get-and-send-phone))
      (= state "authorizationStateWaitCode")
      (get-and-send-code))))

(defn mq-handler
  "Loops through all incoming messages and applies your logic"
  []
  (async/go-loop []
    (let [message (async/<! c/message-queue)
          type (get message ttype)]
      (cond
        (= type "updateAuthorizationState")
        (resolve-auth message)
        (= type "updateNewMessage")
        (pp/pprint message)))
    (recur)))

;; Absolute path to tdlibjson.so and timeout is a double that sets timeout in receive method of tdlib
;; Verbosity level is a param sent to tdlib
(defn start-telegram [path-to-lib timeout & verbosity-level]
  (when-not (and (nil? path-to-lib) (nil? timeout))
    (c/client-start path-to-lib timeout (first verbosity-level))
    (c/init-reader-loop)
    (mq-handler)))

(defn start-tg
  "Start client connection and message handling, will block input on repl"
  [path-to-lib timeout]
  (let [signal (CountDownLatch. 1)]
    (start-telegram path-to-lib timeout)
    (.await signal)))




