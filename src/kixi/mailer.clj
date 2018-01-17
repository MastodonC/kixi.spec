(ns kixi.mailer
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec]]
            [kixi.spec.conformers :as sc]))

(s/def ::to-addresses
  (s/coll-of (api-spec sc/email? "string") :min-count 1 :max-count 50))

(s/def ::to-groups
  (s/coll-of (api-spec sc/uuid? "string") :min-count 1 :max-count 50))

(s/def ::source (api-spec sc/email? "string"))

(s/def ::destination
  (s/or :addresses (s/keys :req-un [::to-addresses])
        :groups (s/keys :req-un [::to-groups])))

(s/def ::subject spec/string?)
(s/def ::text spec/string?)
(s/def ::html spec/string?)

(s/def ::body
  (s/keys :req-un [::text]
          :opt-un [::html]))

(s/def ::message
  (s/keys :req-un [::subject ::body]))
