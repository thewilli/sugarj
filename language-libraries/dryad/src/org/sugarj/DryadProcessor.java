package org.sugarj;

import static org.sugarj.common.ATermCommands.getApplicationSubterm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.StrategoConstructor;
import org.spoofax.terms.StrategoList;
import org.spoofax.terms.StrategoString;
import org.strategoxt.lang.Strategy;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.dryad.Activator;
import org.sugarj.dryad.strategies.ResolveResource_0_0;
import org.sugarj.dryad.strategies.STRJVM__callstatic__string__stringstring_0_5;
import org.sugarj.dryad.strategies.WriteClassFile_0_1;

public class DryadProcessor extends ExtendedAbstractBaseProcessor {

	private static final long serialVersionUID = 8526963341981176061L;

	//delimiter for module names
	//Delimiter of (SugarJ-)internal module names (like "org/sugarj/common")
	private static String MODULE_DELMIMITER = "/";

	//target output file
	private Path outFile;
	//Name of namespace
	private String namespace;

	@Override
	public AbstractBaseLanguage getLanguage() {
		return DryadLanguage.getInstance();
	}

	@Override
	public List<Strategy> getJavaStrategies(){
		ArrayList<Strategy> strategies = new ArrayList<Strategy>();
		strategies.add(ResolveResource_0_0.instance);
		strategies.add(STRJVM__callstatic__string__stringstring_0_5.instance);
		strategies.add(WriteClassFile_0_1.instance);
		return strategies;
	}

	@Override
	public void init(RelativePath sourceFile, Environment environment) {
		super.init(sourceFile, environment);
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
		StrategoList namespaceParts = (StrategoList)toplevelDecl.getSubterm(0).getSubterm(1).getSubterm(0);
		namespace = "";
		//build namespace out of term fragments
		for(IStrategoTerm subId : namespaceParts.getAllSubterms()){
			if(namespace.length() > 0)
				namespace += MODULE_DELMIMITER;
			namespace += ((StrategoString)subId.getSubterm(0)).stringValue();
		}
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
			return getClass().getClassLoader().loadClass(relModulePath.replace('/', '.')) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
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
	public IStrategoTerm getCompileStrategyArgument(Path inputFile,
			String outputDir, String fullOutputFileName,
			IStrategoTerm nsTerm, IStrategoTerm[] importTerms,
			IStrategoTerm[] baseTerms) throws SourceCodeException, IOException {
		if(baseTerms.length == 0)
			return null; //nothing to compile

		if(nsTerm == null)
			nsTerm = getInterpreter().getFactory().makeAppl(new StrategoConstructor("None", 0));
		else
			nsTerm = getInterpreter().getFactory().makeAppl(
					getInterpreter().getFactory().makeConstructor("Some", 1),
					nsTerm.getSubterm(0)
					);
		//Remove TopLeveDeclaration-Wrapper
		for(int i = 0; i < importTerms.length; i++)
			importTerms[i] = importTerms[i].getSubterm(0);
		for(int i = 0; i < baseTerms.length; i++)
			baseTerms[i] = baseTerms[i].getSubterm(0);

		IStrategoTerm[] termArgs = new IStrategoTerm[2];
		termArgs[0] = getInterpreter().getFactory().makeString(fullOutputFileName);
		//check if content is a Bytecode Class File (instead of a Java File)
		if(
				baseTerms[0].getTermType() == IStrategoTerm.APPL &&
				((IStrategoAppl)baseTerms[0]).getName().equals("ClassFile")
				){
			termArgs[1] = baseTerms[0]; //BC Classfile
		}else{
			termArgs[1] = getInterpreter().getFactory().makeAppl(
					getInterpreter().getFactory().makeConstructor("CompilationUnit",3),
					nsTerm,
					getInterpreter().getFactory().makeList(importTerms),
					getInterpreter().getFactory().makeList(baseTerms)
					);
		}
		return getInterpreter().getFactory().makeList(termArgs);
	}



	@Override
	public List<Path> parseCompileStrategyResult(IStrategoTerm result,
			Exception ex) throws SourceCodeException, IOException {
		if(ex != null)
			throw new IOException("Compilation failed: " + ex.getMessage());
		if(result.getTermType() == IStrategoTerm.INT){
			if(((IStrategoInt)result).intValue() != 0)
				throw new IOException("Compiling failed with error code: " + ((IStrategoInt)result).intValue());
		}else{
			throw new IOException("Compilation failed: " + result.toString());
		}
		return new ArrayList<Path>();
	}

	@Override
	public String getCompileStrategyName() {
		return "compileClass";
	}









}
