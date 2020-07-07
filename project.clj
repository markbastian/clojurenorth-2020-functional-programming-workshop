(defproject clojure-fp-workshop "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 ;; for birthday-greeting:
                 [org.clojure/data.csv "1.0.0"]
                 [com.draines/postal "2.0.3"]
                 [funcool/cuerdas "2020.03.26-3"]
                 [talltale "0.4.3"]
                 [org.clojure/test.check "1.0.0" :scope "test"]]

  :profiles {:birthday {:cloverage {:ns-regex      [#"katas.birthday.core"]
                                    :test-ns-regex [#"katas.test.birthday"]}}}

  :aliases {"test-birthday"     ["test" "katas.test.birthday"]
            "birthday-coverage" ["with-profile" "+birthday" "cloverage"]})
