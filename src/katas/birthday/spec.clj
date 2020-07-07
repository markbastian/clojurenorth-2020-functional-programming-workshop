(ns katas.birthday.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as cs]
            [talltale.core :as tc]))

(def email-regex
  #"[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")

(s/def ::name (s/with-gen string? tc/first-name-gen))

(s/def ::email (s/with-gen
                 (s/and string? #(re-matches email-regex %))
                 #(gen/return (tc/email))))

(s/def ::date-of-birth (s/with-gen
                         (s/and string? #(re-matches #"\d{4}/\d{2}/\d{2}" %))
                         #(gen/fmap
                            (fn [v] (cs/replace (str v) #"-" "/"))
                            (tc/date-of-birth-gen (tc/age)))))

(s/def ::employee (s/keys :req-un [::name ::email ::date-of-birth]))
(s/def ::employees (s/coll-of ::employee))

(comment
  (gen/generate (gen/return (tc/date-of-birth)))
  (gen/sample (gen/return (tc/date-of-birth)))

  (gen/sample (s/gen ::name))
  (gen/sample (s/gen ::email))
  (gen/sample (s/gen ::date-of-birth))
  (gen/generate (s/gen ::employee))
  (gen/sample (s/gen ::employee))
  (gen/generate (s/gen ::employees)))
