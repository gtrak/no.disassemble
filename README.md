# nodisassemble

A Clojure library designed to let you inspect bytecode of functions and things.

## Artifacts 
The Most Recent Release is available on clojars

With Leiningen:

     [nodisassemble "0.1.2"]

HOWEVER, don't use it this way, let lein-nodissassemble's project middleware inject it for you.

    {:plugins [[lein-nodisassemble "0.1.2"]]}

## Usage

no.disassemble is the runtime library created to negotiate with the agent ClassTransformer that stores class bytes globally.

In order to use no.disassemble, add lein-nodisassemble to your :plugins, which will initialize the agent transformer and make bytecode available.

WARNING: there is no cleanup of bytecode yet, so evaling a lot would surely exhaust the heap.


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
    

* It can even disassemble itself

<pre>
    no.disassemble=> (println (disassemble disassemble))
    // Compiled from disassemble.clj (version 1.5 : 49.0, super bit)
    public final class no.disassemble$disassemble extends clojure.lang.AFunction {
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__0;
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__1;
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__2;
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__3;
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__4;
      
      // Field descriptor #13 Lclojure/lang/Keyword;
      public static final clojure.lang.Keyword const__5;
      
      // Field descriptor #7 Lclojure/lang/Var;
      public static final clojure.lang.Var const__6;
      
      // Field descriptor #16 Lclojure/lang/KeywordLookupSite;
      static final clojure.lang.KeywordLookupSite __site__0__;
      
      // Field descriptor #18 Lclojure/lang/ILookupThunk;
      static clojure.lang.ILookupThunk __thunk__0__;
      
      // Method descriptor #20 ()V
      // Stack: 4, Locals: 0
      public static {};
          0  ldc <String "no.disassemble"> [22]
          2  ldc <String "sanitize"> [24]
          4  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
          7  checkcast clojure.lang.Var [32]
         10  putstatic no.disassemble$disassemble.const__0 : clojure.lang.Var [34]
         13  ldc <String "clojure.core"> [36]
         15  ldc <String "class?"> [38]
         17  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
         20  checkcast clojure.lang.Var [32]
         23  putstatic no.disassemble$disassemble.const__1 : clojure.lang.Var [40]
         26  ldc <String "clojure.core"> [36]
         28  ldc <String "class"> [42]
         30  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
         33  checkcast clojure.lang.Var [32]
         36  putstatic no.disassemble$disassemble.const__2 : clojure.lang.Var [44]
         39  ldc <String "clojure.core"> [36]
         41  ldc <String "get"> [46]
         43  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
         46  checkcast clojure.lang.Var [32]
         49  putstatic no.disassemble$disassemble.const__3 : clojure.lang.Var [48]
         52  ldc <String "no.disassemble"> [22]
         54  ldc <String "classes"> [50]
         56  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
         59  checkcast clojure.lang.Var [32]
         62  putstatic no.disassemble$disassemble.const__4 : clojure.lang.Var [52]
         65  aconst_null
         66  ldc <String "detailed"> [54]
         68  invokestatic clojure.lang.RT.keyword(java.lang.String, java.lang.String) : clojure.lang.Keyword [58]
         71  checkcast clojure.lang.Keyword [60]
         74  putstatic no.disassemble$disassemble.const__5 : clojure.lang.Keyword [62]
         77  ldc <String "no.disassemble"> [22]
         79  ldc <String "levels"> [64]
         81  invokestatic clojure.lang.RT.var(java.lang.String, java.lang.String) : clojure.lang.Var [30]
         84  checkcast clojure.lang.Var [32]
         87  putstatic no.disassemble$disassemble.const__6 : clojure.lang.Var [66]
         90  new clojure.lang.KeywordLookupSite [68]
         93  dup
         94  aconst_null
         95  ldc <String "detailed"> [54]
         97  invokestatic clojure.lang.RT.keyword(java.lang.String, java.lang.String) : clojure.lang.Keyword [58]
        100  invokespecial clojure.lang.KeywordLookupSite(clojure.lang.Keyword) [72]
        103  dup
        104  putstatic no.disassemble$disassemble.__site__0__ : clojure.lang.KeywordLookupSite [74]
        107  putstatic no.disassemble$disassemble.__thunk__0__ : clojure.lang.ILookupThunk [76]
        110  return
          Line numbers:
            [pc: 0, line: 19]
      
      // Method descriptor #20 ()V
      // Stack: 1, Locals: 1
      public disassemble$disassemble();
        0  aload_0
        1  invokespecial clojure.lang.AFunction() [78]
        4  return
          Line numbers:
            [pc: 0, line: 19]
      
      // Method descriptor #80 (Ljava/lang/Object;)Ljava/lang/Object;
      // Stack: 9, Locals: 4
      public java.lang.Object invoke(java.lang.Object obj);
          0  getstatic no.disassemble$disassemble.const__0 : clojure.lang.Var [34]
          3  invokevirtual clojure.lang.Var.getRawRoot() : java.lang.Object [84]
          6  checkcast clojure.lang.IFn [86]
          9  getstatic no.disassemble$disassemble.const__1 : clojure.lang.Var [40]
         12  invokevirtual clojure.lang.Var.getRawRoot() : java.lang.Object [84]
         15  checkcast clojure.lang.IFn [86]
         18  aload_1 [obj]
         19  invokeinterface clojure.lang.IFn.invoke(java.lang.Object) : java.lang.Object [88] [nargs: 2]
         24  dup
         25  ifnull 40
         28  getstatic java.lang.Boolean.FALSE : java.lang.Boolean [94]
         31  if_acmpeq 41
         34  aload_1 [obj]
         35  aconst_null
         36  astore_1 [obj]
         37  goto 58
         40  pop
         41  getstatic no.disassemble$disassemble.const__2 : clojure.lang.Var [44]
         44  invokevirtual clojure.lang.Var.getRawRoot() : java.lang.Object [84]
         47  checkcast clojure.lang.IFn [86]
         50  aload_1 [obj]
         51  aconst_null
         52  astore_1 [obj]
         53  invokeinterface clojure.lang.IFn.invoke(java.lang.Object) : java.lang.Object [88] [nargs: 2]
         58  ldc <String "getCanonicalName"> [96]
         60  invokestatic clojure.lang.Reflector.invokeNoArgInstanceMember(java.lang.Object, java.lang.String) : java.lang.Object [102]
         63  invokeinterface clojure.lang.IFn.invoke(java.lang.Object) : java.lang.Object [88] [nargs: 2]
         68  astore_2 [cls_name]
         69  getstatic no.disassemble$disassemble.const__4 : clojure.lang.Var [52]
         72  invokevirtual clojure.lang.Var.getRawRoot() : java.lang.Object [84]
         75  checkcast clojure.lang.IFn [86]
         78  invokeinterface clojure.lang.IFn.invoke() : java.lang.Object [104] [nargs: 1]
         83  aload_2 [cls_name]
         84  aconst_null
         85  astore_2 [cls_name]
         86  invokestatic clojure.lang.RT.get(java.lang.Object, java.lang.Object) : java.lang.Object [107]
         89  astore_3 [bytecode]
         90  new org.eclipse.jdt.internal.core.util.Disassembler [109]
         93  dup
         94  invokespecial org.eclipse.jdt.internal.core.util.Disassembler() [110]
         97  ldc <String "disassemble"> [112]
         99  iconst_3
        100  anewarray java.lang.Object [114]
        103  dup
        104  iconst_0
        105  aload_3 [bytecode]
        106  aconst_null
        107  astore_3 [bytecode]
        108  aastore
        109  dup
        110  iconst_1
        111  ldc <String "\n"> [116]
        113  aastore
        114  dup
        115  iconst_2
        116  getstatic no.disassemble$disassemble.__thunk__0__ : clojure.lang.ILookupThunk [76]
        119  dup
        120  getstatic no.disassemble$disassemble.const__6 : clojure.lang.Var [66]
        123  invokevirtual clojure.lang.Var.getRawRoot() : java.lang.Object [84]
        126  dup_x2
        127  invokeinterface clojure.lang.ILookupThunk.get(java.lang.Object) : java.lang.Object [120] [nargs: 2]
        132  dup_x2
        133  if_acmpeq 140
        136  pop
        137  goto 162
        140  swap
        141  pop
        142  dup
        143  getstatic no.disassemble$disassemble.__site__0__ : clojure.lang.KeywordLookupSite [74]
        146  swap
        147  invokeinterface clojure.lang.ILookupSite.fault(java.lang.Object) : clojure.lang.ILookupThunk [126] [nargs: 2]
        152  dup
        153  putstatic no.disassemble$disassemble.__thunk__0__ : clojure.lang.ILookupThunk [76]
        156  swap
        157  invokeinterface clojure.lang.ILookupThunk.get(java.lang.Object) : java.lang.Object [120] [nargs: 2]
        162  aastore
        163  invokestatic clojure.lang.Reflector.invokeInstanceMethod(java.lang.Object, java.lang.String, java.lang.Object[]) : java.lang.Object [130]
        166  areturn
          Line numbers:
            [pc: 0, line: 19]
            [pc: 0, line: 21]
            [pc: 9, line: 21]
            [pc: 9, line: 21]
            [pc: 9, line: 21]
            [pc: 41, line: 21]
            [pc: 69, line: 22]
            [pc: 69, line: 22]
            [pc: 90, line: 23]
            [pc: 116, line: 23]
          Local variable table:
            [pc: 69, pc: 166] local: cls_name index: 2 type: java.lang.Object
            [pc: 90, pc: 166] local: bytecode index: 3 type: java.lang.Object
            [pc: 0, pc: 166] local: this index: 0 type: java.lang.Object
            [pc: 0, pc: 166] local: obj index: 1 type: java.lang.Object
      
      // Method descriptor #137 (ILclojure/lang/ILookupThunk;)V
      // Stack: 1, Locals: 3
      public void swapThunk(int arg0, clojure.lang.ILookupThunk arg1);
         0  iload_1
         1  tableswitch default: 27
              case 0: 20
        20  aload_2
        21  putstatic no.disassemble$disassemble.__thunk__0__ : clojure.lang.ILookupThunk [76]
        24  goto 27
        27  return
    
    
    }
    nil
    no.disassemble=> 
</pre>


## License

Copyright © 2013 Gary Trakhman

Distributed under the Eclipse Public License, the same as Clojure.
