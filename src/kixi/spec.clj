(ns kixi.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
            [spec-tools.core :as st]
            [spec-tools.swagger.core :as swagger])
  (:import [clojure.spec.alpha Spec Specize]))

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

(alias 'c 'clojure.core)

(defn- unfn [expr]
  (if (c/and (seq? expr)
             (symbol? (first expr))
             (= "fn*" (name (first expr))))
    (let [[[s] & form] (rest expr)]
      (conj (walk/postwalk-replace {s '%} form) '[%] 'fn))
    expr))

(defn- ->sym
  "Returns a symbol from a symbol or var"
  [x]
  (if (var? x)
    (let [^clojure.lang.Var v x]
      (symbol (str (.name (.ns v)))
              (str (.sym v))))
    x))

(defn- res [form]
  (cond
    (keyword? form) form
    (symbol? form) (c/or (-> form resolve ->sym) form)
    (sequential? form) (walk/postwalk #(if (symbol? %) (res %) %) (unfn form))
    :else form))

(defn all-preds-spec-impl
  [keys forms preds gfn]
  (let [id (java.util.UUID/randomUUID)
        kps (zipmap keys preds)
        cform (fn [x]
                (loop [[k p] (first kps)]
                  (if (and k p)
                    (let [ret (p x)]
                      (if ret
                        (recur (rest kps))
                        ::s/invalid))
                    x)))]
    (reify
      Specize
      (specize* [s] s)
      (specize* [s _] s)

      Spec
      (conform* [_ x] (cform x))
      (unform* [_ [k x]])
      (explain* [this path via in x]
        (when (= ::s/invalid
                 (cform x))
          (mapcat (fn [k form pred]
                    (when-not (pred x)
                      [{:path (conj path k) :pred form :val x :via via :in in}]))
                  keys forms preds)))
      (gen* [_ overrides path rmap]
        )
      (with-gen* [_ gfn]
        )
      (describe* [_]
        `(or ~@(mapcat vector keys forms))))))

(defmacro all-preds
  [let-block & key-pred-forms]
  (let [pairs (partition 2 key-pred-forms)
        keys (mapv first pairs)
        pred-forms (mapv second pairs)
        pf (mapv res pred-forms)
        preds (mapv (fn [p]
                      `(fn ~let-block ~p)) pf)]
    (c/assert (c/and (even? (count key-pred-forms)) (every? keyword? keys)) "all-preds expects k1 p1 k2 p2..., where ks are keywords")
    `(all-preds-spec-impl ~keys
                          '~preds
                          ~preds nil)))
