(ns kixi.datastore.metadatastore.time
  (:require  [clojure.spec.alpha :as s]
             [kixi.spec :refer [api-spec]]
             [kixi.spec.conformers :as sc]))

(s/def ::from (api-spec (s/nilable sc/date?) "string"))
(s/def ::to (api-spec (s/nilable sc/date?) "string"))

(s/def ::temporal-coverage
  (s/keys :opt [::from ::to]))
