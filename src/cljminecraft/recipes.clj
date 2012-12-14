(ns cljminecraft.recipes
  (:require [cljminecraft.player :as plr])
  (:require [cljminecraft.bukkit :as bk])
  (:require [cljminecraft.items :as items])
  (:import [org.bukkit.inventory ShapedRecipe ShapelessRecipe ItemStack]))

(defn shapeless [material-map result ingredients qty]
  (let [result (ShapelessRecipe. (items/item-stack result qty))]
    (doseq [c ingredients]
      (.addIngredient result (items/get-material (get material-map c))))
    result))

(defn shaped [material-map result ingredients qty]
  (let [result (ShapedRecipe. (items/item-stack result qty))]
    (.shape result (into-array String ingredients))
    (doseq [[c mkey] material-map]
      (try
        (.setIngredient result (char c) (items/get-material mkey))
        (catch Exception e
          ;; Wow, dumb idea to throw an exception if the ingredient
          ;; doesn't appear in a shape.
          )))
    result))

(defn recipe [material-map result ingredients & [qty]]
  (if (coll? ingredients)
    (shaped material-map result ingredients (or qty 1))
    (shapeless material-map result ingredients (or qty 1))))

(defn register-recipes
  [& recipes]
  (doseq [recipe recipes]
    (.addRecipe (bk/server) recipe)))

#_(def material-map
  {\W :wood \D :dirt \S :stone \O :compass})

#_(register-recipes
 (recipe material-map :ink_sack "WW" 2)
 (recipe material-map :monster_egg [" D " "DWD" " D "]))