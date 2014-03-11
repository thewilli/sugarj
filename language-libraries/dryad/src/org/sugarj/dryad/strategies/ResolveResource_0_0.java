package org.sugarj.dryad.strategies;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoString;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;
import org.sugarj.dryad.Activator;


/**
 * Java-based Strategy which resolves ressource file names to a full path
 * @author Willi Eggeling <thewilli@googlemail.com>
 *
 */
public class ResolveResource_0_0 extends Strategy{
	
	public static ResolveResource_0_0 instance = new ResolveResource_0_0();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current) {
	  ITermFactory factory = context.getFactory();
	  //get ressource from "ext" folder of plugin
	  String request = "/ext/" + ((StrategoString)current).stringValue();
	  //use custom Activator functionality to retrieve file
	  String result = Activator.getPluginPath(request);
	  return factory.makeString(result != null ? result : request);
	}
	
}
