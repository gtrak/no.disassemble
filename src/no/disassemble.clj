(ns no.disassemble
  (:require [no.disassemble.r :as disassembler])
  (:import [org.eclipse.jdt.internal.core.util Disassembler]))

(defn- classes
  []
  (no.disassemble.NoDisassemble/getClasses))

(defn- sanitize
  [classname]
  (.replace classname \. \/))

(def levels
  {:detailed 1
   :default 2
   :system 4
   :compact 8 
   :working-copy 16})

(defn disassemble-str
  "Emits a string bytecode disassembly of an object or class."
  [obj]
  (let [cls-name (sanitize (.getCanonicalName (if (class? obj) obj (class obj))))
        bytecode (get (classes) cls-name)]
    (.disassemble (Disassembler.) bytecode "\n" (:detailed levels))))

(defn disassemble-data
  "Emits a data structure disassembly of an object or class."
  [obj]
  (disassembler/disassemble obj))

(def disassemble disassemble-str)
