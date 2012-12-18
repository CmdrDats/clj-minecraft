(ns cljminecraft.repl
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.events :as ev]
            [cljminecraft.entity :as ent]
            [cljminecraft.player :as plr]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.config :as cfg]
            [cljminecraft.commands :as cmd]
            [cljminecraft.recipes :as r]
            [cljminecraft.items :as i]
            [cljminecraft.files :as f]
            [cljminecraft.core :as core]
            ))

;; This is a REPL scratchpatch file. It simply includes everything so
;; that you can play around a build stuff

;; Handy reference to plugin
(def plugin core/clj-plugin)

(def bombs (atom []))

(defn drop-dirt [ev]
  (when (= (.. ev getItemDrop getItemStack getType) (i/get-material :dirt))
    (swap! bombs conj (.getItemDrop ev))
    (plr/send-msg ev "You just dropped a bomb! wooh")))

(defn interact [ev]
  (log/info "Explosion!")
  (doseq [bomb @bombs]
    (let [l (.getLocation bomb)]
      (.createExplosion (.getWorld l) l 10.0)))
  (reset! bombs []))

(defn ondie [ev]
  (.setDeathMessage ev "You fool!"))

(let [m (i/get-material [:wool :yellow])]
  (doseq [x (range 10)
          y (range 10)]
    (let [l (.getLocation (plr/get-player "CmdrDats"))]
      (.setY l (- (.getY l) 1))
      (.setX l (- (.getX l) x))
      (.setZ l (- (.getZ l) y))
      (if (not= (.getType (.getBlock l)) (i/get-material :air))
        (.sendBlockChange (plr/get-player "DatsGuest") l (.getItemType m) (.getData m))))))

(ev/register-event @plugin "player.player-drop-item" #'drop-dirt)
(ev/register-event @plugin "player.player-interact" #'interact)
(ev/register-event @plugin "entity.player-death" #'ondie)
