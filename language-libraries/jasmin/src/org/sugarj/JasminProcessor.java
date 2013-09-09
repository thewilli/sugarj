package org.sugarj;

import static org.sugarj.common.ATermCommands.getApplicationSubterm;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.ATermCommands;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.StringCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import jasmin.Main;

public class JasminProcessor extends AbstractBaseProcessor {

  private static final long serialVersionUID = 8526963395981176061L;

  //shared SugarJ environment
  private Environment environment;
  //original source file currently processed
  private RelativePath sourceFile;
  //target output file
  private Path outFile;
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
    if (ppTable == null){ 
      try {
        ppTable = ATermCommands.readPrettyPrintTable(getLanguage().ensureFile("org/sugarj/languages/Jasmin.pp").getAbsolutePath());
      } catch (Exception e) {
        ATermCommands.setErrorMessage(term, "generating Jasmin parse table failed");
        return null;
      } 
    }
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
    this.outFile = environment.createOutPath(FileCommands.dropExtension(sourceFile.getRelativePath()) + "." + JasminLanguage.getInstance().getBaseFileExtension());
  }

  @Override
  public void processModuleImport(IStrategoTerm toplevelDecl) throws IOException {

  }

  @Override
  public List<String> processBaseDecl(IStrategoTerm toplevelDecl) throws IOException {
    //Term can be either a base- (pure Jasmin) or namespace declaration here
    //TODO: Differentiate between base and NS declartion
    
    IStrategoTerm term = getApplicationSubterm(toplevelDecl, "JasminBody", 0);
    String text = null;
    try {
      text = prettyPrint(term);
    } catch (NullPointerException e) {
      ATermCommands.setErrorMessage(toplevelDecl, "pretty printing Jasmin failed");
    }
    if (text != null)
      body.add(text);
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
    if(body.isEmpty())
      return "";
    return StringCommands.printListSeparated(body, "\n");
    // TODO Auto-generated method stub
  }

  @Override
  public Path getGeneratedSourceFile() {
   return outFile;
  }

  @Override
  public List<Path> compile(List<Path> generatedSourceFiles, Path targetDir, List<Path> classpath) throws IOException, SourceCodeException {
    jasmin.Main compiler = null;
    LinkedList<Path> outputFiles = new LinkedList<Path>();
    String outputDirWithSuffix = targetDir.getAbsolutePath();
    if(!outputDirWithSuffix.endsWith("/") && !outputDirWithSuffix.endsWith(File.separator))
      outputDirWithSuffix += File.separator;
    String compilerArgs[] = new String[3];
    compilerArgs[0] = "-d";
    compilerArgs[1] = targetDir.getAbsolutePath();
    for(Path compileFile : generatedSourceFiles){
      compiler = new jasmin.Main();
      compilerArgs[2] = compileFile.getAbsolutePath();
      compiler.run(compilerArgs); //execute
      String targetFile = outputDirWithSuffix + FileCommands.dropExtension(FileCommands.fileName(compileFile)) + JasminLanguage.getInstance().getBinaryFileExtension();
      
      if(FileCommands.fileExists(new AbsolutePath(targetFile)))
        outputFiles.add(new AbsolutePath(targetFile));
    }
    //TODO: How does the compiled file(s) in the temporary output directory get to the output binary path?
    return outputFiles;
  }

}
