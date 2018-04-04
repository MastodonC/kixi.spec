(ns kixi.collect.request.reject-test
  (:require [kixi.collect.request.reject :as sut]
            [kixi.spec.base :refer :all]
            [clojure.test :refer :all]))

(deftest specs-test
  (is (spec-can-generate? ::sut/reason))
  (is (spec-can-generate? ::sut/message)))
