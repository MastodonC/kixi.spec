(ns kixi.datastore.metadatastore.time
  (:require  [clojure.spec.alpha :as s]
             [kixi.spec.conformers :as sc]))

(s/def ::from (s/nilable sc/date))
(s/def ::to (s/nilable sc/date))

(s/def ::temporal-coverage
  (s/keys :opt [::from ::to]))
