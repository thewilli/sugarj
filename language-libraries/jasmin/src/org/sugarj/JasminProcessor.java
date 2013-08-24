package org.sugarj;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.Environment;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;

public class JasminProcessor extends AbstractBaseProcessor {

  private static final long serialVersionUID = 1L;

  @Override
  public AbstractBaseLanguage getLanguage() {
    return JasminLanguage.getInstance();
  }

  @Override
  public void init(RelativePath sourceFile, Environment environment) {
    
    // TODO Auto-generated method stub

  }

  @Override
  public void processModuleImport(IStrategoTerm toplevelDecl) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> processBaseDecl(IStrategoTerm toplevelDecl) throws IOException {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }

  @Override
  public String getNamespace() {
    return "";
  }

  @Override
  public String getModulePathOfImport(IStrategoTerm decl) {
    return decl.toString();
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isModuleExternallyResolvable(String relModulePath) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getExtensionName(IStrategoTerm decl) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IStrategoTerm getExtensionBody(IStrategoTerm decl) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getGeneratedSource() {
    return "";
    // TODO Auto-generated method stub
  }

  @Override
  public Path getGeneratedSourceFile() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Path> compile(List<Path> generatedSourceFiles, Path targetDir, List<Path> classpath) throws IOException, SourceCodeException {
    // TODO Auto-generated method stub
    return null;
  }

}
