(ns kixi.collect.campaign-test
  (:require [kixi.collect.campaign :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [kixi.spec.base :refer :all]))

(deftest specs-test
  (is (spec-can-generate? ::sut/id))
  (is (spec-can-generate? ::sut/created-at)))
