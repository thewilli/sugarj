package org.sugarj;

import static org.sugarj.common.ATermCommands.getApplicationSubterm;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoString;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.dryad.Activator;
import org.sugarj.dryad.strategies.DryadStrategyRegisterer;

public class DryadProcessor extends AbstractBaseProcessor {

  private static final long serialVersionUID = 8526963341981176061L;

  //target output file
  private Path outFile;
  //Name of module
  private String moduleName;
  //Name of namespace
  private String namespace;
  //content of file, hold for output ("pretty-printing")
  private IStrategoTerm modulePackage; //package of module
  private List<IStrategoTerm> moduleImports; //imports of module
  private List<IStrategoTerm> moduleTypes; //types defined by module (classes, interfaces, ..)
  
  
  @Override
  public AbstractBaseLanguage getLanguage() {
    return DryadLanguage.getInstance();
  }

  @Override
  public void init(RelativePath sourceFile, Environment environment) {
    //Entry point: Processing of new file
    environment.addToIncludePath(new AbsolutePath(Activator.getPluginPath("/ext")));
    //create output file (which holds the built file content later)
    this.outFile = 
        environment.createOutPath(
            FileCommands.dropExtension(sourceFile.getRelativePath()) + "." + DryadLanguage.getInstance().getBaseFileExtension()
        );
    //initialize file content term
    modulePackage = getInterpreter().getFactory().parseFromString("None()");
    moduleImports = new ArrayList<IStrategoTerm>();
    moduleTypes   = new ArrayList<IStrategoTerm>();;
  }

  @Override
  public void processModuleImport(IStrategoTerm toplevelDecl) throws IOException {
    moduleImports.add(toplevelDecl.getSubterm(0));
  }
 
  private void processNamespaceDecl(IStrategoTerm toplevelDecl) throws IOException {
    StrategoList namespaceParts = (StrategoList)toplevelDecl.getSubterm(0).getSubterm(1).getSubterm(0);
    namespace = "";
    //build namespace out of term fragments
    for(IStrategoTerm subId : namespaceParts.getAllSubterms()){
      if(namespace.length() > 0)
        namespace += ".";
      namespace += ((StrategoString)subId.getSubterm(0)).stringValue();
    }
    //Store package description for compilation
    modulePackage = getInterpreter().getFactory().makeAppl(
        getInterpreter().getFactory().makeConstructor("Some", 1),
        toplevelDecl.getSubterm(0)
    );
  }

  @Override
  public List<String> processBaseDecl(IStrategoTerm toplevelDecl) throws IOException {
    //Term can be either a base- (Type-) or namespace ( = "package abc.def;") declaration here
    if(((DryadLanguage)getLanguage()).isNamespaceDec(toplevelDecl)){
      processNamespaceDecl(toplevelDecl);
      return Collections.emptyList();
    }
    //Add type
    moduleTypes.add(toplevelDecl.getSubterm(0)); //TODO: Only if regular, non-sugar Type
    
    return Collections.emptyList();
  }

  @Override
  public String getNamespace() {
    return namespace;
  }
  
  /**
   * Converts an ATerm-encoded Java import to its string representation
   */
  private static String getTermInputPath(IStrategoAppl term){
    if(term.getName().equals("Id"))
      return ((StrategoString) term.getSubterm(0)).stringValue();
    if(term.getSubtermCount() == 1)
      return getTermInputPath((IStrategoAppl)term.getSubterm(0));
    return
        getTermInputPath((IStrategoAppl)term.getSubterm(0)) 
        + "."
        + getTermInputPath((IStrategoAppl)term.getSubterm(1));
  }

  @Override
  public String getModulePathOfImport(IStrategoTerm decl) {
    IStrategoAppl importTerm = (IStrategoAppl)decl.getSubterm(0);
    //Add to list of imports for compilation
    String path = null;
    if(importTerm.getName().equals("TypeImportOnDemandDec")){
      //import some.type.*;
      path = "";
      for(IStrategoTerm term : importTerm.getSubterm(0).getSubterm(0).getAllSubterms()){
        if(path.length() > 0)
          path += ".";
        path += ((IStrategoString) term.getSubterm(0)).stringValue();
      }
      path += ".*";
    }else if(importTerm.getName().equals("TypeImportDec")){
      //import some.type;
      path = getTermInputPath((IStrategoAppl)importTerm.getSubterm(0));      
    }else{
      getInterpreter().getIOAgent().printError("Unknown Dryad import: " + importTerm.getName());
    }
    return path;
  }

