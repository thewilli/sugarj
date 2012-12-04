package org.sugarj.jasmin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.spoofax.NotImplementedException;
import org.strategoxt.HybridInterpreter;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.languagelib.SourceFileContent;

public class JasminSourceFileContent extends SourceFileContent {

  private static final long serialVersionUID = -8453559148927326474L;
  private List<String> bodyDecls = new LinkedList<String>();

  @Override
  public String getCode(Set<RelativePath> generatedClasses, HybridInterpreter interp, Path outFile) throws ClassNotFoundException, IOException {
    throw new NotImplementedException();
  }
  
  public void addBodyDecl(String bodyDecl) {
    bodyDecls.add(bodyDecl);
  }

  @Override
  public int hashCode() {
    throw new NotImplementedException();
  }

  @Override
  public boolean equals(Object o) {
    throw new NotImplementedException();
  }

  @Override
  public boolean isEmpty() {
    throw new NotImplementedException();
  }
}
