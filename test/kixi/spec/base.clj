(ns kixi.spec.base
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check :as tc]))

(defn check
  ([sym]
   (check sym 100))
  ([sym samples]
   (-> sym
       (stest/check {:clojure.spec.test.check/opts {:num-tests samples}})
       first
       stest/abbrev-result
       :failure)))
