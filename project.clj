(defproject conjecture "0.3.0-SNAPSHOT"
  :description "A clojure.test compatible third-party testing library for
  Clojure."
  :url "http://github.com/pjstadig/conjecture"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [conjecture "0.3.0-SNAPSHOT"]]
  :plugins [[lein-conjecture "0.2.0-SNAPSHOT"]]
  :aliases {"test" "conjecture"})
