package org.sugarj;

import static org.sugarj.common.ATermCommands.getApplicationSubterm;
import static org.sugarj.common.ATermCommands.isApplication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.Term;
import org.strategoxt.HybridInterpreter;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.IErrorLogger;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.common.path.RelativeSourceLocationPath;
import org.sugarj.driver.sourcefilecontent.ISourceFileContent;
import org.sugarj.haskell.HaskellSourceFileContent;

/**
 * @author Sebastian Erdweg <seba at informatik uni-marburg de>
 */
public class HaskellLib extends LanguageLib {

  private static final long serialVersionUID = -8916207157344649789L;

  private transient File libDir;
  
  private HaskellSourceFileContent sourceContent;
  private Path outFile;
  private Set<RelativePath> generatedModules = new HashSet<RelativePath>();
  
  private String relNamespaceName;
  private String moduleName;

  @Override
  public List<File> getGrammars() {
    List<File> grammars = new LinkedList<File>(super.getGrammars());
    grammars.add(ensureFile("org/sugarj/languages/SugarHaskell.def"));
    grammars.add(ensureFile("org/sugarj/languages/Haskell.def"));
    return Collections.unmodifiableList(grammars);
  }
  
  @Override
  public File getInitGrammar() {
    return ensureFile("org/sugarj/haskell/initGrammar.sdf");
  }

  @Override
  public String getInitGrammarModule() {
    return "org/sugarj/haskell/initGrammar";
  }

  @Override
  public File getInitTrans() {
    return ensureFile("org/sugarj/haskell/initTrans.str");
  }

  @Override
  public String getInitTransModule() {
    return "org/sugarj/haskell/initTrans";
  }

  @Override
  public File getInitEditor() {
    return ensureFile("org/sugarj/haskell/initEditor.serv");
  }

  @Override
  public String getInitEditorModule() {
    return "org/sugarj/haskell/initEditor";
  }

  @Override
  public File getLibraryDirectory() {
    if (libDir == null) { // set up directories first
      String thisClassPath = "org/sugarj/HaskellLib.class";
      URL thisClassURL = HaskellLib.class.getClassLoader().getResource(thisClassPath);
      
      System.out.println(thisClassURL);
      
      if (thisClassURL.getProtocol().equals("bundleresource"))
        try {
          thisClassURL = FileLocator.resolve(thisClassURL);
        } catch (IOException e) {
          e.printStackTrace();
        }
      
      String classPath = thisClassURL.getPath();
      String binPath = classPath.substring(0, classPath.length() - thisClassPath.length());
      
      libDir = new File(binPath);
    }
    
    return libDir;
  }

  @Override
  public String getGeneratedFileExtension() {
    return ".o";
  }

  @Override
  public String getSugarFileExtension() {
    //TODO should be ".sugh"
    return ".sugj";
  }

  @Override
  public ISourceFileContent getSource() {
    return sourceContent;
  }

  @Override
  public Path getOutFile() {
    return outFile;
  }

  @Override
  public Set<RelativePath> getGeneratedFiles() {
    return generatedModules;
  }

  @Override
  public boolean isNamespaceDec(IStrategoTerm decl) {
    return isApplication(decl, "ModuleDec");
  }

  @Override
  public boolean isLanguageSpecificDec(IStrategoTerm decl) {
    return isApplication(decl, "Topdecl");
  }

  @Override
  public boolean isSugarDec(IStrategoTerm decl) {
    return isApplication(decl, "SugarBody");
  }

  @Override
  public boolean isEditorServiceDec(IStrategoTerm decl) {
    return isApplication(decl, "EditorServicesDec");   
  }

  @Override
  public boolean isImportDec(IStrategoTerm decl) {
    return isApplication(decl, "Import");   
  }

  @Override
  public boolean isPlainDec(IStrategoTerm decl) {
    return isApplication(decl, "PlainDec");   
  }

  @Override
  public void processNamespaceDec(IStrategoTerm toplevelDecl, Environment environment, HybridInterpreter interp, IErrorLogger errorLog, RelativeSourceLocationPath sourceFile, RelativeSourceLocationPath sourceFileFromResult) throws IOException {
    moduleName = Term.asJavaString(getApplicationSubterm(toplevelDecl, "ModuleDec", 0));
    relNamespaceName = FileCommands.dropFilename(sourceFile.getRelativePath());
    
    RelativePath clazz = environment.createBinPath(relNamespaceName + Environment.sep + moduleName + getGeneratedFileExtension());
    generatedModules.add(clazz);
    
    sourceContent.setNamespaceDecl(prettyPrint(toplevelDecl, interp));
  }

  @Override
  public void processLanguageSpecific(IStrategoTerm toplevelDecl, Environment environment, HybridInterpreter interp) throws IOException {
    sourceContent.addBodyDecl(prettyPrint(toplevelDecl, interp));
  }

  @Override
  public String prettyPrint(IStrategoTerm term, HybridInterpreter interp) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setupSourceFile(RelativePath sourceFile, Environment environment) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getRelativeNamespace() {
    return relNamespaceName;
  }

  @Override
  public LanguageLibFactory getFactoryForLanguage() {
    return HaskellLibFactory.getInstance();
  }

  @Override
  public String getImportedModulePath(IStrategoTerm toplevelDecl, HybridInterpreter interp) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void compile(List<Path> outFiles, Path bin, List<Path> path, Set<? extends Path> generatedFiles, Map<Path, Integer> generatedFileHashes, HybridInterpreter interp, boolean generateFiles) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void addImportModule(IStrategoTerm toplevelDecl, HybridInterpreter interp) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCheckedImportModule(IStrategoTerm toplevelDecl, HybridInterpreter interp) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public String getSugarName(IStrategoTerm decl, HybridInterpreter interp) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getSugarAccessibility(IStrategoTerm decl) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IStrategoTerm getSugarBody(IStrategoTerm decl) {
    // TODO Auto-generated method stub
    return null;
  }

}