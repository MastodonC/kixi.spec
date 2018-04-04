(ns kixi.spec
  (:require [spec-tools.core :as st]
            [spec-tools.swagger.core :as swagger]))

(defn transform-keys [m]
  (reduce-kv (fn [x y z] (assoc x (keyword "json-schema" (name y)) z)) {} m))

;; This macro allows us to give type hints to swagger
;; when using complex specs
(defmacro api-spec
  [symb typename]
  `(st/create-spec {:spec ~symb
                    :form '~symb
                    :json-schema/type ~typename}))

(defmacro api-spec-array
  [symb typename]
  `(st/create-spec {:spec ~symb
                    :form '~symb
                    :json-schema/type "array"
                    :json-schema/items {:type ~typename}}))

(defmacro api-spec-set
  [symb typename]
  `(st/create-spec {:spec ~symb
                    :form '~symb
                    :json-schema/type "array"
                    :json-schema/items {:type ~typename
                                        :uniqueItems true}}))

(defmacro api-spec-explicit
  ([symb spec]
   `(st/create-spec (merge {:spec ~symb
                            :form '~symb}
                           (transform-keys (swagger/transform ~spec)))))
  ([symb spec title]
   `(st/create-spec (merge {:spec ~symb
                            :form '~symb
                            :json-schema/title ~title}
                           (transform-keys (swagger/transform ~spec))))))

;; From https://github.com/gfredericks/schpec
(defn alias
  "Like clojure.core/alias, but can alias to non-existing namespaces"
  [alias namespace-sym]
  (try (clojure.core/alias alias namespace-sym)
       (catch Exception _
         (create-ns namespace-sym)
         (clojure.core/alias alias namespace-sym))))

;; Functions that we use commonly for dispatching messages.
;; Enables:
;;    (defmulti foo event-type-version-pair)
;;    (defmethod foo [:kixi.event/foo "1.0.0"] [...] )
(def event-dispatch (juxt :kixi.event/type :kixi.event/version))
(def command-dispatch (juxt :kixi.command/type :kixi.command/version))
