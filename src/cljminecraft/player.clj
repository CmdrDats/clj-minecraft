(ns cljminecraft.player
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.util :as util]
            [cljminecraft.items :as items]))

;; Various player helper functions
(defn broadcast
  "Broadcast a message to all players"
  [format & args]
  (apply bk/broadcast format args))

(defn broadcast-permission
  "Broadcast a message to all players with a given permission"
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

(extend-type org.bukkit.entity.Player
  HasPlayer
  (get-player [this] this))

(defn send-msg [player fmt & args]
  (.sendMessage (get-player player) (apply format fmt args)))

(defn give [player material-key & [qty]]
  (let [stack (items/item-stack material-key qty)
        player (get-player player)]
    (.addItem (.getInventory (get-player player)) (into-array org.bukkit.inventory.ItemStack [stack]))))


(defonce permission-attachments (atom {}))
(defn permission-attach-player!
  "Attach a player to a plugin for permission handling - clj-minecraft handles this automatically, so you shouldn't need to."
  [plugin player]
  (let [plr (get-player player)]
    (if-not (contains? @permission-attachments (.getName plr))
      (swap! permission-attachments assoc (.getName plr) (.addAttachment plr plugin)))))

(defn permission-detach-player!
  "Detach a player from permission handling - clj-minecraft handles this automatically, so you shouldn't need to."
  [player]
  (let [plr (get-player player)
        attach (get @permission-attachments (.getName plr))]
    (try
      (.removeAttachment plr attach)
      (catch Exception e nil))
    (swap! permission-attachments dissoc (.getName plr))))

(defn permission-attach-all!
  [plugin]
  (doseq [plr (.getOnlinePlayers (.getServer plugin))]
    (permission-attach-player! plugin plr)))

(defn permission-detach-all!
  []
  (doseq [plr (keys @permission-attachments)]
    (permission-detach-player! plr)))

(defn defined-permissions []
  (for [plugin (.getPlugins (bk/plugin-manager))
        permission (.getPermissions (.getDescription plugin))]
    (.getName permission)))

(defn command-permissions []
  (for [plugin (.getPlugins (bk/plugin-manager))
        i (.values (.getCommands (.getDescription plugin)))]
    (.get i "permission")))

(defn find-permission
  "Finds permissions starting with a given name"
  [name]
  (let [permissionlist (concat (defined-permissions) (command-permissions))]
    (filter #(.startsWith % name) permissionlist)))

(defn has-permission
  "Check if a player has a permission - this does an implicit (get-player player), so you can pass in a String, Player, Event (that has .getPlayer), InventoryView, etc. (see get-player protocol)"
  [player permission]
  (let [plr (get-player player)]
    (.hasPermission plr permission)))

(defn set-permission
  "This uses the clj-minecraft permission handling to set a permission. This is very simplistic and really for basic permission setting so that you don't need another plugin for the basic permission management - allow-type can be :allow, :disallow or :release - :disallow actively disallows a permission, where :release just unsets it and lets possibly another plugin set it again."
  [player permission allow-type]
  (let [plr (get-player player)
        attach (get @permission-attachments (.getName plr))]
    (case allow-type
      :allow (.setPermission attach permission true)
      :disallow (.setPermission attach permission false)
      :release (.unsetPermission attach permission))))