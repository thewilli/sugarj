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
	
	/**
	 * Generate a list of depending .jar files
	 * @param jars list of jar file paths
	 * @return list of jars
	 */
	private static List<URL> generateDepList(IStrategoTerm jars){
		//verify term type
		if(jars.getTermType() != IStrategoTerm.LIST)
			throw new IllegalArgumentException("invalid argument: jars");
		List<URL> deps = new ArrayList<URL>();
		//iterate over each provided .jar
		for(IStrategoTerm term : jars.getAllSubterms()){
			//validate entry
			if(term.getTermType() != IStrategoTerm.STRING)
				throw new IllegalArgumentException("invalid argument: jars");
			String jar = ((IStrategoString)term).stringValue();
			//convert to Java File
			File jarFile = new File(jar);
			//verify that files exists
			if(!jarFile.exists())
				throw new IllegalArgumentException(".jar does not exist: " + jar);
			try{
				//add to list
				deps.add(jarFile.toURI().toURL());
			}catch(Exception ex){/*cannot happend here*/}
		}
		return deps;
	}
	
	/**
	 * Get a Java String from a Stratego String Term
	 * @param term String term
	 * @return Java String rerpesentation of temr
	 */
	private static String asString(IStrategoTerm term){
		//Verify type
		if(term.getTermType() != IStrategoTerm.STRING)
			throw new IllegalArgumentException("invalid string: " + term.toString());
		//extract value
		return ((IStrategoString)term).stringValue();
	}
	
	/**
	 * Extract parameter types from Stratego Term
	 * @param params provided parameter
	 * @return List of parameter types
	 */
	protected static Class<?>[] getParamTypes(IStrategoTerm params){
		//verify term type
		if(params.getTermType() != IStrategoTerm.LIST)
			throw new java.lang.IllegalArgumentException("invalid argument parameter");
		Class<?>[] paramTypes = new Class<?>[params.getSubtermCount()];
		//iterate over each provided term
		for(int i = 0; i < params.getSubtermCount(); i++){
			//verify its type
			if(params.getSubterm(i).getTermType() != IStrategoTerm.APPL)
				throw new java.lang.IllegalArgumentException("invalid argument parameter");
			IStrategoAppl param = (IStrategoAppl) params.getSubterm(i);
			//check target type (currently only "String" and "Int" supported)
			if(param.getName().equals("String")){
				paramTypes[i] = String.class;
			}else if(param.getName().equals("Int")){
				paramTypes[i] = int.class;
			}else{
				//unsupported type detected
				throw new java.lang.IllegalArgumentException("invalid argument parameter type: " + param.getName());
			}
		}
		return paramTypes;
	}
	
	/**
	 * Retrieve parameter values from provided paramter term
	 * @param params term representation of parameters
	 * @return list of parameter values
	 */
	protected static Object[] getParamValues(IStrategoTerm params){
		//type does not need to be checked here as already done in getParamTypes()
		Object paramValues[] = new Object[params.getSubtermCount()];
		//iterate over each argument
		for(int i = 0; i < params.getSubtermCount(); i++){
			IStrategoAppl param = (IStrategoAppl) params.getSubterm(i);
			//handle different types
			if(param.getName().equals("String")){
				//a string can be "null" or a string value
				if(param.getSubterm(0).getTermType() == IStrategoTerm.APPL && ((IStrategoAppl)param.getSubterm(0)).getName().equals("Null")){
					paramValues[i] = null;
				}else if(param.getSubterm(0).getTermType() == IStrategoTerm.STRING){
					paramValues[i] = asString(param.getSubterm(0));
				}else{
					throw new java.lang.IllegalArgumentException("invalid argument parameter value");
				}
			}else if(param.getName().equals("Int")){
				//handle integer value
				if(param.getSubterm(0).getTermType() != IStrategoTerm.INT)
					throw new java.lang.IllegalArgumentException("invalid argument parameter value");
				paramValues[i] = ((IStrategoInt)param.getSubterm(0)).intValue();
			}
		}
		return paramValues;
	}
	
	/**
	 * Converts the Java method invocation result to a term representation
	 * @param factory Term factory to create IStrategoTerm instances
	 * @param resultValue Java result value
	 * @return Term representation of value
	 */
	protected static IStrategoTerm resultToTerm(ITermFactory factory,Object resultValue){
		//value can be either null or an Object instance (string representation is returned then)
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
