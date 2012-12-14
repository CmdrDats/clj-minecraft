(ns cljminecraft.commands
  (:require [cljminecraft.bukkit :as bk]
            [cljminecraft.util :as util]
            [cljminecraft.logging :as log]
            [cljminecraft.player :as plr]
            [cljminecraft.events :as ev]
            [cljminecraft.entity :as ent]
            [cljminecraft.items :as items]
            )
  (:import [org.bukkit.command TabExecutor]))

(defn respond
  [sender fmt & args]
  (.sendMessage sender (apply format fmt args)))

(defmulti convert-type (fn [_ x _] (if (coll? x) (first x) x)))

(defmethod convert-type :string [sender type arg] arg)

(defmethod convert-type :int [sender type arg]
  (try
    (Integer/parseInt arg)
    (catch Exception e (util/throw-runtime "Invalid integer: %s" arg))))

(defmethod convert-type :player [sender type arg]
  (let [result (plr/get-player arg)]
    (if (nil? result) (util/throw-runtime "Invalid player: %s" arg)
        result)))

(defmethod convert-type :material [sender type arg]
  (let [result (items/get-material (:keyword arg))]
    (if (nil? result) (util/throw-runtime "Invalid material: %s" arg)
        result)))

(defmethod convert-type :long [sender type arg]
  (try
    (Long/parseLong arg)
    (catch Exception e (util/throw-runtime "Invalid long: %s" arg))))

(defmethod convert-type :double [sender type arg]
  (try
    (Double/parseDouble arg)
    (catch Exception e (util/throw-runtime "Invalid double: %s" arg))))

(defmethod convert-type :keyword [sender type arg]
  (keyword arg))

(defmethod convert-type :default [sender type arg]
  (str arg))

(defmethod convert-type :event [sender type arg]
  (str arg))

(defmethod convert-type :entity [sender type arg]
  (str arg))

(defmulti param-type-tabcomplete (fn [_ x _] x))

(defmethod param-type-tabcomplete :player [sender type arg]
  (let [lower (.toLowerCase arg)]
    (map #(.getDisplayName %)
         (filter #(.startsWith (.toLowerCase (org.bukkit.ChatColor/stripColor (.getDisplayName %))) lower)
                 (bk/online-players)))))

(defmethod param-type-tabcomplete :material [sender type arg]
  (let [lower (.toLowerCase arg)]
    (filter #(.startsWith % lower) (map name (keys items/materials)))))

(defmethod param-type-tabcomplete :event [sender type arg]
  (ev/find-event arg))

(defmethod param-type-tabcomplete :entity [sender type arg]
  (ent/find-entity arg))

(defn arity-split [args]
  (split-with #(not= '& %) args))

(defn arity-count-match [cnt args]
  (let [[req opt] (arity-split args)]
    (if (empty? opt)
      (= cnt (count req))
      (>= cnt (count req)))))

(defn check-arity [f arg-count]
  (let [arglists (:arglists (meta f))
        count-matched (map (partial #'arity-count-match arg-count) arglists)]
    (empty? (filter false? count-matched))))

(defn command [f param-types sender command alias args]
  (log/info "Running command %s" command)
  (try
    (cond
     (not (check-arity f (count (concat [sender] args)))) (respond sender "Incorrect number of arguments for %s: %d" alias (count args))
     :else
     (let [converted (map (partial convert-type sender) param-types args)
           {:keys [msg] :as response} (apply f sender converted)]
       (log/info "Responding with %s" response)
       (if msg (respond sender msg))))
    (catch RuntimeException e (.printStackTrace e) (respond sender "An error occurred with this command")))
  true)

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
     (or (var? opts) (fn? opts)) (opts sender command alias args)
     :else (param-type-tabcomplete sender (or type param) (last args)))))

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









