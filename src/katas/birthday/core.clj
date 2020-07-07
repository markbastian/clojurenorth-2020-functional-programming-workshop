(ns katas.birthday.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [postal.core :as postal]
    [cuerdas.core :as cuerdas]
    [clojure.string :as cs])
  (:import (java.time MonthDay Year LocalDate)
           (java.time.temporal ChronoUnit)
           (java.time.format DateTimeFormatter)
           (javax.swing JOptionPane JPasswordField)))

(defn csv->maps [[headers & rows]]
  (let [headers (map cuerdas/keyword headers)]
    (->> rows
         (map (fn [row] (map cs/trim row)))
         (map (partial zipmap headers)))))

(defn observed-birthday [year date-of-birth]
  (let [year (cond-> year (not (int? year)) (-> Year/from .getValue))]
    (.atYear ^MonthDay (MonthDay/from date-of-birth) year)))

(defn birthday? [{:keys [today date-of-birth] :or {today (LocalDate/now)}}]
  (= today (observed-birthday today date-of-birth)))

(defn age [{:keys [today date-of-birth] :or {today (LocalDate/now)} :as m}]
  (let [period (.until date-of-birth today)
        years (.get period ChronoUnit/YEARS)
        a (cond-> years
                  (and (birthday? m) (pos? (.get period ChronoUnit/DAYS)))
                  inc)]
    (max 0 a)))

(defn parse-date [date-str]
  (LocalDate/parse date-str (DateTimeFormatter/ofPattern "yyyy/MM/dd")))

(defn read-csv-file [readable]
  (->> readable slurp csv/read-csv csv->maps))

(defn employees-with-birthday [today employee-info]
  (->> employee-info
       (map (fn [m] (-> m
                        (update :date-of-birth parse-date)
                        (assoc :today today))))
       (filter birthday?)))

(defn prepare-message [from {:keys [name email] :as m}]
  {:from    "markbastian@gmail.com"                         ;from
   :to      "markbastian@gmail.com"                         ;email
   :subject "Happy Birthday"
   :body    (format "Happy Birthday %s!\nWow, you're %s already!" name (age m))})

(defn password []
  (let [pwd (JPasswordField.)]
    (when
      (= JOptionPane/OK_OPTION
         (JOptionPane/showConfirmDialog
           nil
           pwd
           "Enter password:"
           JOptionPane/OK_CANCEL_OPTION
           JOptionPane/PLAIN_MESSAGE))
      (.getText pwd))))

(defn send-greetings [server-config {:keys [from today resource]}]
  (->> resource
       read-csv-file
       (employees-with-birthday today)
       (map (partial prepare-message from))
       (map (partial postal/send-message server-config))))

(comment
  (send-greetings
    {:host "smtp.gmail.com"
     :user "markbastian@gmail.com"
     :pass (password)
     :port 587
     :tls  true}
    {:from "markbastian@gmail.com"
     :today (LocalDate/of 2020 11 28)
     :resource (io/resource "birthday/employees.csv")})

  (def fake-send-message (constantly {:code 0, :error :SUCCESS, :message "messages sent"}))
  (def fake-password (constantly "password"))

  (with-redefs [password fake-password
                postal/send-message fake-send-message]
    (send-greetings
      {:host "smtp.gmail.com"
       :user "markbastian@gmail.com"
       :pass (password)
       :port 587
       :tls  true}
      {:from     "markbastian@gmail.com"
       :today    (LocalDate/of 2020 11 28)
       :resource (io/resource "birthday/employees.csv")})))


