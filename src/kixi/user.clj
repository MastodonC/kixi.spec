(ns kixi.user
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec]]
            [kixi.spec.conformers :as sc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; User

(s/def ::id (api-spec sc/uuid? "string"))
(s/def ::name spec/string?)
(s/def ::created sc/timestamp?)
(s/def ::username (api-spec sc/email? "string"))
(s/def ::password (api-spec sc/password? "string"))
(s/def ::groups (s/coll-of sc/uuid?))
(s/def ::self-group (api-spec sc/uuid? "string"))

(s/def :kixi/user
  (s/keys :req [::id
                ::groups]
          :opt [::self-group]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Login

(s/def ::login
  (s/keys :req-un [::username ::password]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Auth Token

(s/def ::auth-token spec/string?)
(s/def ::refresh-token spec/string?)
(s/def ::token-pair (s/keys :req-un [::auth-token ::refresh-token]))
(s/def ::token-pair-container (s/keys :req-un [::token-pair]))
