(ns cljminecraft.blocks
  (:require [cljminecraft.logging :as log]
            [cljminecraft.items :as i]
            [cljminecraft.player :as plr]
            [cljminecraft.bukkit :as bk]))

(defn left-face [key]
  ({:up :up, :down :down
    :north :east, :east :south
    :south :west, :west :north} key))

(defn right-face [key]
  ({:up :up, :down :down
    :north :west, :west :south
    :south :east, :east :north} key))

(defn opposite-face [key]
  ({:up :down, :down :up
    :north :south, :south :north
    :east :west, :west :east} key))

(defn find-relative-dir [d r]
  ({:north d :south (opposite-face d) :east (left-face d) :west (right-face d) :up :up :down :down} r))


(defmulti run-action (fn [ctx a] (:action a)))
(defn run-actions [ctx & actions]
  (loop [a (first actions)
         r (rest actions)
         context ctx]
    (cond
     (nil? a) context
     (and (coll? a) (not (map? a))) (recur (first a) (concat (rest a) r) context)
     :else
     (recur (first r) (rest r) (run-action context a)))))

(defmacro defaction [name docstring ctx-binding params & method-body]
  (let [params (map #(symbol (.getName (symbol %))) params)]
    `(do
       (defn ~name ~docstring [~@params]
         (zipmap [:action ~@(map keyword params)] [~(keyword name) ~@params]))
       (defmethod run-action ~(keyword name) [~ctx-binding {:keys [~@params]}]
         ~@method-body))))

(defaction move
  "Move the current point in a direction"
  {:keys [origin material painting?] :as ctx} [direction distance]
  (let [[direction distance]
        (if (neg? distance) ;; If we're negative, do the opposite thing.
          [(opposite-face direction) (Math/abs distance)]
          [direction distance])
        d (find-relative-dir (:direction ctx) direction)
        startblock (.getBlock origin)
        m (i/get-material material)]
    (when painting?
      (doseq [i (range (or distance 1))]
        (doto (.getRelative startblock (get i/blockfaces d) i)
          (.setData 0)
          (.setType (.getItemType m))
          (.setData (.getData m)))))
    (assoc ctx :origin (.getLocation (.getRelative startblock (get i/blockfaces d) (or distance 1))))))


(defn forward [& [x]]
  (move :north x))

(defn back [& [x]]
  (move :south x))

(defn left [& [x]]
  (move :east x))

(defn right [& [x]]
  (move :west x))

(defn up [& [x]]
  (move :up x))

(defn down [& [x]]
  (move :down x))


(defaction turn
  "Turn the direction the current context is facing"
  {:keys [direction] :as ctx} [relativedir]
  (assoc ctx :direction (find-relative-dir direction relativedir)))

(defn turn-left []
  (turn :east))

(defn turn-right []
  (turn :west))

(defn turn-around []
  (turn :south))

(defaction pen
  "Do something with the 'pen', set whether it should paint as you move or not"
  ctx [type]
  (case type
    :up (assoc ctx :painting? false)
    :down (assoc ctx :painting? true)
    :toggle (assoc ctx :painting? (not (:painting? ctx)))))

(defn pen-up []
  (pen :up))

(defn pen-down []
  (pen :down))

(defn pen-toggle []
  (pen :toggle))

(defaction material
  "Set the current material to paint with"
  ctx [material-key]
  (assoc ctx :material material-key))

(defaction fork
  "Run actions with ctx but don't update current ctx - effectively a subprocess"
  ctx [actions]
  (run-actions ctx actions)
  ctx)

(defaction mark
  "Stow away the state of a context into a given key"
  {:keys [marks] :as ctx} [mark]
  (assoc ctx :marks (assoc marks mark (dissoc ctx marks))))

(defn gen-mark []
  (.toString (java.util.UUID/randomUUID)))

(defaction jump
  "Jump your pointer to a given mark"
  {:keys [marks] :as ctx} [mark]
  (merge ctx (get marks mark {})))

(defaction copy
  "copy a sphere of a given radius into a mark"
  {:keys [marks origin] :as ctx} [mark radius]
  (let [distance (* radius radius)
        copy-blob
        (doall
         (for [x (range (- 0 radius) (inc radius))
               y (range (- 0 radius) (inc radius))
               z (range (- 0 radius) (inc radius))
               :when (<= (+ (* x x) (* y y) (* z z)) distance)]
           [x y z (.getData (.getState (.getRelative (.getBlock origin) x y z)))]))
        m (get-in ctx [:marks mark] {})]
    (assoc ctx :marks (assoc marks mark (assoc m :copy {:radius radius :blob (doall copy-blob)})))))

(defaction cut
  "Cut a sphere of a given radius into a mark"
  ctx [mark radius material]
  (let [{:keys [origin] :as ctx} (run-action ctx (copy mark radius))
        mat (i/get-material material)
        distance (* radius radius)]
    (doseq [x (range (- 0 radius) (inc radius))
            y (range (- 0 radius) (inc radius))
            z (range (- 0 radius) (inc radius))
            :when (<= (+ (* x x) (* y y) (* z z)) distance)]
      (let [state (.getState (.getRelative (.getBlock origin) x y z))]
        (.setData state material)
        (.update state true)
        ))
    ctx))

(defaction paste
  "Paste a previously copied or cut block against a mark"
  {:keys [origin] :as ctx} [mark]
  (let [{:keys [blob radius]} (get-in ctx [:marks mark :copy] {})]
    (doseq [[x y z data] blob]
      (let [block (.getRelative (.getBlock origin) x y z)]
        (.setTypeIdAndData block (.getItemTypeId data) (.getData data) false)
        ))
    ctx))


(defn copy-to-mark
  "copy a block to a mark"
  [mark]
  {:action :copy-to-mark :mark mark})

(defn cut-to-mark
  "cut a block to a mark, replacing everything with a given material or air if not provided"
  [mark & [material]]
  )

(defn clear-mark [mark]
  {:action :clear-mark})

(defn extrude [direction x & actions]
  (for [c (range x)]
    (fork
     {:action :move :direction direction :distance c}
     actions)))

(defn setup-context [player-name]
  {:origin (.getLocation (plr/get-player player-name))
   :direction :north
   :material :wool
   :painting? true
   :marks {}})

(comment
  (def ctx (setup-context (first (.getOnlinePlayers (bk/server)))))

  (defn floor-part []
    [(forward 5) (turn-right) (forward 1) (turn-right) (forward 5) (turn-left) (forward 1) (turn-left)])

  (defn floor []
    [(floor-part) (floor-part) (floor-part) (floor-part) (floor-part) (floor-part) (floor-part) (floor-part)])


  (run-actions ctx
               (material :air)
               (floor) (turn-around) (up) (floor))

  (run-actions
   ctx
   (material :air)
   (extrude
    :up 10
    (forward 10) (right 10) (back 8) (left 2) (back 2) (left 8))
   )

  (run-actions ctx (material :air) (copy :my-mark 3) (up 5) (paste :my-mark)))

