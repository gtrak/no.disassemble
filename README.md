# nodisassemble

A Clojure library designed to let you inspect bytecode of functions and things.

## Usage

no.disassemble is the runtime library created to negotiate with the agent ClassTransformer that stores class bytes globally.

In order to use no.disassemble, add lein-nodisassemble to your :plugins, which will initialize the agent transformer and make bytecode available.

WARNING: there is no cleanup of bytecode yet, so evaling a lot would surely exhaust the heap.


    user=> (require 'no.disassemble.NoDisassemble
    no.disassemble.NoDisassemble   
    user=> (require 'no.disassemble)
    nil
    
    user=> (in-ns 'no.disassemble)
    #<Namespace no.disassemble>
    
    no.disassemble=> (disassemble (fn []))
    "// Compiled from NO_SOURCE_FILE (version 1.5 : 49.0, super bit)\npublic final class no.disassemble$eval1170$fn__1171 extends clojure.lang.AFunction {\n  \n  // Method descriptor #7 ()V\n  // Stack: 0, Locals: 0\n  public static {};\n    0  return\n      Line numbers:\n        [pc: 0, line: 1]\n  \n  // Method descriptor #7 ()V\n  // Stack: 1, Locals: 1\n  public disassemble$eval1170$fn__1171();\n    0  aload_0\n    1  invokespecial clojure.lang.AFunction() [10]\n    4  return\n      Line numbers:\n        [pc: 0, line: 1]\n  \n  // Method descriptor #12 ()Ljava/lang/Object;\n  // Stack: 1, Locals: 1\n  public java.lang.Object invoke();\n    0  aconst_null\n    1  areturn\n      Line numbers:\n        [pc: 0, line: 1]\n      Local variable table:\n        [pc: 0, pc: 1] local: this index: 0 type: java.lang.Object\n\n}"

    no.disassemble=> (println (disassemble (fn [])))
    // Compiled from NO_SOURCE_FILE (version 1.5 : 49.0, super bit)
    public final class no.disassemble$eval1174$fn__1175 extends clojure.lang.AFunction {
      
      // Method descriptor #7 ()V
      // Stack: 0, Locals: 0
      public static {};
        0  return
          Line numbers:
            [pc: 0, line: 1]
      
      // Method descriptor #7 ()V
      // Stack: 1, Locals: 1
      public disassemble$eval1174$fn__1175();
        0  aload_0
        1  invokespecial clojure.lang.AFunction() [10]
        4  return
          Line numbers:
            [pc: 0, line: 1]
      
      // Method descriptor #12 ()Ljava/lang/Object;
      // Stack: 1, Locals: 1
      public java.lang.Object invoke();
        0  aconst_null
        1  areturn
          Line numbers:
            [pc: 0, line: 1]
          Local variable table:
            [pc: 0, pc: 1] local: this index: 0 type: java.lang.Object
    
    }
    nil
    no.disassemble=> 
    

## License

Copyright © 2013 Gary Trakhman

Distributed under the Eclipse Public License, the same as Clojure.
