(ns tdlib-json-clojure-wrapper.tg-connector.requests
  (:require [tdlib-json-clojure-wrapper.tg-connector.core :as c]
            [clojure.core.async :as async]))

(defn get-auth-state [] (c/client-send "getAuthorizationState"))

(defn log-out [] (c/client-send "logOut" true))
