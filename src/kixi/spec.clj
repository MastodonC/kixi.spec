(ns kixi.spec
  (:require [spec-tools.core :as st]))

;; This macro allows us to give type hints to swagger
;; when using complex specs
(defmacro api-spec
  [symb typename]
  `(st/create-spec {:spec ~symb
                    :form '~symb
                    :json-schema/type ~typename}))

;; From https://github.com/gfredericks/schpec
(defn alias
  "Like clojure.core/alias, but can alias to non-existing namespaces"
  [alias namespace-sym]
  (try (clojure.core/alias alias namespace-sym)
       (catch Exception _
         (create-ns namespace-sym)
         (clojure.core/alias alias namespace-sym))))
