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
import java.util.Set;
import org.eclipse.core.runtime.FileLocator;
import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.ATermCommands;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.IErrorLogger;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.jasmin.JasminSourceFileContent;
import org.sugarj.languagelib.SourceFileContent;

public class JasminLib extends LanguageLib {

  private static final long serialVersionUID = -5197926491222655526L;

  private transient File libDir;
  
  private JasminSourceFileContent sourceContent;
  private Path outFile;
  private Set<RelativePath> generatedModules = new HashSet<RelativePath>();
  
  private String relNamespaceName;
  private String moduleName;

  private IStrategoTerm ppTable;

  public String getVersion() {
    return "jasmin-0.1";
  }
  
  @Override
  public String getLanguageName() {
    return "Jasmin";
  }

  @Override
  public List<File> getGrammars() {
    List<File> grammars = new LinkedList<File>(super.getGrammars());
    grammars.add(ensureFile("org/sugarj/languages/Jasmin.def"));
    grammars.add(ensureFile("org/sugarj/languages/SugarJasmin.def"));
    return Collections.unmodifiableList(grammars);
  }
  
  @Override
  public File getInitGrammar() {
    return ensureFile("org/sugarj/jasmin/initGrammar.sdf");
  }

  @Override
  public String getInitGrammarModule() {
    return "org/sugarj/jasmin/initGrammar";
  }

  @Override
  public File getInitTrans() {
    return ensureFile("org/sugarj/jasmin/initTrans.str");
  }

  @Override
  public String getInitTransModule() {
    return "org/sugarj/jasmin/initTrans";
  }

  @Override
  public File getInitEditor() {
    return ensureFile("org/sugarj/jasmin/initEditor.serv");
  }

  @Override
  public String getInitEditorModule() {
    return "org/sugarj/jasmin/initEditor";
  }

