(ns kixi.spec.random)

(defn uuid
  []
  (str (java.util.UUID/randomUUID)))

(defn string
  ([]
   (string 10))
  ([len]
   (apply str (take len (repeatedly #(if (zero? (rand-int 2)) (char (+ (rand 26) 65)) (char (+ (rand 26) 97))))))))

(defn email
  ([]
   (email 10 10))
  ([pl sl]
   (apply str (string pl) "@" (string sl) "." (string 3))))
