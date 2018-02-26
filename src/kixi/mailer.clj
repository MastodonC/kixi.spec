(ns kixi.mailer
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec alias]]
            [kixi.spec.conformers :as sc]
            ;;
            [kixi.mailer.destination :as d]
            [kixi.mailer.message :as m]
            [kixi.mailer.reject :as reject]))

(s/def ::source (api-spec sc/email? "string"))

(s/def ::destination
  (s/or :addresses (s/keys :req [::d/to-addresses])
        :groups (s/keys :req [::d/to-groups])))

(s/def ::message
  (s/keys :req [::m/subject ::m/body]))
