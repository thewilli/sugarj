package org.sugarj;

import static org.sugarj.common.ATermCommands.getApplicationSubterm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoString;
import org.strategoxt.lang.Strategy;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.errors.SourceLocation;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.dryad.Activator;
import org.sugarj.dryad.strategies.GetJarContent_0_0;
import org.sugarj.dryad.strategies.ResolveResource_0_0;
import org.sugarj.strategies.ThrowException_0_1.StrategoCompilationException;
import org.sugarj.util.Pair;

/**
 * Processor of SugarDryad
 * @author Willi Eggeling <thewilli@googlemail.com>
 *
 */
public class DryadProcessor extends ExtendedAbstractBaseProcessor {

	private static final long serialVersionUID = 8526963341981176061L;

	//delimiter for module names
	//Delimiter of (SugarJ-)internal module names (like "org/sugarj/common")
	private static String MODULE_DELMIMITER = "/";
	//target output file
	private Path outFile;
	//Name of namespace
	private String namespace;
	//SugarJ Environment for file processing
	private Environment environment;

	@Override
	public AbstractBaseLanguage getLanguage() {
		return DryadLanguage.getInstance();
	}

	@Override
	public List<Strategy> getJavaStrategies(){
		//add required Java-Strategies
		ArrayList<Strategy> strategies = new ArrayList<Strategy>();
		strategies.add(ResolveResource_0_0.instance);
		strategies.add(GetJarContent_0_0.instance);
		return strategies;
	}

	@Override
	public void init(RelativePath sourceFile, Environment environment) {
		super.init(sourceFile, environment);
		this.environment = environment;
		//Entry point: Processing of new file
		environment.addToIncludePath(new AbsolutePath(Activator.getPluginPath("/ext")));
		//create output file (which holds the built file content later)
		this.outFile = 
				environment.createOutPath(
						FileCommands.dropExtension(sourceFile.getRelativePath()) + "." + DryadLanguage.getInstance().getBaseFileExtension()
						);
	}

	@Override
	public void processModuleImport(IStrategoTerm toplevelDecl) throws IOException {
		super.processModuleImport(toplevelDecl);
	}

	private void processNamespaceDecl(IStrategoTerm toplevelDecl) throws IOException {
		//get namespace
		namespace = "";
		try{
			namespace = getModulePathOfNamespace(toplevelDecl);
		}catch(Exception ex){}
	}

	@Override
	public List<String> processBaseDecl(IStrategoTerm toplevelDecl) throws IOException {
		super.processBaseDecl(toplevelDecl);
		//Term can be either a base- (Type-) or namespace ( = "package abc.def;") declaration here
		if(((DryadLanguage)getLanguage()).isNamespaceDec(toplevelDecl)){
			processNamespaceDecl(toplevelDecl);
			return Collections.emptyList();
		}
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
				+ MODULE_DELMIMITER
				+ getTermInputPath((IStrategoAppl)term.getSubterm(1));
	}
	
	@Override
	public String getModulePathOfNamespace(IStrategoTerm nsTerm){
		StrategoList namespaceParts = (StrategoList)nsTerm.getSubterm(0).getSubterm(1).getSubterm(0);
		String namespace = "";
		//build namespace out of term fragments
		for(IStrategoTerm subId : namespaceParts.getAllSubterms()){
			if(namespace.length() > 0)
				namespace += MODULE_DELMIMITER;
			namespace += ((StrategoString)subId.getSubterm(0)).stringValue();
		} 
		return namespace;
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
					path += MODULE_DELMIMITER;
				path += ((IStrategoString) term.getSubterm(0)).stringValue();
			}
			path += MODULE_DELMIMITER + "*";
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
			//use the class loader to determine whether the import is loadble
			return getClass().getClassLoader().loadClass(relModulePath.replace('/', '.')) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	/**
	 * Check if an import of the current file imports another file representing a class (not a pure Sugar Library).
	 * @param importTerm Import Declaration
	 * @return Import Term representing .class file of target, null otherweise
	 */
	private IStrategoTerm getOptionalImportTerm(IStrategoTerm importTerm){
		//get module path
		String modulePath = getModulePathOfImport(importTerm);
		//check if .class file exists for module name
		File f =
				environment.createOutPath(
						getModulePathOfImport(importTerm) + 
						"." + getLanguage().getBinaryFileExtension()
				).getFile();
		if(!f.exists())
			return null; //no binary file exists
		//create import term
		String[] packageNames = modulePath.split(MODULE_DELMIMITER);
		ITermFactory factory = getInterpreter().getFactory();
		List<IStrategoTerm> packageParts = new ArrayList<IStrategoTerm>();
		for(int i = 0; i < packageNames.length - 1; i++)
			packageParts.add(factory.makeString(packageNames[i]));
		return factory.makeAppl(
				factory.makeConstructor("ClassImport", 3),
				factory.makeString(packageNames[packageNames.length - 1]),
				factory.makeList(packageParts),
				factory.makeString(f.getAbsolutePath())
		);
	}

	@Override
	public String getExtensionName(IStrategoTerm decl) throws IOException {
		return ((StrategoString)decl.getSubterm(0)).stringValue();
	}

	@Override
	public IStrategoTerm getExtensionBody(IStrategoTerm decl) {
		return getApplicationSubterm(decl, "DryadExtension", 1);
	}


	@Override
	public Path getGeneratedSourceFile() {
		return outFile;
	}
	
