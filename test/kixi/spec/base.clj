(ns kixi.spec.base
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check :as tc]
            [kixi.spec :refer [event-dispatch]]))

(defn check
  ([sym]
   (check sym 100))
  ([sym samples]
   (-> sym
       (stest/check {:clojure.spec.test.check/opts {:num-tests samples}})
       first
       stest/abbrev-result
       :failure)))

(defn generate-event
  [t v]
  (gen/generate (s/gen (s/and :kixi/event #(= [t v] (event-dispatch %))))))

(defn event-can-generate?
  [t v]
  (let [c 100]
    (= c (count (s/exercise (s/and :kixi/event #(= [t v] (event-dispatch %))) c)))))

(defn spec-can-generate?
  [sp]
  (let [c 100]
    (= c (count (s/exercise sp c)))))
