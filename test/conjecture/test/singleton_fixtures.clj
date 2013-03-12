;;;; Copyright © Paul Stadig. All rights reserved.
;;;;
;;;; The use and distribution terms for this software are covered by the Eclipse
;;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;;;; can be found in the file epl-v10.html at the root of this distribution.
;;;;
;;;; By using this software in any fashion, you are agreeing to be bound by the
;;;; terms of this license.
;;;;
;;;; You must not remove this notice, or any other, from this software.
(ns conjecture.test.singleton-fixtures
  (:require [conjecture.test.core]
            [conjecture.test.fixtures])
  (:use [conjecture.core])
  (:import (java.io PrintWriter StringWriter)))

(deftest test-singleton-fixtures
  (is (= 1 @conjecture.singleton-fixtures/called)))

(defn test-ns-hook []
  (binding [*test-out* (PrintWriter. (StringWriter.))]
    (run-tests 'conjecture.test.core)
    (run-tests 'conjecture.test.fixtures))
  (test-all-vars 'conjecture.test.singleton-fixtures))
