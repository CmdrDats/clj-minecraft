(ns cljminecraft.commands
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.player :as plr])
  (:import [org.bukkit.command TabExecutor]))

(defn respond
  [sender fmt & args]
  ;; This needs tweaking to handle responding with info if sender is console?
  (plr/send-msg sender fmt args))

(defmulti convert-type (fn [x _] (if (coll? x) (first x) x)))

(defmethod convert-type :string [type arg] arg)

(defmethod convert-type :int [type arg]
  (try
    (Integer/parseInt arg)
    (catch Exception e nil)))

(defmethod convert-type :player [type arg]
  (plr/get-player arg))

(defmethod convert-type :material [type arg]
  (get plr/materials (keyword arg)))

(defmethod convert-type :long [type arg]
  (try
    (Long/parseLong arg)
    (catch Exception e nil)))

(defmethod convert-type :double [type arg]
  (try
    (Double/parseDouble arg)
    (catch Exception e nil)))

(defmethod convert-type :keyword [type arg]
  (log/info "Keywording %s" arg)
  (keyword arg))

(defmethod convert-type :default [type arg]
  (log/info "Default conversion of %s %s" type arg)
  (str arg))

(defn command [f param-types sender command alias args]
  (log/info "Running command %s" command)
  (let [converted (map convert-type param-types args)
        {:keys [msg] :as response} (apply f converted)]
    (if msg (respond sender msg))) true)

(defmulti param-type-tabcomplete (fn [x _] x))

(defmethod param-type-tabcomplete :player [type arg]
  ["hello"])

(defn to-string-seq [a]
  (for [i (seq a)] (if (keyword? i) (name i) (str i))))

(defn tabcomplete [f param-types sender command alias args]
  (log/info "Tab completing for command %s, %s - %s" command param-types (seq args))
  (let [args (seq args)
        param (get (into [] param-types) (dec (count args)))
        [type opts] (if (coll? param) param [])]
    (log/info "For param %s %s %s %s" param type opts (get (into [] param-types) (dec (count args))))
    (cond
     (nil? param) nil
     (coll? opts) (to-string-seq opts)
     (fn? opts) (opts sender command alias args)
     :else (param-type-tabcomplete (or type param) (last args)))))

(defn build-executor [f param-types]
  (proxy [TabExecutor] []
    (onCommand [sender cmd alias args]
      (command f param-types sender cmd alias args))
    (onTabComplete [sender cmd alias args]
      (tabcomplete f param-types sender cmd alias args))))

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









