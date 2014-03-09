package org.sugarj.dryad.strategies;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;


/**
 * Retrieve a list of all classes within a .jar file
 * @author Willi Eggeling
 *
 */
public class GetJarContent_0_0 extends Strategy{
	
	public static GetJarContent_0_0 instance = new GetJarContent_0_0();
	
	/**
	 * Return the list of class file entries within the provided .jar file
	 * @param jarName Full path to .jar file
	 * @param entries list where resulting entries should be put in
	 * @param factory term factory to create Stratego string entries
	 * @throws Exception in case of any error
	 */
	private void processJar(IStrategoTerm jarName, ArrayList<IStrategoTerm> entries,ITermFactory factory) throws Exception{
		ZipInputStream zstream = new ZipInputStream(new FileInputStream(((IStrategoString)jarName).stringValue()));
		ZipEntry entry;
		while((entry = zstream.getNextEntry()) != null){
			if(entry.getName().endsWith(".class")){
				entries.add(factory.makeString(entry.getName()));
			}
		}
		zstream.close();
	}
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm jarName) {
	  ITermFactory factory = context.getFactory();
	  ArrayList<IStrategoTerm> entries = new ArrayList<IStrategoTerm>();
	  try{
		  //fill list
		  processJar(jarName,entries,factory);
	  }catch(Exception ex){}
	  //return list of resulting entries
	  return factory.makeList(entries);
	}
	
}
