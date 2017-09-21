(ns kixi.group
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec]]
            [kixi.spec.conformers :as sc]))

(s/def ::id (api-spec sc/uuid? "string"))
(s/def ::name spec/string?)
(s/def ::created sc/timestamp?)
(s/def ::created-by (api-spec sc/uuid? "string"))
(s/def ::type #{"user" "group"})
