(ns cljminecraft.logging
  (:require [clojure.tools.logging :as logging]))

(defmacro info [fmt & args]
  `(logging/info (format ~(str (.getName *ns*) ":" fmt) ~@args)))

(defmacro warn [fmt & args]
  `(logging/warn (format ~(str (.getName *ns*) ":" fmt) ~@args)))

(defmacro debug [fmt & args]
  `(logging/debug (format ~(str (.getName *ns*) ":" fmt) ~@args)))