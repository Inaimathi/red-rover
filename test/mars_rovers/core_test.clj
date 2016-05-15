(ns mars-rovers.core-test
  (:use [clojure.test.check.clojure-test :only [defspec]])
  (:require [clojure.test :refer :all]

            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]

            [mars-rovers.core :refer :all]))

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

(def a-facing (gen/elements [:N :E :S :W]))

(defspec left-right-is-identity
  (prop/for-all [facing a-facing] (= facing (left (right facing)))))

(defspec left-right-is-commutative
  (prop/for-all [facing a-facing] (= (left (right facing)) (right (left facing)))))

(def a-position (gen/tuple gen/large-integer gen/large-integer))

(def a-rover
  (gen/let [pos a-position
            facing a-facing]
    (mk-rover pos facing)))

(defspec turn-is-the-same-as-calling-left-or-right
  (prop/for-all [rover a-rover]
                (and (= (turn rover :L) (assoc rover :facing (left (rover :facing))))
                     (= (turn rover :R) (assoc rover :facing (right (rover :facing)))))))

(deftest no-border-jumping
  (testing "Can't move west of the plateau min"
    (is (= (mk-rover [0 0] :W)
           (move (mk-plateau [0 0] [1 1])
                 (mk-rover [0 0] :W)))))
  (testing "Can't move south of the plateau min"
    (is (= (mk-rover [0 0] :S)
           (move (mk-plateau [0 0] [1 1])
                 (mk-rover [0 0] :S)))))
  (testing "Can't mover east of the plateau max"
    (is (= (mk-rover [1 1] :E)
           (move (mk-plateau [0 0] [1 1])
                 (mk-rover [1 1] :E)))))
  (testing "Can't mover north of the plateau max"
    (is (= (mk-rover [1 1] :N)
           (move (mk-plateau [0 0] [1 1])
                 (mk-rover [1 1] :N))))))

(def an-instruction (gen/elements [:L :R :M]))

(def a-plateau
  (gen/let [min a-position
            max (gen/such-that (fn [[x y]] (and (>= x (first min)) (>= y (second min)))) a-position)]
    (mk-plateau min max)))

(defspec no-series-of-instructions-moves-a-rover-out-of-the-plateau
  (prop/for-all [instructions (gen/vector an-instruction)
                 facing a-facing]
                (let [plateau (mk-plateau [0 0] [10 10])
                      rover (mk-rover [2 3] facing)
                      final-r (apply-instructions plateau rover instructions)]
                  (and (>= (first (plateau :upper-right))
                           (first (final-r :position))
                           (first (plateau :lower-left)))
                       (>= (second (plateau :upper-right))
                           (second (final-r :position))
                           (second (plateau :lower-left)))))))

(def raw-landing+instruction-pair
  (gen/let [name gen/string-alphanumeric
            pos a-position
            f a-facing
            instructions (gen/vector an-instruction)]
    [name pos f instructions]))

(defn format-instruction-pair [rover-name [x y] facing instructions]
  [(str rover-name " Landing:" x " " y " " (name facing))
   (str rover-name " Instructions:" (apply str (map name instructions)))])

(defspec landing+instruction-pair-contributs-name-and-starting-state
  (prop/for-all [[name pos f instructions] raw-landing+instruction-pair]
                (let [parsed (read-landing+instruction-pair
                              (format-instruction-pair name pos f instructions))]
                  (and (= name (parsed :name))
                       (= (mk-rover pos f) (parsed :rover))
                       (= instructions (parsed :instructions))))))

(def raw-instructions
  (gen/let [max a-position
            pairs (gen/vector raw-landing+instruction-pair)]
    [max pairs]))

(defn format-instruction-lines [[x y] pairs]
  (concat [(str "Plateau:" x " " y)]
          (apply concat (map (partial apply format-instruction-pair) pairs))))

(defspec read-instructions-can-handle-it
  (prop/for-all [[max pairs] raw-instructions]
                (let [parsed (read-instructions
                              (format-instruction-lines max pairs))]
                  (and (= (mk-plateau [0 0] max) (parsed :plateau))
                       (= (count pairs) (count (parsed :rovers)))))))

(def sample-instructions
  "Plateau:5 5
Rover1 Landing:1 2 N
Rover1 Instructions:LMLMLMLMM
Rover2 Landing:3 3 E
Rover2 Instructions:MMRMMRMRRM")

(deftest sample-works
  (testing "The provided sample works as expected"
    (is (let [instructions (read-instructions (clojure.string/split sample-instructions #"\n"))
              [r1 r2] (instructions :rovers)]
          (and (= (mk-rover [1 3] :N)
                  (apply-instructions (instructions :plateau) (r1 :rover) (r1 :instructions)))
               (= (mk-rover [5 1] :E)
                  (apply-instructions (instructions :plateau) (r2 :rover) (r2 :instructions))))))))
