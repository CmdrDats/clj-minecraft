(ns cljminecraft.world
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.player :as plr])
  (:require [cljminecraft.util :as util])
  (:import [org.bukkit.material Directional]
           [org.bukkit Location]
           [org.bukkit.util Vector]))

(def effects (util/map-enums org.bukkit.Effect))
(def sounds (util/map-enums org.bukkit.Sound))
(def treetypes (util/map-enums org.bukkit.TreeType))

(defn facing-block
  "If this is a directional block, will return the block it's facing, otherwise it will return the block itself"
  [block]
  (let [data (.. block getState getData)]
    (if (instance? Directional data)
      (.getFace block (.getFacing data))
      block)))

(defn explode
  "Creates an explosion at a given location"
  [location power & [fire?]]
  (.createExplosion (.getWorld location) location power (or fire? false)))

(defn coerce-vector
  "Coerces a [x y z] vec a org.bukkit.util.Vector"
  [vec]
  ;; TODO: Look into making this an open type - ie, either protocol or defmulti
  (cond
   (instance? Vector vec) vec
   (coll? vec)
   (let [[x y z] vec] (Vector. x y z))
   :else
   (Vector.)))

(defn arrow
  "Pew! direction is either a org.bukkit.util.Vector or a [x y z] vec"
  [location direction & [speed spread]]
  (let [vector (coerce-vector direction)]
    (.spawnArrow (.getWorld location) vector (or speed 0.6) (or spread 12))))

(defn lightning
  [location & [effectonly?]]
  (if effectonly?
    (.strikeLightningEffect (.getWorld location) location)
    (.strikeLightning (.getWorld location) location)))

(defn effect [location effect-key data & [radius]]
  (if radius
    (.playEffect (.getWorld location) location effect data radius)
    (.playEffect (.getWorld location) location effect data)))

(defn sound [location sound-key volume pitch]
  (if-let [sound (get sounds sound-key)]
    (.playSound (.getWorld location) location sound volume pitch)))

(defn generate-tree [location treetype-key & [change-delegate]]
  (let [world (.getWorld location)
        treetype (get treetypes treetype-key)]
    (if change-delegate
      (.generateTree location treetype change-delegate)
      (.generateTree location treetype))))


