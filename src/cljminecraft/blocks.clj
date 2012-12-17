(ns cljminecraft.blocks
  (:require [cljminecraft.items :as i]))

(defn turn-direction
  "Set the current facing direction, relative to the current direction (imagine current direction as north)"
  [blockface-direction])

(defn move-direction
  "Move relative to the origin and current facing direction"
  [[x y z]]
  )

(defn up [& [x]]
  (move-direction [0 (- 0 (or x 1)) 0]))

(defn down [& [x]]
  (move-direction [0 (or x 1) 0]))

(defn left [x]
  (move-direction [(- 0 (or x 1)) 0 0]))

(defn right [x]
  (move-direction [(or x 1) 0 0]))

(defn forward [x]
  (move-direction [0 0 (or x 1)]))

(defn back [x]
  (move-direction [0 0 (- 0 (or x 1))]))

(defn turn-left []
  (turn-direction :east))

(defn turn-right []
  (turn-direction :west))

(defn turn-up []
  (turn-direction :up))

(defn turn-down []
  (turn-direction :down))

(defn turn-around []
  (turn-direction :south))

(defn material
  "Set the current 'painted' material :none, or nil for 'pen up', ie - no effect."
  [])

(defn penup [])

(defn pendown [])

(defn fork
  "Run the given actions seperate to the main context. This could run parallel to your main process, but it generally means that your position is left unchanged"
  [& actions])

(defn extend-actions
  "For the to-action, do the extended action after every movement in the to-action.."
  [extended-actions & to-actions]
  (for [to-action to-actions]
    [to-action
     (fork extended-action)]))

(defn test-fn
  "Test an expression, given the current context - executing actions in the true of false block as required"
  [fn true-block & [false-block]])

(defn if-material
  "Decision branch depending on material"
  [material-key true-block & [false-block]])

(defn run-actions
  [origin direction])


(comment
  "To define a house:"
  (defn wall [length height]
    [(fork
      (extend-actions
       [(forward length)]
       (for [_ (range heigth)] (up))))
     (penup)
     (forward length)
     ])

  (defn house-walls [width length height]
    [(wall width height)
     (turn-right)
     (wall length height)
     (turn-right)
     (wall width height)
     (turn-right)])

  (extend-actions
      [(forward width)
       (right length)
       (back width)
       (left length)]
      (up height))

  (let [marker (generate-marker)]
    (mark marker)
    (foward width)
    (right length)
    (up height)
    (fill-to-mark marker :air)
    (extend-actions
      [(forward width)
       (right length)
       (back width)
       (left length)]
      (up height))))



(let [shuffled (shuffle players)]
  (dosomething)
  (doelsfklds))