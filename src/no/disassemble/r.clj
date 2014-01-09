(ns no.disassemble.r
  "Low-level diassembly of classes. Uses the Eclipse jdt disassembler, but
  coerces its object graph into idiomatic Clojure data structures.
  
  The motherlode: http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.7/org.eclipse.jdt/core/3.7.0/org/eclipse/jdt/internal/core/util/Disassembler.java"
  (:refer-clojure :exclude [methods])
  (:import (org.eclipse.jdt core.JavaCore
                            core.Signature
                            core.compiler.CharOperation
                            internal.compiler.codegen.AttributeNamesConstants
                            internal.compiler.lookup.TypeConstants)
           (org.eclipse.jdt.core.util IClassFileReader
                                      IClassFileAttribute
                                      IConstantPoolEntry
                                      IConstantPoolConstant
                                      IAnnotationComponent
                                      ISourceAttribute
                                      IAnnotationDefaultAttribute
                                      ICodeAttribute
                                      ILocalVariableAttribute
                                      IRuntimeVisibleParameterAnnotationsAttribute
                                      IAnnotation
                                      IStackMapTableAttribute
                                      ILocalVariableTableEntry
                                      IExceptionTableEntry
                                      IAnnotationComponentValue
                                      ISignatureAttribute
                                      IInnerClassesAttributeEntry
                                      ILineNumberAttribute
                                      IStackMapFrame
                                      IRuntimeInvisibleParameterAnnotationsAttribute
                                      IRuntimeVisibleAnnotationsAttribute
                                      ILocalVariableTypeTableEntry
                                      ILocalVariableTypeTableAttribute
                                      IStackMapAttribute
                                      IConstantValueAttribute
                                      IEnclosingMethodAttribute
                                      IVerificationTypeInfo
                                      IRuntimeInvisibleAnnotationsAttribute
                                      IInnerClassesAttribute
                                      IParameterAnnotation
                                      IExceptionAttribute
                                      IFieldInfo
                                      IBytecodeVisitor
                                      IMethodInfo
                                      IModifierConstants)
           (org.eclipse.jdt.internal.core.util Messages
                                               ClassFileReader)
           ))

(def constant-modifiers "Maps keywords to IModifierConstants"
  {:public        IModifierConstants/ACC_PUBLIC
   :private       IModifierConstants/ACC_PRIVATE
   :protected     IModifierConstants/ACC_PROTECTED
   :static        IModifierConstants/ACC_STATIC
   :final         IModifierConstants/ACC_FINAL
   :super         IModifierConstants/ACC_SUPER
   :synchronized  IModifierConstants/ACC_SYNCHRONIZED
   :volatile      IModifierConstants/ACC_VOLATILE
   :bridge        IModifierConstants/ACC_BRIDGE
   :transient     IModifierConstants/ACC_TRANSIENT
   :varargs       IModifierConstants/ACC_VARARGS
   :native        IModifierConstants/ACC_NATIVE
   :interface     IModifierConstants/ACC_INTERFACE
   :abstract      IModifierConstants/ACC_ABSTRACT
   :strict        IModifierConstants/ACC_STRICT
   :synthetic     IModifierConstants/ACC_SYNTHETIC
   :annotation    IModifierConstants/ACC_ANNOTATION
   :enum          IModifierConstants/ACC_ENUM})

(defn modifiers->kw
  "Given a set of legal modifier keywords, and an IModifierConstant integer,
  returns a set of keywords that integer represents, like :public, :varags,
  :native, :annotation, etc."
  [modifiers i]
  (assert i)
  (->> modifiers
       (remove (fn [modifier]
                 (if-let [m (get constant-modifiers modifier)]
                   (zero? (bit-and i m))
                   (throw (IllegalArgumentException.
                            (str "Unknown modifier " (prn-str modifier)))))))
       (into (sorted-set))))

(defn constant-kind->kw
  "Given an integer constant kind, returns a keyword like :class, :string,
  :name-and-type, etc from the constants in IConstantPoolConstant."
  [kind]
  (condp = kind
    IConstantPoolConstant/CONSTANT_Class               :class
    IConstantPoolConstant/CONSTANT_Fieldref            :fieldref
    IConstantPoolConstant/CONSTANT_Methodref           :methodref
    IConstantPoolConstant/CONSTANT_InterfaceMethodref  :interface-methodref
    IConstantPoolConstant/CONSTANT_String              :string
    IConstantPoolConstant/CONSTANT_Integer             :integer
    IConstantPoolConstant/CONSTANT_Float               :float
    IConstantPoolConstant/CONSTANT_Long                :long
    IConstantPoolConstant/CONSTANT_Double              :double
    IConstantPoolConstant/CONSTANT_NameAndType         :name-and-type
    IConstantPoolConstant/CONSTANT_Utf8                :utf8))

