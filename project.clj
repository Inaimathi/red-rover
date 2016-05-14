(defproject mars-rovers "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [me.raynes/fs "1.4.6"]
                 [me.raynes/conch "0.8.0"]]
  :profiles {:dev
             {:dependencies [[org.clojure/test.check "0.9.0"]
                             [cider/cider-nrepl "0.8.2"]]
              :resource-paths ["test/resources"]}}
  :main mars-rovers.core
  :aot [mars-rovers.core])
