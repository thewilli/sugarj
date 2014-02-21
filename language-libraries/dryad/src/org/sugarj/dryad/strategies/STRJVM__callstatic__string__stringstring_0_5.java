package org.sugarj.dryad.strategies;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;
import org.sugarj.dryad.Activator;


//TODO: Clean code of whole file

public class STRJVM__callstatic__string__stringstring_0_5 extends Strategy{
	
	public static STRJVM__callstatic__string__stringstring_0_5 instance = new STRJVM__callstatic__string__stringstring_0_5();
	
	private static String convertString(IStrategoTerm term){
		String result = term.toString().substring(1);
		result = result.substring(0,result.length() - 1);
		return result;
	}
	
	 @SuppressWarnings("deprecation")
	  private java.net.URL getDepURL(String dep){
	    try{
	    return new File(Activator.getPluginPath(dep)).toURL();
	    }catch(Exception ex){
	      return null;
	    }
	  }
	
	private String runExt(IStrategoTerm t1, IStrategoTerm t2){
		try{
		    //TODO: Add error handling
		
		    ArrayList<java.net.URL> deps = new ArrayList<java.net.URL>();
        deps.add(getDepURL("/ext/aterm2class/aterm.jar"));
        deps.add(getDepURL("/ext/aterm2class/aterm-java-1.8.2.jar"));
        deps.add(getDepURL("/ext/aterm2class/bcel-5.2.jar"));
        deps.add(getDepURL("/ext/aterm2class/IntermediateLanguage-1.0.jar"));
        deps.add(getDepURL("/ext/aterm2class/jjtraveler.jar"));
        deps.add(getDepURL("/ext/aterm2class/shared-objects.jar"));
        deps.add(getDepURL("/ext/aterm2class/aterm2class.jar"));
        
	      URLClassLoader loader = URLClassLoader.newInstance(deps.toArray(new java.net.URL[0]),ClassLoader.getSystemClassLoader());
		 
			
	      Class<?> clazz = Class.forName("str.classtree.Class2ATerm", true, loader);
  			Class<?> params[] = new Class[2];
  			params[0] = String.class;
  			params[1] = String.class;
  			Object o = 
			    clazz.getMethod("disasm_from_jar",params)
			      .invoke(
			           null,
			           convertString(t1).replace("/plugin-root", Activator.getPluginPath("/ext")),
			           convertString(t2)
			      );
			return o.toString();
		}catch(InvocationTargetException ex){
			Throwable e = ex.getCause();
			String r = "ERROR2: " + e.toString() + " -- " + e.getMessage() + " -- ";
			for(StackTraceElement e22 : e.getStackTrace()){
				r += e22.toString() + "\n";
			}
			return r;
		}catch(Exception ex){
			String r = "ERROR: " + ex.toString() + " -- " + ex.getMessage() + " -- ";
			for(StackTraceElement e : ex.getStackTrace()){
				r += e.toString() + "\n";
			}
			return r; // "ERROR: " + ex.getMessage() + "--" + ex.getStackTrace().toString();
		}
		
		
	}
	
	/*
	private static void addSoftwareLibrary(File file) throws Exception {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	}*/
		
	
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			IStrategoTerm strjvm, IStrategoTerm tclass, IStrategoTerm tmethod,
			IStrategoTerm targ1, IStrategoTerm targ2) {
		
		ITermFactory factory = context.getFactory();
		
		//context.getIOAgent().printError("STRJVM__callstatic__string__stringstring_0_5 called");
		String className = convertString(tclass);
		String methodName = convertString(tmethod);
		if(className.equals("str/classtree/Class2ATerm")){
			if(methodName.equals("disasm_from_jar")){
				try {

					String result = runExt(targ1, targ2);
					//context.getIOAgent().printError("Result: " + result);
					return factory.parseFromString(result);
				} catch (Exception e) {
					e.printStackTrace();
					context.getIOAgent().printError("Error: " + e.getMessage());
				}
				
			}else{
				context.getIOAgent().printError("method '" + className + "' not supported");
			}
		}else{
			context.getIOAgent().printError("class '" + className + "' not supported");
		}

		 return factory.makeString("UNSUPPORTED");
	}
	

}
