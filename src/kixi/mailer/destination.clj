(ns kixi.mailer.destination
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec alias]]
            [kixi.spec.conformers :as sc]))

(s/def ::to-addresses
  (s/coll-of (api-spec sc/email? "string") :min-count 1 :max-count 50))

(s/def ::to-groups
  (s/coll-of (api-spec sc/uuid? "string") :min-count 1 :max-count 50))
