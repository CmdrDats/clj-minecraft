(ns cljminecraft.commands
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log])
  (:import [org.bukkit.command TabExecutor]))

(defn build-executor [f param-types]
  (proxy TabExecutor []
    (onCommand [sender command alias args]
      (apply f args))
    (onTabComplete [sender command alias args])))

(defn register-command
  "Registers a command function, providing a function name, type and parameter types"
  [plugin command-name f & param-types]
  (let [executor (build-executor f param-types)]
    (doto (.getCommand plugin command-name)
      (.setExecutor executor)
      (.setTabCompleter executor))))

(comment
  ;; Example usage of register-command
  (defn give-money [sender receiver amount]
    (log/info "Taking %s money from %s and giving to %s" amount sender receiver))

  ;; The types here infer validation rules and tab-completion - The
  ;; receiving function will be guarenteed to be passed a Player
  ;; object and an integer instead of two strings.
  (register-command plugin "transfer" #'give-money :player :int))