package org.sugarj.driver.transformations.extraction;

import org.strategoxt.stratego_lib.*;
import org.strategoxt.lang.*;
import org.spoofax.interpreter.terms.*;
import static org.strategoxt.lang.Term.*;
import org.spoofax.interpreter.library.AbstractPrimitive;
import java.util.ArrayList;
import java.lang.ref.WeakReference;

@SuppressWarnings("all") public class $Overlays_1_0 extends Strategy 
{ 
  public static $Overlays_1_0 instance = new $Overlays_1_0();

  @Override public IStrategoTerm invoke(Context context, IStrategoTerm term, Strategy n_18)
  { 
    ITermFactory termFactory = context.getFactory();
    context.push("Overlays_1_0");
    Fail55:
    { 
      IStrategoTerm u_109 = null;
      IStrategoTerm t_109 = null;
      if(term.getTermType() != IStrategoTerm.APPL || extraction._consOverlays_1 != ((IStrategoAppl)term).getConstructor())
        break Fail55;
      t_109 = term.getSubterm(0);
      IStrategoList annos32 = term.getAnnotations();
      u_109 = annos32;
      term = n_18.invoke(context, t_109);
      if(term == null)
        break Fail55;
      term = termFactory.annotateTerm(termFactory.makeAppl(extraction._consOverlays_1, new IStrategoTerm[]{term}), checkListAnnos(termFactory, u_109));
      context.popOnSuccess();
      if(true)
        return term;
    }
    context.popOnFailure();
    return null;
  }
}