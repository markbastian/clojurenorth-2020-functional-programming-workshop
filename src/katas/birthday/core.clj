(ns katas.birthday.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as cs]
    [cuerdas.core :as cuerdas]
    [katas.birthday.util :as util]
    [postal.core :as postal])
  (:import (java.time LocalDate MonthDay Year)
           (java.time.format DateTimeFormatter)
           (java.time.temporal ChronoUnit)
           (javax.swing JOptionPane JPasswordField)))

;;CSV Parsing functions
(defn csv->maps [[headers & rows]]
  (let [headers (map cuerdas/keyword headers)]
    (->> rows
         (map (fn [row] (map cs/trim row)))
         (map (partial zipmap headers)))))

(defn read-csv-file [readable]
  (->> readable slurp csv/read-csv csv->maps))

;;Date/Birthday functions
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

;;Getting into the DSL
(defn employees-with-birthday [today employee-info]
  (->> employee-info
       (map (fn [m] (-> m
                        (update :date-of-birth parse-date)
                        (assoc :today today))))
       (filter birthday?)))

(defn prepare-message [from {:keys [name email] :as m}]
  {:from    from
   :to      email
   :subject "Happy Birthday"
   :body    (format "Happy Birthday %s!\nWow, you're %s already!" name (age m))})

(defn prepare-greetings [{:keys [from today employee-data]}]
  (->> employee-data
       (employees-with-birthday today)
       (map (partial prepare-message from))))

(defn send-greetings [server-config m]
  (->> m
       prepare-greetings
       (map (partial postal/send-message server-config))))

(comment
  (send-greetings
    {:host "smtp.gmail.com"
     :user "markbastian@gmail.com"
     :pass (util/password)
     :port 587
     :tls  true}
    {:from          "markbastian@gmail.com"
     :today         (LocalDate/of 2020 11 28)
     :employee-data (read-csv-file (io/resource "birthday/employees.csv"))}))


