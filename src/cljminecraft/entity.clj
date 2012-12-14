(ns cljminecraft.entity
  (:require [cljminecraft.util :as util]))

(def entitytypes (util/map-enums org.bukkit.entity.EntityType))

(defn find-entity [nm]
  (let [names (map #(name (first %)) entitytypes)]
    (filter #(.contains % (.toLowerCase nm)) names)))

(defn spawn-entity [location entityname]
  (let [type (get entitytypes (keyword entityname))]
    (when (and type (.isSpawnable type))
      (.spawn (.getWorld location) location type))))