; Coerce disassembler classes to normal clojure data structures.
(defprotocol Coerce
  (coerce [this]))

; These protocols are required for the bytecode visitor
(extend-protocol Coerce
  nil
  (coerce [_] nil)

  Object
  (coerce [x] x)
  
  IConstantPoolEntry
  (coerce [c]
    (let [kind (constant-kind->kw (.getKind c))]
      (assoc
        (case kind
          :class {:class-info-name (String. (.getClassInfoName c))}
          :fieldref {:class-name       (String. (.getClassName c))
                     :field-name       (String. (.getFieldName c))
                     :field-descriptor (String. (.getFieldDescriptor c))}
          :methodref {:class-name         (String. (.getClassName c))
                      :method-name        (String. (.getMethodName c))
                      :method-descriptor  (String. (.getMethodDescriptor c))}
          :interface-methodref {:class-name         (String. (.getClassName c))
                                :method-name        (String. (.getMethodName c))
                                :method-descriptor  (String.
                                                      (.getMethodDescriptor c))}
          :string   {:value (.getStringValue c)}
          :integer  {:value (.getIntegerValue c)}
          :float    {:value (.getFloatValue c)}
          :double   {:value (.getDoubleValue c)}
          :long     {:value (.getLongValue c)}
          :name-and-type {:descriptor-index
                          (.getNameAndTypeInfoDescriptorIndex c)
                          :info-name-index (.getNameAndTypeInfoNameIndex c)}
          :utf8     {:value (String. (.getUtf8Value c) "UTF-8")})
        :kind kind))))

