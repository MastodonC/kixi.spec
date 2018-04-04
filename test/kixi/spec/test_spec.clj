(ns kixi.spec.test-spec
  (:require [clojure.spec.alpha :as s]
            [kixi.collect]))

(def c-types    (set (map first  (keys (methods kixi.comms/command-payload)))))
(def c-versions (set (map second (keys (methods kixi.comms/command-payload)))))
(def e-types    (set (map first  (keys (methods kixi.comms/event-payload)))))
(def e-versions (set (map second (keys (methods kixi.comms/event-payload)))))

(s/def :kixi.command/type c-types)
(s/def :kixi.command/version c-versions)
(s/def :kixi.event/type e-types)
(s/def :kixi.event/version e-versions)
