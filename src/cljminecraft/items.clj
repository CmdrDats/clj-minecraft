(ns cljminecraft.items
  (:require [cljminecraft.util :as util]
            [cljminecraft.logging :as log])
  (:require [cljminecraft.entity :as ent])
  (:import [org.bukkit TreeSpecies Material])
  (:import [org.bukkit.material
            MaterialData Tree Dispenser Sandstone Bed PoweredRail DetectorRail Wool Chest Crops
            PistonBaseMaterial PistonExtensionMaterial LongGrass Step Torch Stairs RedstoneWire
            Furnace Sign Door Ladder Rails Lever RedstoneTorch Button Pumpkin Cake Diode TrapDoor
            MonsterEggs SmoothBrick Mushroom Vine Gate Cauldron WoodenStep CocoaPlant EnderChest
            TripwireHook Tripwire Command Skull Coal Dye SpawnEgg FlowerPot])
  (:import [org.bukkit.block BlockFace])
  (:import [org.bukkit.inventory ItemStack]))

(def materials (util/map-enums Material))
(def treespecies (util/map-enums TreeSpecies))
(def blockfaces (util/map-enums BlockFace))
(def grassspecies (util/map-enums org.bukkit.GrassSpecies))
(def sandstonetypes (util/map-enums org.bukkit.SandstoneType))
(def dyecolors (util/map-enums org.bukkit.DyeColor))
(def cropstates (util/map-enums org.bukkit.CropState))
(def cocoaplantsizes (util/map-enums org.bukkit.material.CocoaPlant$CocoaPlantSize))
(def coaltypes (util/map-enums org.bukkit.CoalType))

(defn is-block? [block & material-keys]
  (contains? (set (map materials material-keys)) (.getType block)))

(defmulti get-new-data
  "Returns a new MaterialData depending on Material Type"
  (fn [t _ _] t))

