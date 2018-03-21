(ns kixi.spec.conformers-test
  (:require [kixi.spec.base :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test :refer :all]
            [kixi.spec.conformers :as sc]))

(def ^:dynamic *tries* 1000)

(defn conformer-check
  [conformer]
  (let [r (map (partial s/explain-data conformer) (gen/sample (s/gen conformer) *tries*))]
    (is (every? nil? r))))

(deftest integer?-test
  (conformer-check sc/integer?))

(deftest varint?-test
  (conformer-check sc/varint?))

(deftest double?-test
  (conformer-check sc/double?))

(deftest integer-range?-test
  (conformer-check (sc/integer-range? 0 100)))

(deftest double-range?-test
  (conformer-check (sc/double-range? 0.0 100.0)))

(deftest set?-test
  (conformer-check (sc/set? :a :b :c)))

(deftest regex?-test
  (conformer-check (sc/regex? "[a-zA-Z0-9]+_[a-zA-Z0-9]+_foobar"
                              #(gen/fmap (partial apply str)
                                         (gen/tuple (gen/such-that sc/not-blank  (gen/string-alphanumeric))
                                                    (gen/return "_")
                                                    (gen/such-that sc/not-blank  (gen/string-alphanumeric))
                                                    (gen/return "_")
                                                    (gen/return "foobar"))))))

(deftest bool?-test
  (conformer-check sc/bool?))

(deftest not-empty-string?-test
  (conformer-check sc/not-empty-string))

(deftest timestamp?-test
  (conformer-check sc/timestamp?))

(deftest var-timestamp?-test
  (conformer-check (sc/var-timestamp? nil))
  (conformer-check (sc/var-timestamp? :date-time))
  (conformer-check (sc/var-timestamp? :rfc822)))

(deftest date?-test
  (conformer-check sc/date?))

(deftest uuid?-test
  (conformer-check sc/uuid?))

(deftest password?-test
  (conformer-check sc/password?))

(deftest email?-test
  (conformer-check sc/email?))

(deftest url?-test
  (conformer-check sc/email?))

(deftest ns-keyword?-test
  (conformer-check sc/ns-keyword?))

(deftest anything-test
  (conformer-check sc/anything))

(deftest anything-small-test
  (conformer-check sc/anything-small))
