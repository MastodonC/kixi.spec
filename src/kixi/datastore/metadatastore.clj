(ns kixi.datastore.metadatastore
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [spec-tools.core :as st]
            [kixi.datastore.metadatastore
             [geography :as geo]
             [license :as l]
             [time :as t]]
            [clojure.spec.gen.alpha :as gen]
            [kixi.spec.conformers :as sc]
            [kixi.spec :refer [api-spec api-spec-array api-spec-set api-spec-explicit]]
            [kixi.user :as user]
            [kixi.group :as group]))

(defn valid-file-name?
  "A file name should be at least one valid character long and only have valid characters and start with a digit or letter."
  [s]
  (when (string? s)
    (re-matches #"^[\p{Digit}\p{IsAlphabetic}].{0,512}$" s)))


(s/def ::type #{"stored" "bundle"})
(s/def ::bundle-type #{"datapack"})
(s/def ::file-type (api-spec sc/not-empty-string "string"))
(s/def ::id (api-spec sc/uuid? "string"))
(s/def ::parent-id (api-spec sc/uuid? "string"))
(s/def ::name (api-spec (s/with-gen (s/and sc/not-empty-string valid-file-name?)
                          #(gen/such-that (fn [x] (and (< 0 (count x) 512)
                                                       (re-matches #"^[\p{Digit}\p{IsAlphabetic}]" ((comp str first) x)))) (gen/string) 100))
                        "string"))
(s/def ::description (api-spec sc/not-empty-string "string"))
(s/def ::logo (api-spec sc/url? "string"))
(s/def ::size-bytes (api-spec sc/varint? "integer"))
(s/def ::source #{"upload" "segmentation"})
(s/def ::header (api-spec sc/bool? "boolean"))
(s/def ::created (api-spec sc/timestamp? "string"))

(s/def ::source-created (api-spec sc/date? "string"))
(s/def ::source-updated (api-spec sc/date? "string"))

(s/def ::maintainer (api-spec sc/not-empty-string "string"))
(s/def ::author (api-spec sc/not-empty-string "string"))
(s/def ::source (api-spec sc/not-empty-string "string"))

(s/def ::file-read (api-spec sc/uuid? "string"))
(s/def ::meta-read (api-spec sc/uuid? "string"))
(s/def ::meta-visible (api-spec sc/uuid? "string"))
(s/def ::meta-update (api-spec sc/uuid? "string"))
(s/def ::bundle-add (api-spec sc/uuid? "string"))

(def activities
  [::file-read ::meta-visible ::meta-read ::meta-update
   ::bundle-add])

(s/def ::activities
  (s/coll-of (set activities)))

(defn activity?
  [x]
  ((set activities) (s/conform sc/ns-keyword? x)))

(s/def ::activity
  (st/spec {:spec (s/with-gen activity? #(gen/elements activities))
            :form `::activity
            :json-schema/type "string"
            :json-schema/enum activities}))

(s/def ::sharing
  (api-spec-explicit
   (s/map-of (set activities)
             (s/coll-of ::group/id))
   (s/keys :opt [::file-read ::meta-read ::meta-update ::bundle-add]) ;; TODO ::meta-visible ??
   ::sharing))

(defmulti provenance-type ::source)

(defmethod provenance-type "upload"
  [_]
  (s/keys :req [::source ::user/id ::created]))

(defmethod provenance-type "segmentation"
  [_]
  (s/keys :req [::source ::parent-id ::user/id ::created]))

(s/def ::provenance (api-spec-explicit (s/multi-spec provenance-type ::source)
                                       (s/keys :req [::source ::user/id ::created])
                                       ::provenance))

(s/def ::tags
  (api-spec-set (s/coll-of sc/not-empty-string :kind set?) "string"))

(s/def ::bundled-ids
  (api-spec-set (s/coll-of sc/uuid? :kind set?) "string"))

;; TODO improve these
(s/def ::segmentations (s/coll-of (s/keys)))
(s/def ::segment (s/keys))
(s/def ::schema sc/uuid?)
(s/def ::structural-validation (s/keys))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti file-metadata ::type)

(defmethod file-metadata "stored"
  [_]
  (s/keys :req [::type ::file-type ::id ::name ::provenance ::size-bytes ::sharing]
          :opt [::schema ::segmentations ::segment ::structural-validation ::description
                ::tags ::geo/geography ::t/temporal-coverage
                ::maintainer ::author ::source ::l/license
                ::source-created ::source-updated]))

(defmulti bundle-metadata ::bundle-type)

(defmethod bundle-metadata "datapack"
  [_]
  (s/keys :req [::type ::id ::name ::provenance ::sharing ::bundled-ids ::bundle-type]
          :opt [::description
                ::tags ::geo/geography ::t/temporal-coverage
                ::maintainer ::author ::source ::l/license]))

(defmethod file-metadata "bundle"
  [_]
  (s/multi-spec bundle-metadata ::bundle-type))

(s/def ::file-metadata (s/multi-spec file-metadata ::type))
