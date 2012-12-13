(ns cljminecraft.files
  (:require [cheshire.core :as json])
  (:require [clojure.java.io :as io]))

(defn data-folder [plugin]
  (.getDataFolder plugin))

(defn read-json-file [plugin filename]
  (try
    (json/decode (slurp (io/file (data-folder plugin) filename)) true)
    (catch java.io.FileNotFoundException e {})
    (catch Exception e (.printStackTrace e) {})))

(defn write-json-file [plugin filename data]
  (let [file (io/file (data-folder plugin) filename)]
    (io/make-parents file)
    (spit file (json/encode data))))