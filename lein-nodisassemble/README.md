# lein-nodisassemble

A Leiningen plugin to add no.disassemble to your project and startup the jvm instrumentation properly.  Should only be used in a dev profile of some sort.

## Artifacts
The Most Recent Release is deployed to clojars

With Leiningen:

     {:plugins [[lein-nodisassemble "0.1.2"]]}

## Usage

Put `[lein-nodisassemble "0.1.2"]` into the `:plugins` vector of your project.clj.

Look at the documentation for no.disassemble for usage.

## License

Copyright © 2013 Gary Trakhman

Distributed under the Eclipse Public License, the same as Clojure.
