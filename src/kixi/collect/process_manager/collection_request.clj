(ns kixi.collect.process-manager.collection-request
  (:require [clojure.spec.alpha :as s]
            [kixi.spec.conformers :as sc]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::id sc/uuid?)
(s/def ::created-at sc/timestamp?)
(s/def ::action (s/with-gen keyword?
                  #(gen/fmap keyword (gen/string-alphanumeric))))
(s/def ::results (s/map-of ::id
                           (s/coll-of ::action :kind set :into #{} :max-count 5)))