	@Override
	protected IStrategoTerm annotateTerm(IStrategoTerm term){
		//Do not annotate this type, as DryadCompiler has problems with it
		//if(term.getTermType() == IStrategoTerm.APPL && ((IStrategoAppl)term).getName().equals("ClassOrInterfaceType"))
		//	return term; //do not annotate
		return super.annotateTerm(term);
	}

	@Override
	public IStrategoTerm getCompileStrategyArgument(Path inputFile,
			String outputDir, String fullOutputFileName,
			IStrategoTerm nsTerm, IStrategoTerm[] importTerms,
			IStrategoTerm[] baseTerms) throws SourceCodeException, IOException {
		ITermFactory factory = getInterpreter().getFactory();
		if(baseTerms.length == 0)
			return null; //nothing to compile
		
		if(nsTerm == null)
			nsTerm = factory.makeAppl(new StrategoConstructor("None", 0));
		else
			nsTerm = factory.makeAppl(
					factory.makeConstructor("Some", 1),
					nsTerm.getSubterm(0)
			);
		//create of list of additional imports. these files are loaded by the Dryad Compiler
		//for type -checking and -resolution
		List<IStrategoTerm> importHelpers = new ArrayList<IStrategoTerm>();
		//add default runtime .jar
		String jarPath = new File(Class.class.getResource("Class.class").getPath()).toString().split("!")[0].substring(5);
		if(jarPath.startsWith("\\"))
			jarPath = jarPath.substring(1); //remove starting backslash on Windows paths
		importHelpers.add(
			factory.makeAppl(
					factory.makeConstructor("Jar", 1),
					factory.makeString(jarPath)
			)
		);
		
		//Remove TopLeveDeclaration-Wrapper
		for(int i = 0; i < importTerms.length; i++){
			//check if import is another SugarDryad file which should be included
			IStrategoTerm importTerm = getOptionalImportTerm(importTerms[i]);
			if(importTerm != null)
				importHelpers.add(importTerm);
			importTerms[i] = importTerms[i].getSubterm(0);
		}
		for(int i = 0; i < baseTerms.length; i++)
			baseTerms[i] = baseTerms[i].getSubterm(0);

		IStrategoTerm[] termArgs = new IStrategoTerm[3];
		termArgs[0] = factory.makeString(outputDir); //factory.makeString(fullOutputFileName);
		termArgs[1] = factory.makeList(importHelpers);
		//check if content is a Bytecode Class File (instead of a Java File)
		if(
				baseTerms[0].getTermType() == IStrategoTerm.APPL &&
				((IStrategoAppl)baseTerms[0]).getName().equals("ClassFile")
		){
			//set output path to full filename to bypass package limitations
			termArgs[0] = factory.makeString(fullOutputFileName);
		}//else{
			termArgs[2] = factory.makeAppl(
					factory.makeConstructor("CompilationUnit",3),
					nsTerm,
					factory.makeList(importTerms),
					factory.makeList(baseTerms)
			);
		//}
		return factory.makeList(termArgs);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Path> handleCompileStrategyResult(IStrategoTerm result,
			Exception ex, Path inputFile) throws SourceCodeException, IOException {
		ArrayList<Path> generatedFiles = new ArrayList<Path>();
		if(ex != null){ 
			//error occured
			if(ex.getCause() instanceof StrategoCompilationException){
				IStrategoTerm term = ((StrategoCompilationException)ex.getCause()).getTerm();
				String msg = ex.getCause().getMessage() + ": " + term.toString();
				IStrategoList annotations = term.getAnnotations();
				if(annotations != null){
					for(IStrategoTerm anno : annotations){
						if(anno.getTermType() == IStrategoTerm.APPL && ((IStrategoAppl)anno).getName().equals("Pos")){
							int 
								leftLine = ((IStrategoInt)anno.getSubterm(0).getSubterm(0)).intValue(), 
								rightLine = ((IStrategoInt)anno.getSubterm(1).getSubterm(0)).intValue(),
								leftCol = ((IStrategoInt)anno.getSubterm(0).getSubterm(1)).intValue(),
								rightCol = ((IStrategoInt)anno.getSubterm(1).getSubterm(1)).intValue();
							Pair<SourceLocation,String> loc = new Pair<SourceLocation,String>(new SourceLocation(inputFile, leftLine, rightLine, leftCol, rightCol),ex.getCause().getMessage());
							throw new SourceCodeException(loc);
						}
					}
				}
				//no location information found
				throw new SourceCodeException(new SourceLocation(inputFile, 1, 1, 1, 1), msg);
			}
			throw new IOException(ex.getMessage());
			//throw new SourceCodeException(new SourceLocation(inputFile, 1, 1, 1, 1), ex.getMessage());
		}
		if(result.getTermType() == IStrategoTerm.LIST){
			for(IStrategoTerm resultTerm : result.getAllSubterms()){
				if(resultTerm.getTermType() == IStrategoTerm.STRING){
					Path p = new AbsolutePath(((IStrategoString)resultTerm).stringValue());
					if(p.getFile().exists())
						generatedFiles.add(p);
				}else{
					//compilation did not succeed
					throw new IOException("Compilation failed: " + resultTerm.toString());
				}
			}
		}
		return generatedFiles;
	}

	@Override
	public String getCompileStrategyName() {
		return "compileClass";
	}









}