  @Override
  public boolean isModuleExternallyResolvable(String relModulePath) {
    if (relModulePath.endsWith("*"))
      return true;
    try {
      return getClass().getClassLoader().loadClass(relModulePath.replace('/', '.')) != null;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public String getExtensionName(IStrategoTerm decl) throws IOException {
    return moduleName;
  }

  @Override
  public IStrategoTerm getExtensionBody(IStrategoTerm decl) {
    //FIXME
    return getApplicationSubterm(decl, "DryadExtension", 0);
  }

  @Override
  public String getGeneratedSource() {
    if(moduleTypes.size() == 0)
      return ""; //no types found
    
    IStrategoTerm classResult = getInterpreter().getFactory().makeAppl(
      getInterpreter().getFactory().makeConstructor("CompilationUnit",3),
      modulePackage,
      getInterpreter().getFactory().makeList(moduleImports),
      getInterpreter().getFactory().makeList(moduleTypes)
    );
    //remove all annotations and return
    return classResult.toString().replaceAll("\\{[^\\}]+\\}", "");
  }

  @Override
  public Path getGeneratedSourceFile() {
   return outFile;
  }
  
  private static String readFile(String path, Charset encoding) 
      throws IOException 
    {
      byte[] encoded = Files.readAllBytes(Paths.get(path));
      return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

  private String compileFile(Path inputFile, String outputDir) throws IOException, SourceCodeException{
    String targetFile = 
        outputDir
        + FileCommands.dropExtension(FileCommands.fileName(inputFile))
        + DryadLanguage.getInstance().getBinaryFileExtension();
    String fileContent = readFile(inputFile.getAbsolutePath(),Charset.defaultCharset());
    IStrategoTerm[] termArgs = new IStrategoTerm[2];
    termArgs[0] = getInterpreter().getFactory().makeString(targetFile);
    termArgs[1] = getInterpreter().getFactory().parseFromString(fileContent);
    if(termArgs[1] == null)
      throw new IOException("Could not parse file: " + inputFile.getAbsolutePath());
    //invoke strategy
    IStrategoTerm result =
        getInterpreter().getCompiledContext().invokeStrategy("compileClass", getInterpreter().getFactory().makeList(termArgs));
    if(result.getTermType() == IStrategoTerm.INT){
    if(((IStrategoInt)result).intValue() != 0)
      throw new IOException("Compiling failed with error code: " + ((IStrategoInt)result).intValue());
    }else{
      throw new IOException("Compilation failed: " + result.toString());
    }
    if(FileCommands.fileExists(new AbsolutePath(targetFile)))
      return targetFile; //compiling succeeded
    else
      return null; //compiling failed for file
  }
  
  @Override
  public List<Path> compile(List<Path> generatedSourceFiles, Path targetDir, List<Path> classpath) throws IOException, SourceCodeException {
    //create list of generated files
    LinkedList<Path> outputFiles = new LinkedList<Path>();
    //Ensure Java-based strategies are registered
    DryadStrategyRegisterer.registerStrategies(getInterpreter());
    //check amount of files to compile
    if(generatedSourceFiles.size() == 0)
      return outputFiles; //nothing to do
    
    //build output filename
    String outputDirWithSuffix = targetDir.getAbsolutePath();
    if(!outputDirWithSuffix.endsWith("/") && !outputDirWithSuffix.endsWith(File.separator))
      outputDirWithSuffix += File.separator;
    
    try{
      for(Path inputFile : generatedSourceFiles){
        String resultFile = compileFile(inputFile, outputDirWithSuffix);
        if(resultFile != null)
          outputFiles.add(new AbsolutePath(resultFile));
      }
    }catch(IOException ex){
      throw ex;
    }catch(Exception ex){
      throw new IOException(ex.getMessage());
    }
    
    return outputFiles;
  }

}
