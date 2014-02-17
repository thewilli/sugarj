package org.sugarj.dryad.strategies;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.HybridInterpreter;
import org.strategoxt.lang.InteropRegisterer;


/**
 * Helper class that registeres Java Strategies with the HybridInterpreter
 * without the need to build a .jar file
 * @author Willi Eggeling
 *
 */
public final class DryadStrategyRegisterer {
    
  private static Object getFieldValue(Object o, String fieldName){
    try{
      Field field = o.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(o);
    }catch(Exception ex){
      return null;
    }
  }
  
  private static Object getMethodValue(Object o, String methodName){
    try{
      Method method = o.getClass().getDeclaredMethod(methodName, (Class<?>[])null);
      method.setAccessible(true);
      return method.invoke(o, (Object[])null);
    }catch(Exception ex){
      return null;
    }
  }
  
  @SuppressWarnings("unchecked")
  /**
   * Register native Java Strategies. Skips registration if called more than once.
   * @param interpreter HybridInterpreter to register strategies to
   */
  public static void registerStrategies(HybridInterpreter interpreter){
    try {
       InteropRegisterer registerer = new org.sugarj.dryad.strategies.InteropRegisterer();
       interpreter.getCompiledContext().setFactory((ITermFactory)getFieldValue(interpreter, "recordingFactory"));
       registerer.registerLazy(interpreter.getContext(), interpreter.getCompiledContext(), ClassLoader.getSystemClassLoader());
       interpreter.getCompiledContext().addConstructors((Collection<IStrategoConstructor>) getMethodValue(getFieldValue(interpreter, "recordingFactory"), "getAndClearConstructorRecord"));
       interpreter.getCompiledContext().setFactory((ITermFactory)getMethodValue(getFieldValue(interpreter, "recordingFactory"), "getWrappedFactory"));
    } catch (Exception e) {}
  }
}
