(ns kixi.collect.request.reject
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [kixi.spec.conformers :as sc]))

(s/def ::reason
  #{:invalid-cmd
    :unauthorised
    :incorrect-type})

(s/def ::message spec/string?)
