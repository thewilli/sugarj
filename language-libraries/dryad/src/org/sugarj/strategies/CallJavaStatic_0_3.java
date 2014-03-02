package org.sugarj.strategies;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;


/**
 * Java-based Strategy to call static Java functions
 * @author Willi Eggeling
 */
public class CallJavaStatic_0_3 extends Strategy{
	
	public static CallJavaStatic_0_3 instance = new CallJavaStatic_0_3();
	
	private static List<URL> generateDepList(IStrategoTerm jars){
		if(jars.getTermType() != IStrategoTerm.LIST)
			throw new IllegalArgumentException("invalid argument: jars");
		List<URL> deps = new ArrayList<URL>();
		for(IStrategoTerm term : jars.getAllSubterms()){
			if(term.getTermType() != IStrategoTerm.STRING)
				throw new IllegalArgumentException("invalid argument: jars");
			String jar = ((IStrategoString)term).stringValue();
			File jarFile = new File(jar);
			if(!jarFile.exists())
				throw new IllegalArgumentException(".jar does not exist: " + jar);
			try{
				deps.add(jarFile.toURI().toURL());
			}catch(Exception ex){}
		}
		return deps;
	}
	
	private static String asString(IStrategoTerm term){
		if(term.getTermType() != IStrategoTerm.STRING)
			throw new IllegalArgumentException("invalid string: " + term.toString());
		return ((IStrategoString)term).stringValue();
	}
	
	private static Class<?>[] getParamTypes(IStrategoTerm params){
		if(params.getTermType() != IStrategoTerm.LIST)
			throw new java.lang.IllegalArgumentException("invalid argument parameter");
		Class<?>[] paramTypes = new Class<?>[params.getSubtermCount()];
		for(int i = 0; i < params.getSubtermCount(); i++){
			if(params.getSubterm(i).getTermType() != IStrategoTerm.APPL)
				throw new java.lang.IllegalArgumentException("invalid argument parameter");
			IStrategoAppl param = (IStrategoAppl) params.getSubterm(i);
			if(param.getName().equals("String")){
				paramTypes[i] = String.class;
			}else if(param.getName().equals("Int")){
				paramTypes[i] = int.class;
			}else{
				throw new java.lang.IllegalArgumentException("invalid argument parameter type: " + param.getName());
			}
		}
		return paramTypes;
	}
	
	private static Object[] getParamValues(IStrategoTerm params){
		Object paramValues[] = new Object[params.getSubtermCount()];
		for(int i = 0; i < params.getSubtermCount(); i++){
			IStrategoAppl param = (IStrategoAppl) params.getSubterm(i);
			if(param.getName().equals("String")){
				if(param.getSubterm(0).getTermType() != IStrategoTerm.STRING)
					throw new java.lang.IllegalArgumentException("invalid argument parameter value");
				paramValues[i] = asString(param.getSubterm(0));
			}else if(param.getName().equals("Int")){
				if(param.getSubterm(0).getTermType() != IStrategoTerm.INT)
					throw new java.lang.IllegalArgumentException("invalid argument parameter value");
				paramValues[i] = ((IStrategoInt)param.getSubterm(0)).intValue();
			}
		}
		return paramValues;
	}
	
	private static IStrategoTerm resultToTerm(ITermFactory factory,Object resultValue){
		if(resultValue == null){
			return factory.makeAppl(factory.makeConstructor("Null", 0));
		}else{
			return factory.makeAppl(
					factory.makeConstructor("String", 1),
					factory.makeString(resultValue.toString())
			);
		}
	}
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm params,
			IStrategoTerm jars, IStrategoTerm className, IStrategoTerm methodName) {
		ITermFactory factory = context.getFactory();
		try{
			//check arguments
			if(className.getTermType() != IStrategoTerm.STRING)
				throw new IllegalArgumentException("invalid class name provided");
			if(methodName.getTermType() != IStrategoTerm.STRING)
				throw new IllegalArgumentException("invalid method name provided");
			
			//create list of dependencies
			List<URL> deps = generateDepList(jars);
			//create classloader with all provided jars
			 URLClassLoader loader =
					 URLClassLoader.newInstance(deps.toArray(new java.net.URL[0]),ClassLoader.getSystemClassLoader());	 
			 //retrieve target class
			 Class<?> cls = Class.forName(asString(className).replace('/', '.'), true, loader);
			 //get method and result by invoking it with the provided arguments
			 Object result = 
					 cls.getMethod(asString(methodName), getParamTypes(params))
					 .invoke(null, getParamValues(params));
			 return factory.makeAppl(
					 factory.makeConstructor("Result", 1),
					 resultToTerm(factory,result)
			);
			 
		}catch(Exception ex){
			//Exception occured, return it
			return factory.makeAppl(
					factory.makeConstructor("Exception", 2),
					factory.makeString(ex.getClass().getName()),
					factory.makeString(ex.getMessage())
			);
		}		
	}
	
}
