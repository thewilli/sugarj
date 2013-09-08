package org.sugarj;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.ATermCommands;
import org.sugarj.common.Environment;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;

public class JasminProcessor extends AbstractBaseProcessor {

  private static final long serialVersionUID = 8526963395981176061L;

  //shared SugarJ environment
  private Environment environment;
  //original source file currently processed
  private RelativePath sourceFile;
  //source code parts currently processed
  private List<String> body = new LinkedList<String>();
  //Pretty-print table
  private IStrategoTerm ppTable;
  
  /**
   * Pretty-print a term (convert to string representation)
   * @param term term to stringify
   * @return string representation of term
   */
  private String prettyPrint(IStrategoTerm term) {
    if (ppTable == null) 
      ppTable = ATermCommands.readPrettyPrintTable(getLanguage().ensureFile("org/sugarj/languages/Jasmin.pp").getAbsolutePath()); 
    return ATermCommands.prettyPrint(ppTable, term, interp);
  }
  
  @Override
  public AbstractBaseLanguage getLanguage() {
    return JasminLanguage.getInstance();
  }

  @Override
  public void init(RelativePath sourceFile, Environment environment) {
    //Entry point: Processing of new file
    this.sourceFile = sourceFile;
    this.environment = environment;
  }

  @Override
  public void processModuleImport(IStrategoTerm toplevelDecl) throws IOException {

  }

  @Override
  public List<String> processBaseDecl(IStrategoTerm toplevelDecl) throws IOException {
    //Term can be either a base- (pure Jasmin) or namespace declaration here
    //TODO: Differentiate between base and NS declartion
    String source = prettyPrint(toplevelDecl.getSubterm(0));

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
