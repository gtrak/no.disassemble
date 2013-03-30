(ns no.disassemble
  (:import [org.eclipse.jdt.internal.core.util Disassembler]))

(defn- classes
  []
  (no.disassemble.NoDisassemble/classes))

(defn- sanitize
  [classname]
  (.replace classname \. \/))

(comment
  org.eclipse.jdt.core.util.ClassFileBytesDisassembler/DETAILED
  org.eclipse.jdt.core.util.ClassFileBytesDisassembler/SYSTEM
  org.eclipse.jdt.core.util.ClassFileBytesDisassembler/WORKING_COPY)

(def levels
  {:detailed 1
   :default 2
   :system 4
   :compact 8 
   :working-copy 16})

(defn disassemble
  [class]
  (let [cls-name (sanitize (.getCanonicalName class))
        bytecode (get (classes) cls-name)]
    (println cls-name)
    (println bytecode)
    (.disassemble (Disassembler.) bytecode "\n" (:detailed levels))))