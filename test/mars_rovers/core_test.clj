(ns mars-rovers.core-test
  (:use [clojure.test.check.clojure-test :only [defspec]])
  (:require [clojure.test :refer :all]

            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]

            [mars-rovers.core :refer :all]))

(def a-position
  (gen/let [x gen/int y gen/int]
    [x y]))

(def a-rover
  (gen/let [pos a-position
            facing (gen/elements [:N :E :S :W])]
    (mk-rover pos facing)))

(def a-plateau
  (gen/let [min a-position
            max a-position]
    (mk-plateau min max)))

(deftest right-test
  (testing "Right of :N is :E"
    (is (= :E (right :N))))
  (testing "Right of :E is :S"
    (is (= :S (right :E))))
  (testing "Right of :S is :W"
    (is (= :W (right :S))))
  (testing "Right of :W is :N"
    (is (= :N (right :W)))))

(deftest left-test
  (testing "Left of :N is :W"
    (is (= :W (left :N))))
  (testing "Left of :W is :S"
    (is (= :S (left :W))))
  (testing "Left of :S is :E"
    (is (= :E (left :S))))
  (testing "Left of :E is :N"
    (is (= :N (left :E)))))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

(defspec addition-is-commutative
  (prop/for-all [a gen/int
                 b gen/int]
     (= (+ a b) (+ b a))))
