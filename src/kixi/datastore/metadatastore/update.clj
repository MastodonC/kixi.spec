(ns kixi.datastore.metadatastore.update
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as spec]
            [com.rpl.specter :as sp]
            [kixi.datastore.metadatastore :as ms]
            [kixi.datastore.metadatastore
             [geography :as geo]
             [license :as l]
             [time :as mdt]]))

(def metadata-types->field-actions
  {["stored" nil] {::ms/name #{:set}
                   ::ms/description #{:set :rm}
                   ::ms/logo #{:set :rm}
                   ::ms/source #{:set :rm}
                   ::ms/author #{:set :rm}
                   ::ms/maintainer #{:set :rm}
                   ::ms/source-created #{:set :rm}
                   ::ms/source-updated #{:set :rm}
                   ::ms/tags #{:conj :disj}
                   ::l/license {::l/usage #{:set :rm}
                                ::l/type #{:set :rm}}
                   ::geo/geography {::geo/level #{:set :rm}
                                    ::geo/type #{:set :rm}}
                   ::mdt/temporal-coverage {::mdt/from #{:set :rm}
                                            ::mdt/to #{:set :rm}}}

   ["bundle" "datapack"] {::ms/name #{:set}
                          ::ms/description #{:set :rm}
                          ::ms/logo #{:set :rm}
                          ::ms/source #{:set :rm}
                          ::ms/author #{:set :rm}
                          ::ms/maintainer #{:set :rm}
                          ::ms/tags #{:conj :disj}
                          ::l/license {::l/usage #{:set :rm}
                                       ::l/type #{:set :rm}}
                          ::geo/geography {::geo/level #{:set :rm}
                                           ::geo/type #{:set :rm}}
                          ::mdt/temporal-coverage {::mdt/from #{:set :rm}
                                                   ::mdt/to #{:set :rm}}}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti metadata-update
  (fn [payload]
    [(::ms/type payload) (::ms/bundle-type payload)]))

(defn update-spec-name
  [spec]
  (keyword (str (namespace spec) ".update") (name spec)))

(defn -rm?
  [x]
  (if (or (= x :rm)
          (= x "rm"))
    :rm
    ::s/invalid))

(s/def ::rm
  (s/with-gen
    (s/conformer -rm? identity)
    (constantly (s/gen #{:rm "rm"}))))

(defn update-spec
  [[spec actions]]
  (let [rmless-actions (disj actions :rm)]
    (eval
     `(s/def ~(update-spec-name spec)
        ~(if (:rm actions)
           (s/or :r ::rm
                 :o (s/map-of rmless-actions
                              spec))
           (s/map-of rmless-actions
                     spec))))))

(defn update-map-spec
  [[map-spec fields]]
  (eval
   `(s/def ~(update-spec-name map-spec)
      (s/and (s/keys
              :opt ~(mapv update-spec-name fields))
             (s/every-kv ~(into #{} (mapv update-spec-name fields))
                         (constantly true))))))

(defn all-specs-with-actions
  [definition-map]
  (distinct
   (sp/select
    [sp/MAP-VALS sp/ALL (sp/if-path [sp/LAST map?]
                                    [sp/LAST sp/ALL]
                                    identity)]
    definition-map)))

(defn sub-maps-with-keys
  [definition-map]
  (distinct
   (mapv (fn [[k v]]
           [k (keys v)])
         (sp/select
          [sp/MAP-VALS sp/ALL (sp/selected? sp/LAST map?)]
          definition-map))))

(defn create-update-specs
  [definition-map]
  (let [update-specs (map update-spec (all-specs-with-actions definition-map))
        map-specs (map update-map-spec (sub-maps-with-keys definition-map))]
    (doall (concat update-specs map-specs))))

(defn define-metadata-update-implementation
  [[dispatch-value spec->action]]
  (let [type-fields (if (nil? (second dispatch-value))
                      [::ms/id ::ms/type]
                      [::ms/id ::ms/type ::ms/bundle-type])]
    `(defmethod metadata-update
       ~dispatch-value
       ~['_]
       (s/and (s/keys :req ~type-fields
                      :opt ~(mapv update-spec-name (keys spec->action)))
              (s/every-kv ~(into (set type-fields) (mapv update-spec-name (keys spec->action)))
                          (constantly true))))))

(defmacro define-metadata-update-implementations
  []
  `(do
     ~@(map define-metadata-update-implementation metadata-types->field-actions)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(create-update-specs metadata-types->field-actions)

(define-metadata-update-implementations)

(s/def ::metadata-update
  (s/multi-spec metadata-update ::metadata-update))
