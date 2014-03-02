package org.sugarj;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * Extended abstract base language class which includes namespace decleration checks
 * @author wje
 *
 */
public abstract class ExtendedAbstractBaseLanguage extends AbstractBaseLanguage {
	/***
	 * Check whether the provided term is a namespace delcaration (e.g. package name in Java).
	 * May always return false if not applicable for langauge.
	 * @param decl Term to check
	 * @return true if namespace declaration, false otherwise
	 */
	public abstract boolean isNamespaceDec(IStrategoTerm decl);
	
}
