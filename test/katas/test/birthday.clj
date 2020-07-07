(ns katas.test.birthday
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer :all]
    [katas.birthday.core :as birthday]
    [katas.birthday.util :as util]
    [postal.core :as postal])
  (:import (java.time LocalDate)))

(deftest birthday-test
  (testing "A variety of test cases for birthday logic"
    ;Born on leap day, today is leap day.
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2020 2 29)
                  :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on leap day, today is not leap day in leap year
    (is (false? (birthday/birthday?
                  {:today         (LocalDate/of 2020 2 28)
                   :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on leap year, not on leap day, is leap day
    (is (false? (birthday/birthday?
                  {:today         (LocalDate/of 2020 2 29)
                   :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap year, not on leap day, is also not leap day
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2020 2 28)
                  :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap year, not on leap day, today is same date not leap year
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2019 2 28)
                  :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap day, today is not a leap year but 2-28
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2019 2 28)
                  :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on non-leap year
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2019 2 28)
                  :date-of-birth (LocalDate/of 2001 2 28)})))
    (is (true? (birthday/birthday?
                 {:today         (LocalDate/of 2020 2 28)
                  :date-of-birth (LocalDate/of 2001 2 28)})))
    (is (false? (birthday/birthday?
                  {:today         (LocalDate/of 2020 2 29)
                   :date-of-birth (LocalDate/of 2001 2 28)})))
    ))

(deftest age-test
  (testing "A variety of test cases for age logic"
    ;Born on leap day, today is leap day.
    (is (= 20 (birthday/age
                {:today         (LocalDate/of 2020 2 29)
                 :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on leap day, today is not leap day in leap year
    (is (= 19 (birthday/age
                {:today         (LocalDate/of 2020 2 28)
                 :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on leap year, not on leap day, is leap day
    (is (= 20 (birthday/age
                {:today         (LocalDate/of 2020 2 29)
                 :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap year, not on leap day, is also not leap day
    (is (= 20 (birthday/age
                {:today         (LocalDate/of 2020 2 28)
                 :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap year, not on leap day, today is same date not leap year
    (is (= 19 (birthday/age
                {:today         (LocalDate/of 2019 2 28)
                 :date-of-birth (LocalDate/of 2000 2 28)})))
    ;Born on leap day, today is not a leap year but 2-28
    (is (= 19 (birthday/age
                {:today         (LocalDate/of 2019 2 28)
                 :date-of-birth (LocalDate/of 2000 2 29)})))
    ;Born on non-leap year
    (is (= 18 (birthday/age
                {:today         (LocalDate/of 2019 2 28)
                 :date-of-birth (LocalDate/of 2001 2 28)})))
    (is (= 19 (birthday/age
                {:today         (LocalDate/of 2020 2 28)
                 :date-of-birth (LocalDate/of 2001 2 28)})))
    (is (= 19 (birthday/age
                {:today         (LocalDate/of 2020 2 29)
                 :date-of-birth (LocalDate/of 2001 2 28)})))
    (is (zero? (birthday/age
                 {:today         (LocalDate/of 2000 2 29)
                  :date-of-birth (LocalDate/of 2000 2 29)})))
    (is (zero? (birthday/age
                 {:today         (LocalDate/of 1995 2 28)
                  :date-of-birth (LocalDate/of 2000 2 29)})))
    ))

(def fake-send-message (constantly {:code 0, :error :SUCCESS, :message "messages sent"}))
(def fake-password (constantly "password"))

(deftest test-prepare-greetings
  (testing "Ability to prepare all the greetings"
    (= [{:from "markbastian@gmail.com",
         :to "builder.bob@example.com",
         :subject "Happy Birthday",
         :body "Happy Birthday Bob!\nWow, you're 22 already!"}]
       (birthday/prepare-greetings
         {:from          "markbastian@gmail.com"
          :today         (LocalDate/of 2020 11 28)
          :employee-data (birthday/read-csv-file
                           (io/resource "birthday/employees.csv"))}))))

(deftest test-send-greetings
  (testing "Ability to send all emails"
    (= [{:code 0, :error :SUCCESS, :message "messages sent"}]
       (with-redefs [util/password fake-password
                     postal/send-message fake-send-message]
         (birthday/send-greetings
           {:host "smtp.gmail.com"
            :user "markbastian@gmail.com"
            :pass (util/password)
            :port 587
            :tls  true}
           {:from          "markbastian@gmail.com"
            :today         (LocalDate/of 2020 11 28)
            :employee-data (birthday/read-csv-file
                             (io/resource "birthday/employees.csv"))})))))
