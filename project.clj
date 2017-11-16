(defproject kixi/kixi.spec "0.1.13"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta1"]
                 [org.clojure/test.check "0.9.0"]
                 [com.rpl/specter "1.0.2"]
                 [clj-time "0.12.0"]
                 [metosin/spec-tools "0.3.3"]]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
