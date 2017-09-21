(ns kixi.datastore.filestore
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [clojure.spec.gen.alpha :as gen]
            [kixi.spec.conformers :as sc]
            [kixi.spec :refer [api-spec]]))

(s/def ::id (api-spec sc/uuid? "string"))
