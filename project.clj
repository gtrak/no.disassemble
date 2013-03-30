(defproject nodisassemble "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.eclipse.jdt/core "3.3.0-v_771"]
                 [org.clojure/clojure "1.4.0"]]

  :java-source-paths ["java"]
  :manifest {"Premain-Class" "no.disassemble.NoDisassemble"
             "Main-Class" "no.disassemble.NoDisassemble"
             "Can-Redefine-Classes" true
             "Can-Retransform-Classes" true}
  )
