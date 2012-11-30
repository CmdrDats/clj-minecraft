(ns cljminecraft.state)

(defonce server (atom nil))

(defn get-server [plugin]
  {:plugin plugin
   :server (.getServer plugin)
   :plugin-manager (.. plugin getServer getPluginManager)
   :desc (.getDescription plugin)})

(defn register-server [srv]
  (reset! server srv))