package org.sugarj.strategies;

import java.io.File;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;


/**
 * Java-based Strategy to resolve paths within a Language Library
 * @author Willi Eggeling
 */
public class GetSourcePath_0_0 extends Strategy{
	
	private String path = "";
	
	public GetSourcePath_0_0(String path){
		this.path = path;
	}
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
	  ITermFactory factory = context.getFactory();
	  //start with the base path
	  String result = path; 
	  //add separator if not already done
	  if(!result.endsWith(File.separator))
		  result += File.separator;
	  //if current term is a string append it (but prevent double separators)
	  if(current.getTermType() == IStrategoTerm.STRING){
		  String path = ((IStrategoString)current).stringValue();
		  if(path.startsWith(File.separator))
			  path = path.substring(1);
		  result += path;
	  }
	  //return result
	  return factory.makeString(result);
	}
	
}
