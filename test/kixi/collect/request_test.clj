(ns kixi.collect.request-test
  (:require [kixi.collect.request :as sut]
            [clojure.spec.alpha :as s]
            [kixi.spec.base :refer :all]
            [clojure.test :refer :all]))

(deftest specs-test
  (is (spec-can-generate? ::sut/id))
  (is (spec-can-generate? ::sut/ids))
  (is (spec-can-generate? ::sut/message))
  (is (spec-can-generate? ::sut/groups))
  (is (spec-can-generate? ::sut/sender))
  (is (spec-can-generate? ::sut/created-at))
  (is (spec-can-generate? ::sut/group-collection-requests)))
