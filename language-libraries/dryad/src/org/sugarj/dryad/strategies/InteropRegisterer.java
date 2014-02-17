package org.sugarj.dryad.strategies;

import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;

/**
 * Helper class that registeres native Java-based strategies
 */
public class InteropRegisterer extends JavaInteropRegisterer {

  public InteropRegisterer() {
    super(new Strategy[] { 
        STRJVM__callstatic__string__stringstring_0_5.instance,
        WriteClassFile_0_1.instance,
        ResolveResource_0_0.instance
    });
  }
}
