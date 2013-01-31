(ns cljminecraft.entity
  (:require [cljminecraft.util :as util]))

(def entitytypes (util/map-enums org.bukkit.entity.EntityType))

(defn get-entities [world & {:keys [type-keys living?]}]
  (cond
   type-keys
   (let [entclasses
         (filter #(not (nil? %))
                 (map #(if-let [t (get entitytypes %)] (.getEntityClass t))
                      (if (coll? type-keys) type-keys [type-keys])))]
     (if (not-empty entclasses)
       (.getEntitiesByClasses world (into-array Class entclasses))
       []))
   living?
   (.getLivingEntities world)
   :else
   (.getEntities world)))

(defn find-entity [nm]
  (let [names (map #(name (first %)) entitytypes)]
    (filter #(.contains % (.toLowerCase nm)) names)))

(defn spawn-entity [location entityname]
  (let [type (get entitytypes (keyword entityname))]
    (when (and type (.isSpawnable type))
      (.spawnEntity (.getWorld location) location type))))
