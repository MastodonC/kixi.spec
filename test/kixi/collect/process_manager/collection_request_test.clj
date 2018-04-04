(ns kixi.collect.process-manager.collection-request-test
  (:require [kixi.collect.process-manager.collection-request :as sut]
            [kixi.spec.base :refer [event-can-generate?]]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]))

(deftest event-gen-test
  (is (event-can-generate? :kixi.collect.process-manager.collection-request/process-completed "1.0.0"))
  (is (event-can-generate? :kixi.collect.process-manager.collection-request/complete-process-rejected "1.0.0")))

(deftest specs-test
  (is (s/exercise ::sut/id))
  (is (s/exercise ::sut/created-at))
  (is (s/exercise ::sut/action))
  (is (s/exercise ::sut/results)))