(defmacro defmat
  "Convenience macro for the get-new-data multi method, common pattern emerged. I could take it further, but this feels like the right level of abstraction."
  [type & specs]
  (let [sn #(symbol (name %))
        args (map #(sn (second %)) specs)
        lookup-list (filter #(>= (count %) 3) specs)
        spec-let (apply concat (map (fn [[_ n c]] [(sn n) `(get ~c ~(sn n))]) lookup-list))
        spec-set (apply concat (map (fn [[s n]] [`(if ~(sn n) (~s ~(symbol "result") ~(sn n)))]) specs))]
    `(defmethod get-new-data ~type
       [~(symbol "type") ~(symbol "material") [~@args]]
       (let [~@spec-let
             ~(symbol "result") (~(symbol (str type ".")) ~(symbol "material"))]
         ~@spec-set
         ~(symbol "result"))
       )))

(defmacro defdirectional
  "Plenty of types simply have a '.setFacingDirection', so this is a macro to make it a two-worder."
  [type]
  `(defmat ~type [.setFacingDirection ~(symbol "direction") blockfaces]))

(defmat MaterialData
  [.setData data])

(defmat Tree
  [.setSpecies species treespecies]
  [.setDirection direction blockfaces])

(defmat Sandstone
  [.setType sandstonetype sandstonetypes])

(defmat LongGrass
  [.setSpecies species grassspecies])

(defmat Wool
  [.setColor color dyecolors])

(defdirectional Dispenser)
(defdirectional Bed)

(defmethod get-new-data PoweredRail [type material [direction onslope? powered?]]
  (let [result (PoweredRail. material)
        dir (get blockfaces direction)]
    (if dir (.setDirection result dir (if onslope? true false)))
    (if powered? (.setPowered result true))
    result))

(defmethod get-new-data DetectorRail [type material [direction onslope? pressed?]]
  (let [result (DetectorRail. material)
        dir (get blockfaces direction)]
    (if dir (.setDirection result dir (if onslope? true false)))
    (if pressed? (.setPressed result true))
    result))

(defmethod get-new-data Rails [type material [direction onslope?]]
  (let [result (Rails. material)
        dir (get blockfaces direction)]
    (if dir (.setDirection result dir (if onslope? true false)))
    result))

(defmat RedstoneWire
  [.setPowered powered?])

(defdirectional RedstoneTorch)
(defmat Diode
  [.setFacingDirection direction blockfaces]
  [.setDelay delay])

(defmat PistonBaseMaterial
  [.setFacingDirection direction blockfaces])

#_(defmat TripwireHook
  [.setFacingDirection direction blockfaces]
  [.setConnected connected?]
  [.setActivated activated?])

#_(defmat Tripwire
  [.setObjectTriggering triggering?]
  [.setActivated activated?])

(defmat Command
  [.setPowered powered?])

(defdirectional PistonExtensionMaterial)

(defmat WoodenStep
  [.setSpecies species treespecies]
  [.setInverted inverted?])

(defmat Step
  [.setMaterial texture materials]
  [.setInverted inverted?])

(defmat Stairs
  [.setFacingDirection direction blockfaces]
  [.setInverted inverted?])

(defdirectional Torch)
(defdirectional Chest)
(defdirectional EnderChest)

(defmat Crops
  [.setState state cropstates])

(defdirectional Furnace)
(defdirectional Sign)

(defmat Door
  [.setFacingDirection direction blockfaces]
  [.setOpen open?]
  [.setTopHalf tophalf?])

(defmat TrapDoor
  [.setFacingDirection direction blockfaces]
  [.setOpen open?])

(defmat Gate
  [.setFacingDirection direction blockfaces]
  [.setOpen open?])

(defdirectional Ladder)

(defmat Lever
  [.setFacingDirection direction blockfaces]
  [.setPowered powered?])

(defmat Button
  [.setFacingDirection direction blockfaces]
  [.setPowered powered?])

(defdirectional Pumpkin)
(defmat Cake
  [.setSlicesEaten eaten])

(defmat MonsterEggs
  [.setMaterial texture materials])

(defmat SmoothBrick
  [.setMaterial texture materials])
  


(defmethod get-new-data Mushroom [type material [stem? & pntdirs]]
  (let [result (Mushroom. material)]
    (if stem? (.setStem true))
    (doseq [d pntdirs]
      (let [dir (get blockfaces d)]
        (.setPaintedFace result dir)))
    result))

(defmethod get-new-data Vine [type material [& pntdirs]]
  (let [result (Vine. material)]
    (doseq [d pntdirs]
      (let [dir (get blockfaces d)]
        (.putOnFace result dir)))
    result))

(defmat CocoaPlant
  [.setFacingDirection direction blockfaces]
  [.setSize size cocoaplantsizes])

(defmat Coal
  [.setType type coaltypes])

(defmat Dye
  [.setColor color dyecolors])

(defmat SpawnEgg
  [.setSpawnedType type ent/entitytypes])

(declare get-material)

(defmethod get-new-data FlowerPot [type material [content-material]]
  (let [result (FlowerPot. material)]
    (if content-material (.setContents (get-material content-material)))
    result))

;; Whew.

(defn get-material
  "Gets a material from the material map. If you pass it a keyword, it will do a simple lookup. If you pass it a vector [:wood :jungle] or [:water 2], it will return a MaterialData with the material and data setup correctly - Consult the type's multimethod to find what parameters you should pass in."
  [material-key]
  (if (coll? material-key)
    (let [material (get materials (first material-key))
          val (second material-key)]
      (if (number? val)
        (.getNewData material val)
        (get-new-data (.getData material) material (rest material-key))))
    (let [material (get materials material-key)]
      (get-new-data (.getData material) material []))))

(defn item-stack [material-key & [qty]]
  (let [material (get-material material-key)]
    (.toItemStack material (or qty 1))))

(defn drop-item [location itemstack & [naturally?]]
  (if naturally?
    (.dropItemNaturally (.getWorld location) itemstack)
    (.dropItem (.getWorld location) itemstack)))