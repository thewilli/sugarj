package org.sugarj.dryad.strategies;

import java.io.FileOutputStream;
import java.io.PrintStream;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoString;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;


public class WriteClassFile_0_1 extends Strategy{
	
	public static WriteClassFile_0_1 instance = new WriteClassFile_0_1();
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current, IStrategoTerm t_path) {
	  ITermFactory factory = context.getFactory();
	  try{
	    //TODO: do actual class-file conversion
	    String path = ((StrategoString)t_path).stringValue();
	    FileOutputStream out = new FileOutputStream(path);
	    PrintStream pp = new PrintStream(out);
	    pp.print(current);
	    pp.close();
	    out.close();
	  }catch(Exception ex){
	    context.getIOAgent().printError(ex.getMessage());
	    //TODO: Error handling
	    return factory.makeInt(1);
	  }
	  return factory.makeInt(0);
	}
	
}
