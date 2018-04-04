(ns kixi.collect.request
  (:require [clojure.spec.alpha :as s]
            [kixi.spec.conformers :as sc]
            [kixi.user]))

(s/def ::id sc/uuid?)
(s/def ::ids (s/coll-of ::id :distinct true))
(s/def ::message sc/not-empty-string)
(s/def ::groups  (s/coll-of :kixi.group/id))
(s/def ::sender :kixi/user)
(s/def ::created-at sc/timestamp?)
(s/def ::group-collection-requests
  (s/map-of sc/uuid? sc/uuid?)) ;; group-id (k), collection-request-id (v)
