(ns kixi.spec.conformers
  (:require [clojure.core :exclude [integer? double? set?]]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.test.check.generators :as tgen]
            [clojure.string :as str]
            [clj-time.core :as t]
            [clj-time.format :as tf]))

(defn double->int
  "1.0 will convert to 1; anything else will be rejected"
  [d]
  (let [int-val (int d)]
    (when (== int-val d)
      int-val)))

(defn str->double
  "Strings converted to doubles"
  [^String s]
  (try
    (Double/valueOf (str s))
    (catch Exception e
      ::s/invalid)))

(defn str-double->int
  "1, 1. or 1.0...0 will convert to 1"
  [^String s]
  (try
    (double->int (str->double s))
    (catch Exception e
      nil)))

(defn str->int
  [^String s]
  (try
    (Integer/valueOf s)
    (catch NumberFormatException e
      (or
       (str-double->int s)
       ::s/invalid))))

(def not-blank #(not (clojure.string/blank? %)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Integer

(defn -integer?
  [x]
  (cond (string? x) (str->int x)
        (clojure.core/integer? x) x
        (and (clojure.core/double? x)
             (double->int x))     (double->int x)
        :else ::s/invalid))

(def integer?
  (s/with-gen
    (s/conformer -integer? identity)
    (constantly (gen/int))))

(defn -varint?
  [x]
  (cond
    (int? x) x
    (instance? clojure.lang.BigInt x) x
    :else ::s/invalid))

(def varint?
  "big or small int"
  (s/with-gen
    (s/conformer -varint? identity)
    (constantly (gen/int))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Double

(defn -double?
  [x]
  (cond
    (clojure.core/double? x) x
    (clojure.core/integer? x) (double x)
    (string? x) (str->double x)
    :else ::s/invalid))

(def double?
  (s/with-gen
    (s/conformer -double? identity)
    (constantly (gen/double))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Integer Range

(defn -integer-range?
  [min max]
  (let [rcheck (fn [v] (if (>= max v min)
                         v
                         ::s/invalid))]
    (fn [x]
      (let [r (-integer? x)]
        (if (= r ::s/invalid)
          r
          (rcheck r))))))

(defn integer-range?
  [min max]
  (when (or (not (int? min))
            (not (int? max)))
    (throw (IllegalArgumentException. "Both min and max must be integers")))
  (s/with-gen
    (s/conformer (-integer-range? min max) identity)
    #(gen/elements (range min max))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Double Range

(defn -double-range?
  [min max]
  (let [rcheck (fn [v] (if (>= max v min)
                         v
                         ::s/invalid))]
    (fn [x]
      (let [r (-double? x)]
        (if (= r ::s/invalid)
          r
          (rcheck r))))))

(defn double-range?
  [min max]
  (when (or (not (clojure.core/double? min))
            (not (clojure.core/double? max)))
    (throw (IllegalArgumentException. "Both min and max must be doubles")))
  (s/with-gen
    (s/conformer (-double-range? min max) identity)
    #(gen/elements (range min max 0.1))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Set

(defn -set?
  [sargs]
  (fn [x]
    (if (sargs x) x ::s/invalid)))

(defn set?
  [& sargs]
  (s/with-gen
    (s/conformer (-set? (set sargs)) identity)
    #(gen/elements sargs)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Regex

(defn -regex?
  [rs]
  (fn [x]
    (if (and (string? x) (re-matches rs x))
      x
      ::s/invalid)))

(defn regex?
  [rs gen]
  (let [msg (str rs " is not a valid regex.")]
    (if (or (= (type rs) java.util.regex.Pattern)
            (string? rs))
      (try
        (s/with-gen
          (s/conformer (-regex? (re-pattern rs)) identity)
          gen)
        (catch java.util.regex.PatternSyntaxException _
          (throw (IllegalArgumentException. msg))))
      (throw (IllegalArgumentException. msg)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bool

(defn -bool?
  [x]
  (cond
    (boolean? x) x
    (string? x) (case x
                  ("true" "TRUE" "t" "T") true
                  ("false" "FALSE" "f" "F") false
                  ::s/invalid)
    :else ::s/invalid))

(def bool?
  (s/with-gen (s/conformer -bool? identity)
    (constantly (gen/boolean))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; String

(defn -string?
  [x]
  (cond
    (string? x) x
    :else (str x)))

(defn -not-empty-string?
  [x]
  (if (and (string? x)
           (not-empty x)) x
      ::s/invalid))

(def not-empty-string
  (s/with-gen
    (s/conformer -not-empty-string? identity)
    #(gen/not-empty (s/gen string?))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DateTime

(def format :basic-date-time)
(def date-format :basic-date)

(def formatter
  (tf/formatters format))

(def date-formatter
  (tf/formatters date-format))

(def time-parser
  (partial tf/parse formatter))

(def time-unparser
  (partial tf/unparse formatter))

(def date-parser
  (partial tf/parse date-formatter))

(def date-unparser
  (partial tf/unparse date-formatter))

(defn -timestamp?
  [x]
  (if (instance? org.joda.time.DateTime x)
    x
    (try
      (if (string? x)
        (time-parser x)
        ::s/invalid)
      (catch IllegalArgumentException e
        ::s/invalid))))

(def timestamp?
  (s/with-gen
    (s/conformer -timestamp? time-unparser)
    #(gen/return (time-unparser (t/now)))))

(defn -var-timestamp?
  [format]
  (fn [x]
    (if (instance? org.joda.time.DateTime x)
      x
      (try
        (if (string? x)
          (tf/parse (tf/formatters format) x)
          ::s/invalid)
        (catch IllegalArgumentException e
          ::s/invalid)))))

(defn var-timestamp?
  [fmt]
  (let [format (or fmt :basic-date-time)]
    (s/with-gen
      (s/conformer (-var-timestamp? format)
                   (partial tf/unparse (tf/formatters format)))
      #(gen/return (tf/unparse (tf/formatters format) (t/now))))))

(defn midnight-timestamp?
  [x]
  (and (instance? org.joda.time.DateTime x)
       (zero? (t/hour x))
       (zero? (t/minute x))
       (zero? (t/milli x))))

(defn -date?
  [x]
  (if (or (instance? org.joda.time.DateMidnight x)
          (midnight-timestamp? x))
    x
    (try
      (if (string? x)
        (date-parser x)
        ::s/invalid)
      (catch IllegalArgumentException e
        ::s/invalid))))

(def date?
  (s/with-gen
    (s/conformer -date? date-unparser)
    #(gen/return (date-unparser (t/today-at-midnight)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Uuid / Passwords / Emails

(def -uuid?
  (-regex? #"^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))

(def uuid?
  (s/with-gen
    (s/conformer -uuid? identity)
    #(tgen/no-shrink (gen/fmap str (gen/uuid)))))

(def password?
  "At least 8 characters, one upper, one lower and one digit."
  (regex? #"(?=.*\d.*)(?=.*[a-z].*)(?=.*[A-Z].*).{8,}"
          #(gen/fmap (comp (partial apply str) shuffle flatten)
                     (gen/tuple (gen/vector (gen/elements (range 0 9)))
                                (gen/vector (gen/fmap char (gen/elements (range 65 91))))
                                (gen/vector (gen/fmap char (gen/elements (range 97 123))))))))

(defn fmt-email
  [[prefix host tld]]
  (str (str/lower-case prefix) "@" (str/lower-case host) "." (str/lower-case tld)))

(def email-re-str
  "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,63}")

;; regex from here http://www.lispcast.com/clojure.spec-vs-schema
(def -email?
  (-regex? (re-pattern (str "^" email-re-str "$"))))

(def email?
  (s/with-gen
    (s/conformer -email? identity)
    #(gen/fmap fmt-email
               (gen/tuple
                (gen/such-that not-blank  (gen/string-alphanumeric))
                (gen/such-that not-blank  (gen/string-alphanumeric))
                (gen/one-of [(gen/return "com")
                             (gen/return "co.uk")
                             (gen/return "net")])))))

(defn fmt-url
  [[secure? domains paths extension]]
  (str "http"
       (when secure? "s")
       "://"
       (clojure.string/join "." domains)
       "/"
       (when (not-empty paths) (clojure.string/join "/" paths))
       (when extension (str "." extension))))

(def -url?
  (-regex? #"^http[s]?:\/\/([^:\/\s]+)((\/\w+)*\/)([\w\-\.]+[^#?\s]+)(.*)?(#[\w\-]+)?$"))

(def url?
  (let [not-blank #(not (clojure.string/blank? %))]
    (s/with-gen
      (s/conformer -url? identity)
      #(gen/fmap fmt-url
                 (gen/tuple
                  (gen/boolean)
                  (gen/vector (gen/such-that not-blank  (gen/string-alphanumeric)) 2 5)
                  (gen/vector (gen/such-that not-blank  (gen/string-alphanumeric)) 2 5)
                  (gen/one-of [(gen/such-that not-blank (gen/string-alphanumeric))
                               (gen/return nil)]))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Namespaced Keyword

(defn -ns-keyword?
  [x]
  (cond
    (and (keyword? x)
         (namespace x)) x
    (string? x) (try
                  (let [kw (apply keyword (clojure.string/split x #"/" 2))]
                    (if (namespace kw)
                      kw
                      ::s/invalid))
                  (catch Exception e
                    ::s/invalid))
    :else ::s/invalid))

(def ns-keyword?
  (s/with-gen
    (s/conformer -ns-keyword? identity)
    #(gen/such-that namespace (gen/keyword-ns))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Other

(def anything
  (s/with-gen (constantly true)
    #(gen/any)))

(def anything-small
  (let [mix-gens [(gen/string-alphanumeric)
                  (gen/int)
                  (gen/boolean)]]
    (s/with-gen (constantly true)
      #(gen/one-of (conj mix-gens
                         (gen/vector (gen/one-of mix-gens)))))))
