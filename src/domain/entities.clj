(ns domain.entities
  (:require [java-time :as t]))

(defrecord User
   [^String id
    ^String first-name
    ^String last-name
    ^String password])
(defn user [id first-name last-name password]
  (->User id first-name last-name password))

(defrecord News
  [^Integer id
   ^String user-id
   ^String text
   ^String date-time])
(defn news [id user-id text]
  (->News id user-id text (.toString (t/local-date-time))))
