(ns kixi.collect-test
  (:require [kixi.collect :as sut]
            [kixi.spec.base :refer [event-can-generate?]]
            [clojure.test :refer :all]))

(deftest event-gen-test
  (is (event-can-generate? :kixi.collect/collection-requested "1.0.0"))
  (is (event-can-generate? :kixi.collect/collection-request-rejected "1.0.0")))
