(def version (:version (read-string (slurp "build-meta.edn"))))

(defproject nodisassemble version
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.eclipse.jdt/org.eclipse.jdt.core "3.7.1"]
;                 [org.eclipse.equinox/app "1.0.0-v20070606"]
                 [org.clojure/clojure "1.4.0"]]

  :java-source-paths ["java"]
  :manifest {"Premain-Class" "no.disassemble.NoDisassemble"
;;             "Can-Redefine-Classes" true
;;             "Can-Retransform-Classes" true
             }

  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]

  )
