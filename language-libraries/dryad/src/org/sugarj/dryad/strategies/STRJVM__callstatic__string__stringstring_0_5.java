package org.sugarj.dryad.strategies;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
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
	
	private static String runExt(IStrategoTerm t1, IStrategoTerm t2){
		try{
		  //TODO: Add error handling
		  //FIXME: Change hardcoded paths
		File file = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/class2aterm.jar");
		File file2 = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/bcel-5.2.jar");
		File file3 = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/aterm-java-1.8.2.jar");
		File file4 = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/jjtraveler.jar");
		File file5 = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/shared-objects.jar");
		File file6 = new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/IntermediateLanguage-1.0.jar");
		
		@SuppressWarnings("deprecation")
		ClassLoader loader = URLClassLoader.newInstance(
			    new java.net.URL[] {file5.toURL(),file6.toURL(), file4.toURL(),file2.toURL(), file3.toURL(),file.toURL() },
			    STRJVM__callstatic__string__stringstring_0_5.class.getClassLoader()
			);
			
			Class<?> clazz = Class.forName("str.classtree.Class2ATerm", true, loader);
			//Class<? extends Runnable> runClass = clazz.asSubclass(Runnable.class);
			@SuppressWarnings("rawtypes")
			Class params[] = new Class[2];
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
		
		context.getIOAgent().printError("STRJVM__callstatic__string__stringstring_0_5 called");
		String className = convertString(tclass);
		String methodName = convertString(tmethod);
		if(className.equals("str/classtree/Class2ATerm")){
			if(methodName.equals("disasm_from_jar")){
				try {

					String result = runExt(targ1, targ2);
					//addSoftwareLibrary(new File("/Users/wje/work/SugarJasmin/2nd-try/eclipse-workspace/SpoofaxTest1/ext/class2aterm.jar"));
					//String result = Class2ATerm.disasm_from_jar(convertString(targ1),convertString(targ2));
					context.getIOAgent().printError("Result: " + result);
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
		
		/*
		int i = 0;
		for (IStrategoTerm t : new IStrategoTerm[]{strjvm,t2,t3,t4,t5}){
			i++;
			context.getIOAgent().printError("Var " + i + ": " + t.toString());
		}*/
		
		 
		 return factory.makeString("UNSUPPORTED");
		//return super.invoke(context, current, t1, t2, t3, t4, t5);
	}
	

}
