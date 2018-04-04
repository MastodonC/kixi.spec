(ns kixi.collect.campaign
  (:require [clojure.spec.alpha :as s]
            [kixi.spec.conformers :as sc]))

(s/def ::id sc/uuid?)
(s/def ::created-at sc/timestamp?)
