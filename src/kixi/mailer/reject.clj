(ns kixi.mailer.reject
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec :refer [api-spec alias]]
            [kixi.spec.conformers :as sc]))

(s/def ::reason
  #{:invalid-cmd
    :service-error})

(s/def ::explain spec/string?)
