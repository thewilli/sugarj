package org.sugarj.driver.transformations.renaming;

import org.strategoxt.stratego_lib.*;
import org.strategoxt.lang.*;
import org.spoofax.interpreter.terms.*;
import static org.strategoxt.lang.Term.*;
import org.spoofax.interpreter.library.AbstractPrimitive;
import java.util.ArrayList;
import java.lang.ref.WeakReference;

@SuppressWarnings("all") final class lifted2 extends Strategy 
{ 
  TermReference w_22;

  TermReference x_22;

  @Override public IStrategoTerm invoke(Context context, IStrategoTerm term)
  { 
    Fail388:
    { 
      lifted3 lifted30 = new lifted3();
      lifted30.w_22 = w_22;
      lifted30.x_22 = x_22;
      term = try_1_0.instance.invoke(context, term, lifted30);
      if(term == null)
        break Fail388;
      if(true)
        return term;
    }
    return null;
  }
}