;;;; Copyright © Rich Hickey and Paul Stadig. All rights reserved.
;;;;
;;;; The use and distribution terms for this software are covered by the Eclipse
;;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;;;; can be found in the file epl-v10.html at the root of this distribution.
;;;;
;;;; By using this software in any fashion, you are agreeing to be bound by the
;;;; terms of this license.
;;;;
;;;; You must not remove this notice, or any other, from this software.

;;; test_clojure/test.clj: unit tests for test.clj

;; by Stuart Sierra
;; January 16, 2009

;; Thanks to Chas Emerick, Allen Rohner, and Stuart Halloway for
;; contributions and suggestions.

(ns name.stadig.test.conjecture
  (:use name.stadig.conjecture))

(deftest can-test-symbol
  (let [x true]
    (is x "Should pass"))
  (let [x false]
    (is x "Should fail")))

(deftest can-test-boolean
  (is true "Should pass")
  (is false "Should fail"))

(deftest can-test-nil
  (is nil "Should fail"))

(deftest can-test-=
  (is (= 2 (+ 1 1)) "Should pass")
  (is (= 3 (+ 2 2)) "Should fail"))

(deftest can-test-instance
  (is (instance? Long (+ 2 2)) "Should pass")
  (is (instance? Float (+ 1 1)) "Should fail"))

(deftest can-test-thrown
  (is (thrown? ArithmeticException (/ 1 0)) "Should pass")
  ;; No exception is thrown:
  (is (thrown? Exception (+ 1 1)) "Should fail")
  ;; Wrong class of exception is thrown:
  (is (thrown? ArithmeticException (throw (RuntimeException.))) "Should error"))

(deftest can-test-thrown-with-msg
  (is (thrown-with-msg? ArithmeticException #"Divide by zero" (/ 1 0))
      "Should pass")
  ;; Wrong message string:
  (is (thrown-with-msg? ArithmeticException #"Something else" (/ 1 0))
      "Should fail")
  ;; No exception is thrown:
  (is (thrown? Exception (+ 1 1)) "Should fail")
  ;; Wrong class of exception is thrown:
  (is (thrown-with-msg? IllegalArgumentException #"Divide by zero" (/ 1 0))
      "Should error"))

(deftest can-catch-unexpected-exceptions
  (is (= 1 (throw (Exception.))) "Should error"))

(deftest can-test-method-call
  (is (.startsWith "abc" "a") "Should pass")
  (is (.startsWith "abc" "d") "Should fail"))

(deftest can-test-anonymous-fn
  (is (#(.startsWith % "a") "abc") "Should pass")
  (is (#(.startsWith % "d") "abc") "Should fail"))

(deftest can-test-regexps
  (is (re-matches #"^ab.*$" "abbabba") "Should pass")
  (is (re-matches #"^cd.*$" "abbabba") "Should fail")
  (is (re-find #"ab" "abbabba") "Should pass")
  (is (re-find #"cd" "abbabba") "Should fail"))

(deftest #^{:has-meta true} can-add-metadata-to-tests
  (is (:has-meta (meta #'can-add-metadata-to-tests)) "Should pass"))

;; still have to declare the symbol before testing unbound symbols
(declare does-not-exist)

#_(deftest can-test-unbound-symbol
    (is (= nil does-not-exist) "Should error"))

#_(deftest can-test-unbound-function
    (is (does-not-exist) "Should error"))


;; Here, we create an alternate version of test/report, that
;; compares the event with the message, then calls the original
;; 'report' with modified arguments.

(declare ^:dynamic original-report)

(defn custom-report [data]
  (let [event (:type data)
        msg (:message data)
        expected (:expected data)
        actual (:actual data)
        passed (cond
                (= event :fail) (= msg "Should fail")
                (= event :pass) (= msg "Should pass")
                (= event :error) (= msg "Should error")
                :else true)]
    (if passed
      (original-report {:type :pass, :message msg,
                        :expected expected, :actual actual})
      (original-report {:type :fail, :message (str msg " but got " event)
                        :expected expected, :actual actual}))))

;; test-ns-hook will be used by test/test-ns to run tests in this
;; namespace.
(defn test-ns-hook []
  (binding [original-report report
            report custom-report]
    (test-all-vars (find-ns 'name.stadig.test.conjecture))))

(deftest test-idempotent-fixture
  (let [count (atom 0)
        called? (atom false)
        fx (fn [f] (swap! count inc) (f))
        ifx (idempotent-fixture fx)]
    ((join-fixtures ifx ifx ifx) #(do (is (@has-run? fx) "Should pass")
                                      (reset! called? true)))
    (is (= 1 @count) "Should pass")
    (is (not (@has-run? fx)) "Should pass")
    (is @called? "Should pass")
    (try
      ((join-fixtures ifx ifx ifx) #(throw (Exception.)))
      (is false "Should fail")
      (catch Exception _))
    (is (not (@has-run? fx)) "Should pass")))

(deftest test-idempotent-generator
  (let [order (atom [])
        gen (fn [msg] (fn [f] (swap! order conj msg) (f)))
        gen (idempotent-generator gen)
        called? (atom false)
        ifx (gen "hello")
        ifx2 (gen "goodbye")]
    ((join-fixtures ifx ifx ifx2) #(reset! called? true))
    (is (= ["hello" "goodbye"] @order) "Should pass")
    (is @called? "Should pass")))

(deftest test-join-fixtures
  (let [fixtures (atom [])
        fx1 (fn [f] (swap! fixtures conj :fx1) (f))
        fx2 (fn [f] (swap! fixtures conj :fx2) (f))
        noop (fn [])]
    ((join-fixtures fx1) noop)
    (is (= [:fx1] @fixtures) "Should pass")
    (reset! fixtures [])
    ((join-fixtures [fx1]) noop)
    (is (= [:fx1] @fixtures) "Should pass")
    (reset! fixtures [])
    ((join-fixtures fx1 fx2) noop)
    (is (= [:fx1 :fx2] @fixtures) "Should pass")
    (reset! fixtures [])
    ((join-fixtures [fx1 fx2]) noop)
    (is (= [:fx1 :fx2] @fixtures) "Should pass")))
