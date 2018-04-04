(ns kixi.collect.process-manager.collection-request-test
  (:require [kixi.collect.process-manager.collection-request :as sut]
            [kixi.spec.base :refer :all]
            [clojure.test :refer :all]))

(deftest event-gen-test
  (is (event-can-generate? :kixi.collect.process-manager.collection-request/process-completed "1.0.0"))
  (is (event-can-generate? :kixi.collect.process-manager.collection-request/complete-process-rejected "1.0.0")))

(deftest specs-test
  (is (spec-can-generate? ::sut/id))
  (is (spec-can-generate? ::sut/created-at))
  (is (spec-can-generate? ::sut/action))
  (is (spec-can-generate? ::sut/results)))
