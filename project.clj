(defproject name.stadig/conjecture "0.3.0-SNAPSHOT"
  :description "A clojure.test compatible third-party testing library for
  Clojure."
  :url "http://github.com/pjstadig/conjecture"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[name.stadig/lein-conjecture "0.1.0"]]
  :aliases {"test" "conjecture"})
