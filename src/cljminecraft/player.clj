(ns cljminecraft.player
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.util :as util]))

(defonce materials (util/map-enums org.bukkit.Material))

;; Various player helper functions
(defn broadcast
  "Broadcast a message to all players"
  [format & args]
  (apply bk/broadcast format args))

(defn broadcast-permission
  "Broadcast a message to all players"
  [permission format & args]
  (apply bk/broadcast permission format args))

(defprotocol HasPlayer
  (get-player [this]))

(defmacro event-player [event-name]
  (let [evclass (util/package-classname "org.bukkit.event" (str event-name "-event"))]
    `(extend-type
         ~(symbol evclass) HasPlayer
         (~(symbol "get-player") [~(symbol "this")] (.getPlayer ~(symbol "this"))))))

(event-player block.block-break)
(event-player block.block-damage)
(event-player block.block-ignite)
(event-player block.block-place)
(event-player block.sign-change)
(event-player hanging.hanging-place)
(event-player inventory.inventory-click)
(event-player inventory.inventory-close)
(event-player inventory.inventory-open)
(event-player painting.painting-place)
(event-player player.player)

(extend-type org.bukkit.inventory.InventoryView
  HasPlayer
  (get-player [this] (.getPlayer this)))

(extend-type String
  HasPlayer
  (get-player [this] (.getPlayer (bk/server) this)))

(extend-type org.bukkit.Bukkit
  HasPlayer
  (get-player [this] (.getPlayer this)))

(extend-type org.bukkit.entity.Player
  HasPlayer
  (get-player [this] this))

(defn send-msg [player & msg]
  (.sendMessage (get-player player) (apply str msg)))

(defn give [player material-key & [qty]]
  (let [stack (org.bukkit.inventory.ItemStack. (get materials material-key) (int (or qty 1)))
        player (get-player player)]
    (.addItem (.getInventory (get-player player)) (into-array org.bukkit.inventory.ItemStack [stack]))))
