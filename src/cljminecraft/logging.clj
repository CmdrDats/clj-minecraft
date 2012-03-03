(ns cljminecraft.logging
  (:require [clojure.tools.logging :as logging]))

(defmacro info [str]
  `(logging/info (.getName ~(symbol "*ns*")) ":" ~str))

(defmacro warn [str]
  `(logging/warn (.getName ~(symbol "*ns*")) ":" ~str))

(defmacro debug [str]
  `(logging/debug (.getName ~(symbol "*ns*")) ":" ~str))