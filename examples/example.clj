(ns tdlib_json.example
  (:require [tdlib_json.core :as c]
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

(defn send-phone [^String phone-message]
      "Send your phone number, required for auth, you need
      :phone-message is json serialized {ttype \"setAuthenticationPhoneNumber\" :phone_number phone-number}"
      (c/client-send phone-message))

(defn get-and-send-phone []
      (println "Enter your phone number")
      (let [phone (read-line)]
           (send-phone phone)))

(defn send-auth-code [^String code]
      "Send your code, required for auth e.g to-json({ttype \"checkAuthenticationCode\" :code code})"
      (c/client-send))

(defn get-and-send-code []
      (println "Enter your code")
      (let [code (read-line)]
           (defn log-out []
                 (c/client-send {ttype "logOut"}))
           (send-auth-code code)))

(defn get-chats []
      (c/client-send {ttype "getChats" :limit 1}))

(defn send-text-message
      ;; wrap anything you want like this
      ;(to-json {ttype                  "sendMessage"
      ;          :chat_id               chat-id
      ;          :input_message_content {ttype "inputMessageText"
      ;                                  :text {ttype "formattedText"
      ;                                         :text message}}})
      [^String message]
      (c/client-send message))

(defn close []
      (c/client-send {ttype "close"}))

(defn resolve-auth [message]
      (let [state (get-in message [:authorization_state, ttype])]
           (cond
             (#{"authorizationStateClosed" "authorizationStateLoggingOut"} state)
             (c/client-destroy)
             (= state "authorizationStateWaitTdlibParameters")
             (c/client-send config)
             (= state "authorizationStateWaitEncryptionKey")
             (c/client-send {ttype "checkDatabaseEncryptionKey"})
             (= state "authorizationStateWaitPhoneNumber")
             (get-and-send-phone)
             (= state "authorizationStateWaitCode")
             (get-and-send-code))))

(defn mq-handler
      "Loops through all incoming messages and applies your logic, i would parse message here"
      []
      (async/go-loop []
                     (let [^String message (async/<! c/message-queue)
                           type (get message ttype)]
                          (cond
                            (= type "updateAuthorizationState")
                            (resolve-auth message)
                            (= type "updateNewMessage")
                            (pp/pprint message)))
                     (when @c/client
                           (recur))))

;; Absolute path to tdlibjson.so and timeout is a double that sets timeout in receive method of tdlib
;; Verbosity level is a param sent to tdlib
(defn start-telegram
      "path-to-lib: path to tdlibjson.so
       timeout: timeout used by json client
       verbosity-level: level of debug information coming from json client
       buffer-size: size of message-que buffer / default value is 1024"
      ([path-to-lib timeout verbosity-level buffer-size]
       (when-not (and (nil? path-to-lib) (nil? timeout))
                 (c/client-start path-to-lib verbosity-level buffer-size)
                 (c/init-reader-loop timeout)
                 (mq-handler))))




