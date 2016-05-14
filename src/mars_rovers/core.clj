(ns mars-rovers.core
  (:require [clojure.string :as str])
  (:gen-class))

;; Facing = N | E | S | W
;; Direction = L | R
;; Plateau = { :lower-left [Int Int] :upper-right [Int Int] }

(defn mk-rover [position facing]
  "[Int Int] -> Facing -> Rover"
  { :position position :facing facing })

(defn mk-plateau [min max]
  { :lower-left min :upper-right max })

(defn move [plateau rover]
  "Plateau -> Rover -> Rover"
  (let [[x y] (rover :position)
        [min-x min-y] (plateau :lower-left)
        [max-x max-y] (plateau :upper-right)
        facing (rover :facing)
        within (fn [lower n upper] (min (max n lower) upper))]
    (mk-rover
     [(case facing
        :E (within min-x (+ x 1) max-x)
        :W (within min-x (- x 1) max-x)
        x)
      (case facing
        :N (within min-y (+ y 1) max-y)
        :S (within min-y (- y 1) max-y)
        y)]
     (rover :facing))))

(defn right [facing]
  "Facing -> Facing"
  (case facing
    :N :E
    :E :S
    :S :W
    :W :N))

(defn left [facing]
  (right (right (right facing))))

(defn turn [rover direction]
  "Rover -> Direction -> Rover"
  (assoc
   rover :facing
   (case direction
     :L (left (rover :facing))
     :R (right (rover :facing)))))

;; Instruction = L | R | M

(defn apply-instruction [plateau rover instruction]
  "Plateau -> Rover -> Instruction -> Rover"
  (if (= :M instruction)
    (move plateau rover)
    (turn rover instruction)))

(defn apply-instructions [plateau rover instructions]
  "Plateau -> Rover -> [Instruction] -> Rover"
  (reduce (partial apply-instruction plateau) rover instructions))

;; Instructions = {max-x :: Int, max-y :: Int,
;;                 rovers :: [{ name :: String, rover :: Rover, instructoins :: [Instruction] }]}

(defn read-landing+instruction-pair [[landing-line instruction-line]]
  "[String String] -> {name :: String, rover :: Rover, instructoins :: [Instruction]}"
  (let [[tag init-str] (str/split landing-line #":")
        init (vec (map read-string (str/split init-str #" ")))]
    {:name (first (str/split tag #" "))
     :rover (mk-rover [(get init 0) (get init 1)] (keyword (get init 2)))
     :instructions
     (map (comp keyword str)
          (second (str/split instruction-line #":")))}))

(defn read-instructions [lines]
  "String -> Instructions"
  (let [top-left (map read-string (str/split (second (str/split (first lines) #":")) #" "))]
    {:plateau (mk-plateau [0 0] (vec top-left))
     :rovers (map read-landing+instruction-pair (partition 2 (rest lines)))}))

(defn print-rover [rover-entry]
  "{ name :: String, rover :: Rover, ... } -> IO ()"
  (let [rover (rover-entry :rover)
        [x y] (rover :position)]
    (println (str (rover-entry :name) ":" x " " y " " (name (rover :facing))))))

(defn -main []
  "IO ()"
  (let [instructions (doall (read-instructions (vec (line-seq (java.io.BufferedReader. *in*)))))]
    (doseq [r (instructions :rovers)]
      (print-rover
       (assoc r :rover (apply-instructions
                        (instructions :plateau)
                        (r :rover)
                        (r :instructions)))))))
