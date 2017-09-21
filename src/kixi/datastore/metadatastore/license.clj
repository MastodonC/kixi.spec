(ns kixi.datastore.metadatastore.license
  (:require  [clojure.spec.alpha :as s]
             [spec-tools.spec :as spec]
             [kixi.spec.conformers :as sc]))

(s/def ::usage spec/string?)
(s/def ::type spec/string?)

(s/def ::license
  (s/keys :opt [::type ::usage]))
