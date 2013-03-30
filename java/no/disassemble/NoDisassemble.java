package no.disassemble;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import java.util.Set;
import java.util.HashSet;

public class NoDisassemble {

    public static java.util.Map<String,byte[]> classes = new java.util.concurrent.ConcurrentHashMap<String,byte[]>();
    public static Instrumentation inst = null;

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
            //            println("Registering " + className);
            classes.put(className, classBytes);
            return classBytes;
        }

    }

  /**
   * Entry point method, called when the JVM initializes this package as an instrumentation agent.
   * @param args The arguments for this agent.  Currently unused.
   * @param inst The instrumentation object to register with.
   */
  public static void premain(String args, Instrumentation _inst) {
      println("Adding ClojureTransformer");
      inst = _inst;
      inst.addTransformer(new ClojureTransformer(), true);
      
  }


  /**
   * Provides help text if run.
   * @param args Ignored.
   */
  public static void main(String[] args) {
    System.out.println();
    System.out.println("Presuming the profile.jar and auditor.jar are in the dist directory then use with:");
    System.out.println("  java -javaagent:dist/profile.jar -classpath ${CLASSPATH}:dist/auditor.jar <main.program.class>");
    System.out.println();
    System.out.println("If wanting to instrument the java.* classes, then use:");
    System.out.println("  java -javaagent:dist/profile.jar=logsys -Xbootclasspath/a:dist/auditor.jar -classpath ${CLASSPATH}:dist/auditor.jar <main.program.class>");
    System.out.println();
  }

}
