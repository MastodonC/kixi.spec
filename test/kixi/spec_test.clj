(ns kixi.spec-test
  (:require [clojure.test :refer :all]
            [kixi.spec :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as st]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(require '[clojure.spec.alpha :as s])

(s/def ::value string?)

(s/def ::first
  (s/keys :req-un [::value]))

(s/def ::second
  (s/keys :req-un [::value]))

(defn what-we-do-now
  [{{:keys [first]} :args second :ret}]
  (= (:value first)
     (:value second)))

(s/fdef fun
        :args (s/cat :f ::first)
        :fn (all-preds [{{:keys [f]} :args s :ret :as a}]
                       :value-mismatch (= (:value f)
                                          (:value s)))
        :ret ::second)

(defn fun
  [{:keys [value] :as first}]
  {:value value})

(defn check
  [sym]
  (-> sym
      (st/check {:clojure.spec.test.alpha.check/opts {:num-tests 100}})
      first
      st/abbrev-result
      :failure))

(deftest does-allpres-work
  (is (nil? (check `fun))))
