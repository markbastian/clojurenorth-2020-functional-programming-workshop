(ns katas.test.birthday
  (:require
    [clojure.test :refer :all]
    [katas.birthday.core :as birthday])
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

(deftest greet!
  (testing "sends email to everyone with birthday today"
    ;; How do we test???
    #_(greet!)))
