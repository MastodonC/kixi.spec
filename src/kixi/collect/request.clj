(ns kixi.collect.request
  (:require [clojure.spec.alpha :as s]
            [kixi.spec.conformers :as sc]
            [kixi.user]))

;; id of request
(s/def ::id sc/uuid?)

;; collection of ids
(s/def ::ids (s/coll-of ::id :distinct true :into #{} :kind set))

;; message for requested groups
(s/def ::message sc/not-empty-string)

;; these groups are asked for files
(s/def ::requested-groups (s/coll-of :kixi.group/id ))

;; these groups are given permission to view the files
(s/def ::receiving-groups (s/coll-of :kixi.group/id :distinct true :into #{} :kind set))

;; details of original sender user
(s/def ::sender :kixi/user)

;; the route that the requested groups should be sent
(s/def ::submit-route sc/not-empty-string)

;; when event was created
(s/def ::created-at sc/timestamp?)

;; requested-group-id (k), collection-request-id (v)
(s/def ::group-collection-requests (s/map-of sc/uuid? sc/uuid?))
