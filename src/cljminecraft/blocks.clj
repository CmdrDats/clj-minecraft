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

(defn move [{:keys [origin direction material painting?] :as ctx} & [relativedir x]]
  (let [d (find-relative-dir direction relativedir)
        startblock (.getBlock origin)
        m (i/get-material material)]
    (when painting?
      (doseq [i (range (or x 1))]
        (doto (.getRelative startblock (get i/blockfaces d) i)
          (.setData 0)
          (.setType (.getItemType m))
          (.setData (.getData m)))))
    (assoc ctx :origin (.getLocation (.getRelative startblock (get i/blockfaces d) (or x 1))))))

(defn turn [{:keys [direction] :as ctx} & [relativedir]]
  (assoc ctx :direction (find-relative-dir direction relativedir)))

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

(defmethod run-action :move [ctx {:keys [direction distance]}]
  (move ctx direction distance))

(defn forward [& [x]]
  {:action :move :direction :north :distance x})

(defn back [& [x]]
  {:action :move :direction :south :distance x})

(defn left [& [x]]
  {:action :move :direction :east :distance x})

(defn right [& [x]]
  {:action :move :direction :west :distance x})

(defn up [& [x]]
  {:action :move :direction :up :distance x})

(defn down [& [x]]
  {:action :move :direction :down :distance x})


(defmethod run-action :turn [ctx {:keys [direction]}]
  (turn ctx direction))

(defn turn-left []
  {:action :turn :direction :east})

(defn turn-right []
  {:action :turn :direction :west})

(defn turn-around []
  {:action :turn :direction :south})

(defmethod run-action :pen [ctx {:keys [type]}]
  (case type
    :up (assoc ctx :painting? false)
    :down (assoc ctx :painting? true)
    :toggle (assoc ctx :painting? (not (:painting? ctx)))))

(defn pen-up []
  {:action :pen :type :up})

(defn pen-down []
  {:action :pen :type :down})

(defn pen-toggle []
  {:action :pen :type :toggle})

(defmethod run-action :material [ctx {:keys [matkey]}]
  (assoc ctx :material matkey))

(defn material [material-key]
  {:action :material :matkey material-key})

(defmethod run-action :mark [{:keys [marks origin direction] :as ctx} {:keys [uuid]}]
  (assoc ctx :marks (assoc marks uuid (dissoc ctx marks))))

(defmethod run-action :jump [{:keys [marks] :as ctx} {:keys [uuid clear]}]
  (let [mark (get marks uuid {})]
    (merge (if clear (update-in ctx [:marks] dissoc uuid) ctx) mark)))

(defn gen-mark []
  (.toString (java.util.UUID/randomUUID)))

(defn mark [m]
  {:action :mark :uuid m})

(defn jump [m & [clear-mark]]
  {:action :jump :uuid m :clear clear-mark})

(defn extrude [direction x & actions]
  (let [m (gen-mark)]
    (for [c (range x)]
      [(mark m)
       actions
       (jump m true)
       {:action :move :direction direction :distance 1}]
      )))

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
   (floor)
   (extrude
    :up 10
    (forward 10) (right 10) (back 8) (left 2) (back 2) (left 8))
   (floor)))
