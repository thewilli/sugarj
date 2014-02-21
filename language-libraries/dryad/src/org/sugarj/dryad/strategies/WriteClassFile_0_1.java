package org.sugarj.dryad.strategies;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoString;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;
import org.sugarj.dryad.Activator;


public class WriteClassFile_0_1 extends Strategy{
	
	public static WriteClassFile_0_1 instance = new WriteClassFile_0_1();
	
	//TODO: Merge with STRJVM_callstatic_string_string
	
	@SuppressWarnings("deprecation")
  private java.net.URL getDepURL(String dep){
	  try{
	  return new File(Activator.getPluginPath(dep)).toURL();
	  }catch(Exception ex){
	    return null;
	  }
	}
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm t_path) {
	  ITermFactory factory = context.getFactory();
	  try{
	    
	    ArrayList<java.net.URL> deps = new ArrayList<java.net.URL>();
	    deps.add(getDepURL("/ext/aterm2class/aterm.jar"));
	    deps.add(getDepURL("/ext/aterm2class/aterm-java-1.8.2.jar"));
	    deps.add(getDepURL("/ext/aterm2class/bcel-5.2.jar"));
	    deps.add(getDepURL("/ext/aterm2class/IntermediateLanguage-1.0.jar"));
	    deps.add(getDepURL("/ext/aterm2class/jjtraveler.jar"));
	    deps.add(getDepURL("/ext/aterm2class/shared-objects.jar"));
	    deps.add(getDepURL("/ext/aterm2class/aterm2class.jar"));
      
      
	    URLClassLoader loader = URLClassLoader.newInstance(deps.toArray(new java.net.URL[0]),ClassLoader.getSystemClassLoader());
	    Class<?> clazz = Class.forName("str.classtree.classify.ClassClassifier", true, loader);
	    Class<?> params[] = new Class[2];
      params[0] = String.class;
      params[1] = String.class;
      Object o = clazz.getMethod("convertAndExport",params).invoke(null,current.toString(), ((StrategoString)t_path).stringValue());
	    if(o != null)
	      throw new Exception(o.toString());
	    
	  }catch(Exception ex){
	    context.getIOAgent().printError(ex.getMessage());
	    //TODO: Error handling
	    return factory.makeInt(1);
	  }
	  return factory.makeInt(0);
	}
	
}
