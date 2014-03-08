package org.sugarj.strategies;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.strategoxt.lang.Context;
import org.strategoxt.lang.Strategy;

/**
 * Java-based Strategy to call throw an Exception
 * @author Willi Eggeling
 */
public class ThrowException_0_1 extends Strategy{
	public static ThrowException_0_1 instance = new ThrowException_0_1();
	
	/**
	 * Exception that can be thrown from Stratego to cancel compilation
	 * @author Willi Eggeling
	 *
	 */
	public class StrategoCompilationException extends RuntimeException{
		private static final long serialVersionUID = -5610039095886878607L;
		private IStrategoTerm term;
		/**
		 * Get the encapsulated term
		 * @return
		 */
		public IStrategoTerm getTerm(){return term;}
		public StrategoCompilationException(IStrategoTerm term, String message){
			super(message);
			this.term = term;
		}
	}
	
	@Override
	public IStrategoTerm invoke(Context context, IStrategoTerm current,
			IStrategoTerm message) {
		String messageStr =
				(message.getTermType() == IStrategoTerm.STRING) 
						? ((IStrategoString)message).stringValue()
						: message.toString();
		throw new StrategoCompilationException(current, messageStr);
	}
	
}
