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
  (let [cls (if (class? obj) obj (class obj))
        cls-name (sanitize (.getCanonicalName cls))
        bytecode (get (classes) cls-name)]
    (if bytecode
      (.disassemble (Disassembler.) bytecode "\n" (:detailed levels))
      (throw (Exception. (str "Could not load bytecode for " cls ". Is the Java agent enabled?"))))))

(defn disassemble-data
  "Emits a data structure disassembly of an object or class."
  [obj]
  (disassembler/disassemble obj))

(def disassemble disassemble-str)
