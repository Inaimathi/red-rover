(ns mars-rovers.core
  (:require [clojure.string :as str])
  (:gen-class))

;; Facing = N | E | S | W
;; Direction = L | R

(defn mk-rover [position facing]
  "[Int Int] -> Facing -> Rover"
  { :position position :facing facing })

(defn move [minimum maximum rover]
  "[Int Int] -> [Int Int] -> Rover -> Rover"
  (let [[x y] (rover :position)
        [min-x min-y] minimum
        [max-x max-y] maximum
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
;; RoverInstructions = { name :: String, rover :: Rover, instructoins :: [Instruction] }
;; Instructions = { max-x :: Int, max-y :: Int, rovers :: [RoverInstructions] }

(defn read-landing+instruction-pair [max-x max-y [landing-line instruction-line]]
  (let [[tag init-str] (str/split landing-line #":")
        init (vec (map read-string (str/split init-str #" ")))]
    {:name (first (str/split tag #" "))
     :rover (mk-rover [(get init 0) (get init 1)] (keyword (get init 2)))
     :instructions
     (map (comp keyword str)
          (second (str/split instruction-line #":")))}))

(defn read-instructions [lines]
  "String -> Instructions"
  (let [[max-x max-y] (map read-string (str/split (second (str/split (first lines) #":")) #" "))]
    {:max-x max-x
     :max-y max-y
     :rovers
     (map (partial read-landing+instruction-pair max-x max-y)
          (partition 2 (rest lines)))}))

(defn -main []
  (doall
   (let [instructions (read-instructions (vec (line-seq (java.io.BufferedReader. *in*))))]
     (println (str instructions)))))
