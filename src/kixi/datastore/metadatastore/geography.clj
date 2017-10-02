(ns kixi.datastore.metadatastore.geography
  (:require  [clojure.spec.alpha :as s]
             [spec-tools.spec :as spec]
             [kixi.spec :refer [api-spec-explicit]]
             [kixi.spec.conformers :as sc]))

(s/def ::level spec/string?)
(s/def ::type #{"smallest"})

(defmulti geography ::type)

(defmethod geography "smallest"
  [_]
  (s/keys :opt [::type
                ::level]))

(s/def ::geography
  (api-spec-explicit (s/multi-spec geography ::type)
                     (s/keys :opt [::type
                                   ::level])
                     ::geography))
