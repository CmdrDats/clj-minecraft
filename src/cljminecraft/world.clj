(ns cljminecraft.world
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.player :as plr])
  (:import [org.bukkit.material Directional]))

(defn facing-block
  "If this is a directional block, will return the block it's facing, otherwise it will return the block itself"
  [block]
  (let [data (.. block getState getData)]
    (if (instance? Directional data)
      (.getFace block (.getFacing data))
      block)))

