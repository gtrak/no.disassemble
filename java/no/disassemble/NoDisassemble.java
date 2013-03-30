package no.disassemble;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import java.util.Map;

public class NoDisassemble {

    private static Map<String,byte[]> classes = new java.util.concurrent.ConcurrentHashMap<String,byte[]>();
    public static Map<String,byte[]> getClasses() {return classes;}

    private static void println(Object arg){
        System.out.println(arg);
    }
    {
        println("Initializing NoDisassemble");
    }
    public static class ClojureTransformer implements ClassFileTransformer {
        {println("Initializing transformer");}
        public byte[] transform(ClassLoader loader, String className, 
                                Class<?> classBeingRedefined, ProtectionDomain protectionDomain, 
                                byte[] classBytes) throws IllegalClassFormatException {
            classes.put(className, classBytes);
            return classBytes;
        }
    }

  /**
   * Entry point method, called when the JVM initializes this package as an instrumentation agent.
   * @param args The arguments for this agent.  Currently unused.
   * @param inst The instrumentation object to register with.
   */
  public static void premain(String args, Instrumentation inst) {
      println("Adding ClojureTransformer");

      inst.addTransformer(new ClojureTransformer(), true);
  }

}