  @Override
  public File getLibraryDirectory() {
    if (libDir == null) { // set up directories first
      String thisClassPath = "org/sugarj/JasminLib.class";
      URL thisClassURL = JasminLib.class.getClassLoader().getResource(thisClassPath);
      
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
    return "class";
  }

  @Override
  public String getSugarFileExtension() {
    return "sj";
  }
  
  @Override
  public String getOriginalFileExtension() {
    return "j";
  }

  @Override
  public SourceFileContent getSource() {
    return sourceContent;
  }

  @Override
  public Path getOutFile() {
    return outFile;
  }

  @Override
  public Set<RelativePath> getGeneratedFiles() {    throw new NotImplementedException();
    //return generatedModules;
  }
  
  @Override
  public String getRelativeNamespace() {
    return relNamespaceName;
    //FIXME
  }

  @Override
  public boolean isNamespaceDec(IStrategoTerm decl) {
    return false;
    //FIXME ??
    //throw new NotImplementedException();
    
    //return isApplication(decl, "ModuleDec");
  }

  @Override
  public boolean isLanguageSpecificDec(IStrategoTerm decl) {
    return isApplication(decl, "JasminBody");
    //TODO verify
  }

  @Override
  public boolean isSugarDec(IStrategoTerm decl) {
    if (isApplication(decl, "SugarBody")) {
      //sourceContent.setHasNonhaskellDecl(true);
      //FIXME equivalent of line above?
      return true;
    }
    return false;
  }

  @Override
  public boolean isEditorServiceDec(IStrategoTerm decl) {
    //FIXME
    /*
    if (isApplication(decl, "EditorBody")) {   
      sourceContent.setHasNonhaskellDecl(true);
      return true;
    }
    return false;
    */
    throw new NotImplementedException();
  }

  @Override
  public boolean isImportDec(IStrategoTerm decl) {
    //FIXME
      return isApplication(decl, "SugarJasminImport");  
  }

  @Override
  public boolean isPlainDec(IStrategoTerm decl) {
    //FIXME What's this?
    if (isApplication(decl, "PlainDec")) {   
      //sourceContent.setHasNonhaskellDecl(true);
      return true;
    }
    return false;
  }
  
  @Override
  public LanguageLibFactory getFactoryForLanguage() {
    return JasminLibFactory.getInstance();
  }


  
  /*
   * processing stuff follows here
   */
  
  @Override
  public void setupSourceFile(RelativePath sourceFile, Environment environment) {
    outFile = environment.createBinPath(FileCommands.dropExtension(sourceFile.getRelativePath()) + "." + getOriginalFileExtension());
    sourceContent = new JasminSourceFileContent();
  }

  @Override
  public void processNamespaceDec(IStrategoTerm toplevelDecl, Environment environment, IErrorLogger errorLog, RelativePath sourceFile, RelativePath sourceFileFromResult) throws IOException {
    throw new NotImplementedException();
    /*
    String qualifiedModuleName = prettyPrint(getApplicationSubterm(toplevelDecl, "ModuleDec", 0));
    String qualifiedModulePath = qualifiedModuleName.replace('.', '/');
    String declaredModuleName = FileCommands.fileName(qualifiedModulePath);
    moduleName = FileCommands.dropExtension(FileCommands.fileName(sourceFile.getRelativePath()));
    String declaredRelNamespaceName = FileCommands.dropFilename(qualifiedModulePath);
    relNamespaceName = FileCommands.dropFilename(sourceFile.getRelativePath());
    
    RelativePath objectFile = environment.createBinPath(getRelativeNamespaceSep() + moduleName + "." + getGeneratedFileExtension());
    generatedModules.add(objectFile);
    
    sourceContent.setNamespaceDecl(prettyPrint(toplevelDecl));
    
    if (!declaredRelNamespaceName.equals(relNamespaceName))
      setErrorMessage(toplevelDecl,
                      "The declared namespace '" + declaredRelNamespaceName + "'" +
                      " does not match the expected namespace '" + relNamespaceName + "'.", errorLog);
    
    if (!declaredModuleName.equals(moduleName))
      setErrorMessage(toplevelDecl,
                      "The declared module name '" + declaredModuleName + "'" +
                      " does not match the expected module name '" + moduleName + "'.", errorLog);
     */
  }

  @Override
  public void processLanguageSpecific(IStrategoTerm toplevelDecl, Environment environment) throws IOException {
    
    IStrategoTerm term = getApplicationSubterm(toplevelDecl, "JasminBody",0);
    String text = null;
    try {
      text = prettyPrint(term);
    } catch (NullPointerException e) {
      ATermCommands.setErrorMessage(toplevelDecl, "pretty printing Jasmin failed");
    }
    if (text != null)
      sourceContent.addBodyDecl(text);
  }

  @Override
  public String getImportedModulePath(IStrategoTerm toplevelDecl) throws IOException {
    throw new NotImplementedException();
    //FIXME
    //return prettyPrint(getApplicationSubterm(toplevelDecl, "Import", 2)).replace('.', '/');
  }
  
  @Override
  public void addImportModule(IStrategoTerm toplevelDecl, boolean checked) throws IOException {
    throw new NotImplementedException();
    //FIXME
    /*SourceImport imp = new SourceImport(getImportedModulePath(toplevelDecl), prettyPrint(toplevelDecl));
    if (checked)
      sourceContent.addCheckedImport(imp);
    else
      sourceContent.addImport(imp);
    */
  }
  
  @Override
  public String getSugarName(IStrategoTerm decl) throws IOException {
    return moduleName;
  }

  @Override
  public IStrategoTerm getSugarBody(IStrategoTerm decl) {
    return getApplicationSubterm(decl, "SugarBody", 0);
  }

  @Override
  public String prettyPrint(IStrategoTerm term) throws IOException {
    if (ppTable == null) 
      ppTable = ATermCommands.readPrettyPrintTable(ensureFile("org/sugarj/languages/Jasmin.pp").getAbsolutePath());
    
    return ATermCommands.prettyPrint(ppTable, term, interp);
  }
  
  @Override
  protected void compile(List<Path> outFiles, Path bin, List<Path> includePaths, boolean generateFiles) throws IOException {
    throw new NotImplementedException();
    //FIXME
    /*
    if (generateFiles) {
      List<String> cmds = new LinkedList<String>();
      
      for (Path outFile : outFiles)
        cmds.add(outFile.getAbsolutePath());
      /*
      cmds.add("-i");
      if (!includePaths.isEmpty()) {
        StringBuilder searchPath = new StringBuilder("-i");
        for (Path path : includePaths)
          if (new File(path.getAbsolutePath()).isDirectory())
            searchPath.append(path.getAbsolutePath()).append(":");
        searchPath.deleteCharAt(searchPath.length() - 1);
        cmds.add(searchPath.toString());
      }*/
      //new jasmin.Main().run(cmds.toArray(new String[0]));
      //new CommandExecution(false).execute(cmds.toArray(new String[cmds.size()]));
    //}
  }

  @Override
  public boolean isModuleResolvable(String relModulePath) {
    throw new NotImplementedException(); 
    //FIXME
  }

  @Override
  public String getEditorName(IStrategoTerm decl) throws IOException {
    throw new NotImplementedException(); //FIXME
    //return  ;
  }

  @Override
  public IStrategoTerm getEditorServices(IStrategoTerm decl) {
    throw new NotImplementedException(); //FIXME
    //return getApplicationSubterm(decl, "EditorBody", 0);
  }
}
