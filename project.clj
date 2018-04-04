(defproject kixi/kixi.spec "0.1.25-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.9.0"]
                 [com.rpl/specter "1.1.0"]
                 [clj-time "0.14.2"]
                 [metosin/spec-tools "0.5.1"]
                 [kixi/kixi.comms "0.2.37" :scope "provided"]]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