(defmacro defbytecode-visitor
  "Takes a list of expressions like

  _aload (int pc, int index)

  and generates a BytecodeVisitor deftype which traverses bytecode and builds
  up a sequence of Clojure data structures representing each op. Deref returns
  that sequence. Basically just means I can copy-paste the interface with
  minimal changes into the source here."
  [classname & specs]
  `(deftype ~classname [~'ops]
     clojure.lang.IDeref
     (deref [~'this] (deref ~'ops))

     IBytecodeVisitor
     ~@(->> specs
            (partition 2)
            (map (fn [[fun args]]
              (let [; Keyword name for fun, without leading _
                    kw   (-> fun
                             name
                             (.substring 1)
                             keyword)
                    ; Drop types
                    args (keep-indexed #(when (odd? %1) %2) args)
                    
                    values (map (partial list 'coerce) args)]
                `(~fun [~'this ~@args]
                       (swap! ~'ops conj [~kw ~@values]))))))))

(defbytecode-visitor BytecodeVisitor
  _aaload (int pc)
  _aastore (int pc)
  _aconst_null (int pc)
  _aload (int pc, int index)
  _aload_0 (int pc)
  _aload_1 (int pc)
  _aload_2 (int pc)
  _aload_3 (int pc)
  _anewarray (
              int pc,
              int index,
              IConstantPoolEntry constantClass)
  _areturn (int pc)
  _arraylength (int pc)
  _astore (int pc, int index)
  _astore_0 (int pc)
  _astore_1 (int pc)
  _astore_2 (int pc)
  _astore_3 (int pc)
  _athrow (int pc)
  _baload (int pc)
  _bastore (int pc)
  _bipush (int pc, byte _byte)
  _caload (int pc)
  _castore (int pc)
  _checkcast (
              int pc,
              int index,
              IConstantPoolEntry constantClass)
  _d2f (int pc)
  _d2i (int pc)
  _d2l (int pc)
  _dadd (int pc)
  _daload (int pc)
  _dastore (int pc)
  _dcmpg (int pc)
  _dcmpl (int pc)
  _dconst_0 (int pc)
  _dconst_1 (int pc)
  _ddiv (int pc)
  _dload (int pc, int index)
  _dload_0 (int pc)
  _dload_1 (int pc)
  _dload_2 (int pc)
  _dload_3 (int pc)
  _dmul (int pc)
  _dneg (int pc)
  _drem (int pc)
  _dreturn (int pc)
  _dstore (int pc, int index)
  _dstore_0 (int pc)
  _dstore_1 (int pc)
  _dstore_2 (int pc)
  _dstore_3 (int pc)
  _dsub (int pc)
  _dup (int pc)
  _dup_x1 (int pc)
  _dup_x2 (int pc)
  _dup2 (int pc)
  _dup2_x1 (int pc)
  _dup2_x2 (int pc)
  _f2d (int pc)
  _f2i (int pc)
  _f2l (int pc)
  _fadd (int pc)
  _faload (int pc)
  _fastore (int pc)
  _fcmpg (int pc)
  _fcmpl (int pc)
  _fconst_0 (int pc)
  _fconst_1 (int pc)
  _fconst_2 (int pc)
  _fdiv (int pc)
  _fload (int pc, int index)
  _fload_0 (int pc)
  _fload_1 (int pc)
  _fload_2 (int pc)
  _fload_3 (int pc)
  _fmul (int pc)
  _fneg (int pc)
  _frem (int pc)
  _freturn (int pc)
  _fstore (int pc, int index)
  _fstore_0 (int pc)
  _fstore_1 (int pc)
  _fstore_2 (int pc)
  _fstore_3 (int pc)
  _fsub (int pc)
  _getfield (
             int pc,
             int index,
             IConstantPoolEntry constantFieldref)
  _getstatic (
              int pc,
              int index,
              IConstantPoolEntry constantFieldref)
  _goto (int pc, int branchOffset)
  _goto_w (int pc, int branchOffset)
  _i2b (int pc)
  _i2c (int pc)
_i2d (int pc)
_i2f (int pc)
_i2l (int pc)
_i2s (int pc)
_iadd (int pc)
_iaload (int pc)
_iand (int pc)
_iastore (int pc)
_iconst_m1 (int pc)
_iconst_0 (int pc)
_iconst_1 (int pc)
_iconst_2 (int pc)
_iconst_3 (int pc)
_iconst_4 (int pc)
_iconst_5 (int pc)
_idiv (int pc)
_if_acmpeq (int pc, int branchOffset)
_if_acmpne (int pc, int branchOffset)
_if_icmpeq (int pc, int branchOffset)
_if_icmpne (int pc, int branchOffset)
_if_icmplt (int pc, int branchOffset)
_if_icmpge (int pc, int branchOffset)
_if_icmpgt (int pc, int branchOffset)
_if_icmple (int pc, int branchOffset)
_ifeq (int pc, int branchOffset)
_ifne (int pc, int branchOffset)
_iflt (int pc, int branchOffset)
_ifge (int pc, int branchOffset)
_ifgt (int pc, int branchOffset)
_ifle (int pc, int branchOffset)
_ifnonnull (int pc, int branchOffset)
_ifnull (int pc, int branchOffset)
_iinc (int pc, int index, int _const)
_iload (int pc, int index)
_iload_0 (int pc)
_iload_1 (int pc)
_iload_2 (int pc)
_iload_3 (int pc)
_imul (int pc)
_ineg (int pc)
_instanceof (
             int pc,
             int index,
             IConstantPoolEntry constantClass)
; Not defined in this version of the jar :(
;_invokedynamic (
;                int pc,
;                int index,
;                IConstantPoolEntry nameEntry,
;                IConstantPoolEntry descriptorEntry)
_invokeinterface (
                  int pc,
                  int index,
                  byte nargs,
                  IConstantPoolEntry constantInterfaceMethodref)
_invokespecial (
                int pc,
                int index,
                IConstantPoolEntry constantMethodref)
_invokestatic (
               int pc,
               int index,
               IConstantPoolEntry constantMethodref)
_invokevirtual (
                int pc,
                int index,
                IConstantPoolEntry constantMethodref)
_ior (int pc)
_irem (int pc)
_ireturn (int pc)
_ishl (int pc)
_ishr (int pc)
_istore (int pc, int index)
_istore_0 (int pc)
_istore_1 (int pc)
_istore_2 (int pc)
_istore_3 (int pc)
_isub (int pc)
_iushr (int pc)
_ixor (int pc)
_jsr (int pc, int branchOffset)
_jsr_w (int pc, int branchOffset)
_l2d (int pc)
_l2f (int pc)
_l2i (int pc)
_ladd (int pc)
_laload (int pc)
_land (int pc)
_lastore (int pc)
_lcmp (int pc)
_lconst_0 (int pc)
_lconst_1 (int pc)
_ldc (int pc, int index, IConstantPoolEntry constantPoolEntry)
_ldc_w (int pc, int index, IConstantPoolEntry constantPoolEntry)
_ldc2_w (int pc, int index, IConstantPoolEntry constantPoolEntry)
_ldiv (int pc)
_lload (int pc, int index)
_lload_0 (int pc)
_lload_1 (int pc)
_lload_2 (int pc)
_lload_3 (int pc)
_lmul (int pc)
_lneg (int pc)
; Not defined in this version of the jar. :(
;_lookupswitch (
;               int pc,
;               int defaultoffset,
;               int npairs,
;               int[][] offset_pairs)
_lor (int pc)
_lrem (int pc)
_lreturn (int pc)
_lshl (int pc)
_lshr (int pc)
_lstore (int pc, int index)
_lstore_0 (int pc)
_lstore_1 (int pc)
_lstore_2 (int pc)
_lstore_3 (int pc)
_lsub (int pc)
_lushr (int pc)
_lxor (int pc)
_monitorenter (int pc)
_monitorexit (int pc)
_multianewarray (
                 int pc,
                 int index,
                 int dimensions,
                 IConstantPoolEntry constantClass)
_new (
      int pc,
      int index,
      IConstantPoolEntry constantClass)
_newarray (int pc, int atype)
_nop (int pc)
_pop (int pc)
_pop2 (int pc)
_putfield (
           int pc,
           int index,
           IConstantPoolEntry constantFieldref)
_putstatic (
            int pc,
            int index,
            IConstantPoolEntry constantFieldref)
_ret (int pc, int index)
_return (int pc)
_saload (int pc)
_sastore (int pc)
_sipush (int pc, short value)
_swap (int pc)
_tableswitch (
              int pc,
              int defaultoffset,
              int low,
              int high,
              int[] jump_offsets)
_wide (
       int pc,
       int opcode,
       int index)
_wide (
       int pc,
       int iincopcode,
       int index,
       int _const)
_breakpoint (int pc)
_impdep1 (int pc)
_impdep2 (int pc))

(extend-protocol Coerce
  IExceptionTableEntry
  (coerce [e]
    {:start-pc    (.getStartPC e)
     :end-pc      (.getEndPC e)
     :handler-pc  (.getHandlerPC e)
     :catch-type  (String. (.getCatchType e))})
  
  ILocalVariableTableEntry
  (coerce [v]
    {:name        (symbol (String. (.getName v)))
     :descriptor  (String. (.getDescriptor v))
     :length      (.getLength v)
     :start-pc    (.getStartPC v)})

  ILocalVariableTypeTableEntry
  (coerce [v]
    {:start-pc  (.getStartPC v)
     :length    (.getLength v)
     :name      (symbol (String. (.getName v)))
     :signature (String. (.getSignature v))})

  IInnerClassesAttributeEntry
  (coerce [ic]
    {:access-flags (->> (.getAccessFlags ic)
                        (modifiers->kw [:public :protected :private :abstract
                                        :static :final]))
     :inner-name        (when (.getInnerName ic) (String. (.getInnerName ic)))
     :outer-class-name  (when (.getOuterClassName ic)
                          (String. (.getOuterClassName ic)))
     :inner-class-name  (when (.getInnerClassName ic)
                          (String. (.getInnerClassName ic)))})
 
  IAnnotation
  (coerce [a]
    {:type-name (String. (.getTypeName a))
     :components (map coerce (.getComponents a))})

  IAnnotationComponent
  (coerce [c]
    {:name  (String. (.getComponentName c))
     :value (coerce (.getComponentValue c))})

  IAnnotationComponentValue
  (coerce [c]
    {:values                  (map coerce (.getAnnotationComponentValues c))
     :value                   (coerce (.getAnnotationValue c))
     :class-info              (coerce (.getClassInfo c))
     :constant-value          (coerce (.getConstantValue c))
     :enum-constant-name      (when (.getEnumConstantName c)
                                (String. (.getEnumConstantName c)))
     :enum-constant-type-name (when (.getEnumConstantTypeName c)
                                (String. (.getEnumConstantTypeName c)))
     :tag                     (.getTag c)})

  IParameterAnnotation
  (coerce [c] (map coerce (.getAnnotations c)))

  IStackMapFrame
  (coerce [f]
    {:type          (.getFrameType f)
     :offset-delta  (.getOffsetDelta f)
     :locals        (map coerce (.getLocals f))
     :stack-items   (map coerce (.getStackItems f))})

  IVerificationTypeInfo
  (coerce [i]
    {:tag             (.getTag i)
     :offset          (.getOffset i)
     :class-type-name (String. (.getClassTypeName i))})

  IClassFileAttribute
  (coerce [a]
    {:type        (type a)
     :name        (String. (.getAttributeName a))
     :length      (.getAttributeLength a)})

  IAnnotationDefaultAttribute
  (coerce [a] (coerce (.getMemberValue a)))

  ICodeAttribute
  (coerce [c]
    {:max-locals      (.getMaxLocals c)
     :max-stack       (.getMaxStack c)
     :line-numbers    (coerce (.getLineNumberAttribute c))
     :local-variables (coerce (.getLocalVariableAttribute c))
     :exception-table (map coerce (.getExceptionTable c))
     :raw-bytecode    (.getBytecodes c)
     :bytecode        (let [v (BytecodeVisitor. (atom []))]
                        (.traverse c v)
                        @v)
     :code-length     (.getCodeLength c)})

  IConstantValueAttribute
  (coerce [c]
    (coerce (.getConstantValue c)))

  IEnclosingMethodAttribute
  (coerce [em]
    {:enclosing-class   (symbol (String. (.getEnclosingClass em)))
     :method-descriptor (String. (.getMethodDescriptor em))
     :method-name       (symbol (String. (.getMethodName em)))})

  IExceptionAttribute
  (coerce [e] (map #(String.) (.getExceptionNames e)))

  IInnerClassesAttribute
  (coerce [ic] (map coerce (.getInnerClassAttributesEntries ic)))

  ILineNumberAttribute
  (coerce [l]
    (->> l
         .getLineNumberTable
         (mapcat identity)
         (apply sorted-map)))

  ILocalVariableAttribute
  (coerce [v] (map coerce (.getLocalVariableTable v)))

  ILocalVariableTypeTableAttribute
  (coerce [lvtt] (map coerce (.getLocalVariableTypeTable lvtt)))

  IRuntimeInvisibleAnnotationsAttribute
  (coerce [a] (map coerce (.getAnnotations a)))
  
  IRuntimeInvisibleParameterAnnotationsAttribute
  (coerce [a] (map coerce (.getParameterAnnotations a)))

  IRuntimeVisibleAnnotationsAttribute
  (coerce [a] (map coerce (.getAnnotations a)))

  IRuntimeVisibleParameterAnnotationsAttribute
  (coerce [a] (map coerce (.getParameterAnnotations a)))

  ISignatureAttribute
  (coerce [a] (String. (.getSignature a)))

  ISourceAttribute
  (coerce [s] (String. (.getSourceFileName s)))

  IStackMapAttribute
  (coerce [s] (map coerce (.getStackMapFrame s)))

  IStackMapTableAttribute
  (coerce [t] (map coerce (.getStackMapFrame t)))

  IMethodInfo
  (coerce [m]
    {:descriptor          (String. (.getDescriptor m))
     :access-flags        (->> (.getAccessFlags m)
                               (modifiers->kw [:public :protected :private
                                               :abstract :static :final
                                               :synchronized :native :strict
                                               :bridge]))
     :name                (symbol (String. (.getName m)))
     :clinit?             (.isClinit m)
     :constructor?        (.isConstructor m)
     :synthetic?          (.isSynthetic m)
     :deprecated?         (.isDeprecated m)
     :code                (coerce (.getCodeAttribute m))
     :exception           (.getExceptionAttribute m)})
     
  IFieldInfo
  (coerce [f]
    (let [base {:access-flags (->> (.getAccessFlags f)
                                   (modifiers->kw [:public :protected :private
                                                   :static :final :transient
                                                   :volatile :enum]))
                :name         (symbol (String. (.getName f)))
                :descriptor   (String. (.getDescriptor f))
                :synthetic?   (.isSynthetic f)
                :deprecated?  (.isDeprecated f)
                :attributes   (map coerce (.getAttributes f))}
          base
          (if (.hasConstantValueAttribute f)
            (assoc base :constant-value-attribute
                   (.getConstantValueAttribute f))
            base)]
      base)))

(defn class-name
  "Finds the sanitized canonical name of the class of an object. If obj is a
  class, uses obj, otherwise finds the class of that object."
  [obj]
  (-> (if (class? obj)
        obj
        (class obj))
      .getCanonicalName
      (.replace \. \/)))

(defn class-file-bytes
  "Returns a byte array for a class (or an object's class)."
  [obj]
  (get (no.disassemble.NoDisassemble/getClasses)
       (class-name obj)))

(defn ^ClassFileReader class-file-reader
  [obj]
  (ClassFileReader. (class-file-bytes obj) IClassFileReader/ALL))


(defn disassemble
  "Returns a structure representing the bytecode of an object. TODO: inner classes. Maybe broken: interface-names?"
  [obj]
  (let [r (class-file-reader obj)]
    {:attributes      (->> r .getAttributes (map coerce))
     :major-version   (.getMajorVersion r)
     :minor-version   (.getMinorVersion r)
     :class?          (.isClass r)
     :interface?      (.isInterface r)
     :name            (symbol (String. (.getClassName r)))
     :superclass-name (symbol (String. (.getSuperclassName r)))
     :interface-names (.getInterfaceNames r)
     :fields          (->> r .getFieldInfos (map coerce))
     :methods         (->> r .getMethodInfos (map coerce))}))
