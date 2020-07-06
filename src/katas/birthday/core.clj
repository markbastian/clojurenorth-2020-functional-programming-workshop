(ns katas.birthday.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [postal.core :as postal]
    [cuerdas.core :as cuerdas]
    [clojure.string :as cs]
    [java-time :as jt]))

(defn csv->maps [[headers & rows]]
  (let [headers (map cuerdas/keyword headers)]
    (->> rows
         (map (fn [row] (map cs/trim row)))
         (map (partial zipmap headers)))))

(def leap-day (jt/month-day 2 29))

(defn observed-birthday [year birthday]
  (cond-> birthday
          (and (= (jt/month-day birthday) leap-day)
               (not (jt/leap? year)))
          (jt/minus (jt/days 1))))

(defn birthday?
  ([today birthday]
   (= today (observed-birthday today birthday)))
  ([birthday] (birthday? (jt/local-date) birthday)))

(defn prepare-message
  ([today {:keys [name date-of-birth] :as m}]
   (let [age (jt/time-between :years (observed-birthday today date-of-birth) today)]
     (assoc m
       :subject "Happy Birthday"
       :body (format "Happy Birthday %s!\nWow, you're %s already!" name age))))
  ([m] (prepare-message (jt/local-date) m)))

;(defn send-message [server message]
;  (postal/send-message
;    server
;    {:from    "me@example.com"
;     :to      (row 1)}))

(defn greet! []
  (let [today (jt/local-date 1998 11 28)
        server-config {:host "localhost"
                       :user "azurediamond"
                       :pass "hunter2"
                       :port 2525}]
    (->> (io/resource "birthday/employees.csv")
         (io/reader)
         (csv/read-csv)
         csv->maps
         (map #(update % :date-of-birth (partial jt/local-date "yyyy/MM/dd")))
         (filter (comp #(birthday? today %) :date-of-birth))
         (map prepare-message)
         ;(map (partial postal/send-message server-config))
         doall)))
