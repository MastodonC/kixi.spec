(ns kixi.mailer.message
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec alias]]
            [kixi.spec.conformers :as sc]))

(s/def ::subject spec/string?)
(s/def ::text spec/string?)
(s/def ::html spec/string?)

(s/def ::body
  (s/keys :req [::text]
          :opt [::html]))
