(defproject name.stadig/conjecture "0.1.0"
  :description "A Clojure testing library forked from clojure.test."
  :url "http://github.com/pjstadig/conjecture"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[name.stadig/lein-conjecture "0.1.0"]]
  :aliases {"test" "conjecture"})
