package org.sugarj;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoConstructor;
import org.strategoxt.HybridInterpreter;
import org.strategoxt.lang.JavaInteropRegisterer;
import org.strategoxt.lang.Strategy;
import org.sugarj.common.Environment;
import org.sugarj.common.FileCommands;
import org.sugarj.common.errors.SourceCodeException;
import org.sugarj.common.path.AbsolutePath;
import org.sugarj.common.path.Path;
import org.sugarj.common.path.RelativePath;
import org.sugarj.strategies.CallJavaStatic_0_3;
import org.sugarj.strategies.GetSourcePath_0_0;

/**
 * Extended Base processor which Java Strategy import and Strategy compilation support
 * @author Willi Eggeling
 *
 */
public abstract class ExtendedAbstractBaseProcessor extends
AbstractBaseProcessor {

	private static final long serialVersionUID = -6026047119829655978L;

	//namespace term (one or none)
	private IStrategoTerm termNS = null; 
	//import terms
	private List<IStrategoTerm> termsImport = new ArrayList<IStrategoTerm>();
	//base terms
	private List<IStrategoTerm> termsBase = new ArrayList<IStrategoTerm>();
	//Java Strategies
	private List<Strategy> strategies = new ArrayList<Strategy>();
	
	/**
	 * Helper class that provides Strategy registration
	 * @author Willi Eggeling
	 *
	 */
	class InteropRegisterer extends JavaInteropRegisterer{
		public InteropRegisterer(){
			//Register strategies
			super(strategies.toArray(new Strategy[0]));
		}
	}

	/**
	 * Retrieve a private field value using reflection
	 * @param o source object
	 * @param fieldName name of field
	 * @return field value or null
	 */
	private static Object getFieldValue(Object o, String fieldName){
		try{
			Field field = o.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(o);
		}catch(Exception ex){
			return null;
		}
	}
	/**
	 * Retrieve a private method value
	 * @param o source object
	 * @param methodName name of method
	 * @return method value or null
	 */
	private static Object getMethodValue(Object o, String methodName){
		try{
			Method method = o.getClass().getDeclaredMethod(methodName, (Class<?>[])null);
			method.setAccessible(true);
			return method.invoke(o, (Object[])null);
		}catch(Exception ex){
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Register Java-based strategies
	 */
	private void registerStrategies(){
		try{
			//create new instance of strategy registrator
			InteropRegisterer registerer = new InteropRegisterer();
			HybridInterpreter interpreter = getInterpreter();
			//call internal registration mechanism, which would otherwise require the Strategies to be wrapped in a .jar file
			interpreter.getCompiledContext().setFactory((ITermFactory)getFieldValue(interpreter, "recordingFactory"));
			registerer.registerLazy(interpreter.getContext(), interpreter.getCompiledContext(), ClassLoader.getSystemClassLoader());
			interpreter.getCompiledContext().addConstructors((Collection<IStrategoConstructor>) getMethodValue(getFieldValue(interpreter, "recordingFactory"), "getAndClearConstructorRecord"));
			interpreter.getCompiledContext().setFactory((ITermFactory)getMethodValue(getFieldValue(interpreter, "recordingFactory"), "getWrappedFactory"));
		}catch(Exception ex){
			getInterpreter().getIOAgent().printError("Could not register Java Strategies: " + ex.getMessage());
		}
	}


	/**
	 * Get list of Java Strategies that should be registered.
	 * @return List of strategies to register.
	 */
	public List<Strategy> getJavaStrategies(){
		return Collections.emptyList();
	}

	/**
	 * Checks if the current language compiles to terms or not (which is default, compiler takes source file as input)
	 * @return true if language is compiled using a Strategy strategy and input terms, false otherwise
	 */
	private boolean compileToTerms(){
		return getCompileStrategyName() != null;
	}

	/**
	 * Retrieve the Stratego Term the Compiler Strategy gets as argument
	 * @param inputFile Full path of input source file
	 * @param outputDir Full output directory (including trailing '/' or '\')
	 * @param fullOutputFileName Full output path of input file (target name including path) 
	 * @param nsTerm Namespace term, can be null of no namspace was retrieved (Language need to extend ExtendedAbstractBaseLanguage for NS support)
	 * @param importTerms List of import declarations
	 * @param baseTerms List of base declarations (excluding namespace decleration of language extends ExtendedAbstractBaseLanguage)
	 * @return Compiler Strategy input argument or null. If null is returned compilation succeeds without doing anythign (nothing to compile).
	 * @throws SourceCodeException Invalid Source Code input
	 * @throws IOException IO or other problem occured
	 */
	public IStrategoTerm getCompileStrategyArgument(Path inputFile,
			String outputDir, String fullOutputFileName,
			IStrategoTerm nsTerm, IStrategoTerm[] importTerms,
			IStrategoTerm[] baseTerms) throws SourceCodeException, IOException {
		throw new IOException("not implemented: getCompileStrategyArgument()");
	}  

	/**
	 * Parses the result of the compiler strategy execution.
	 * @param result Result term of compiler strategy. May be null if error occured.
	 * @param ex Exception thrown by the Strategy compiler. May be null if no error occured.
	 * @return Generated files. The default generated file (outputDirectory + name-of-source-file + binary extension) does not need to be included, its existence is checked automatically.
	 * @throws SourceCodeException Invalid Source Code input
	 * @throws IOException IO or other problem occured
	 */
	public List<Path> handleCompileStrategyResult(IStrategoTerm result, Exception ex) throws SourceCodeException, IOException{
		return new ArrayList<Path>();
	}

	/**
	 * Name of Compiler Strategy name.
	 * @return Name of Compiler Strategy or null if regular (file-based) compilation should be used.
	 */
	public String getCompileStrategyName(){
		return null;
	}
	
	/**
	 * Retrieve the namespace path of the provided term (e.g. the Java package foo.bar declaration would lead to "foo/bar")
	 * @param nsTerm Namespace term
	 * @return String path of namespace (seperated by '/') or null if unsupported
	 */
	public String getModulePathOfNamespace(IStrategoTerm nsTerm){
		return null;
	}

	/**
	 * File compilation. May not be overwritten if strategy-based compilation is used.
	 * @param inputFile Input file
	 * @param outputDir Output directory including trailing '/' or '\'
	 * @param fullOutputFileName Full output name of input file
	 * @param namespace Namespace decleration (may be null)
	 * @param imports Imports
	 * @param baseDecls Base decleration
	 * @return List of generated files
	 * @throws SourceCodeException
	 * @throws IOException
	 */
	public List<Path> compileFile(
			Path inputFile,
			String outputDir,
			String fullOutputFileName,
			IStrategoTerm namespace,
			IStrategoTerm[] imports,
			IStrategoTerm[] baseDecls
			) throws SourceCodeException, IOException{
		//check if strategy name is provided
		if(getCompileStrategyName() == null)
			throw new IOException("no comile strategy provided");
		registerStrategies(); //register Java-based Strategies to allow their usage in Compiler Strategies
		//retrieve argument for strategy-based compilation
		IStrategoTerm argument = getCompileStrategyArgument(inputFile, outputDir, fullOutputFileName, namespace, imports, baseDecls);
		if(argument == null)
			return Collections.emptyList(); //no argument retrieved, do not compile
		IStrategoTerm result = null;
		Exception ex = null;
		//try to execute compilation strategy. Result may be either an Exception or a Term result
		try{
			interp.setCurrent(argument);
			interp.invoke(getCompileStrategyName());
			result = interp.current();
		}catch(Exception e){
			ex = e;
		}
		//Parse result
		List<Path> paths = handleCompileStrategyResult(result, ex);
		//ensure list is not null
		if(paths == null)
			paths = new ArrayList<Path>();
		else if(Collections.unmodifiableList(paths).getClass().isInstance(paths))
			paths = new ArrayList<Path>(paths); //create new list that can be modified
		if(ex == null){
			//no exception occured, so compilation might have been succesful.
			//check if target file was generated and add it to the list (if not already done by parseCompileStrategyResult)
			Path path = new AbsolutePath(fullOutputFileName);
			if(FileCommands.exists(path) && !paths.contains(path))
				paths.add(path);
		}
		return paths;
	}

	@Override
	/**
	 * Initiliazion of a new file. Need to be called from child classes.
	 * @param sourceFile related source file
	 * @param environment related environment
	 */
	public void init(RelativePath sourceFile, Environment environment) {
		//initialize namespace term (getInterpreter() can be used here)
		termNS = getInterpreter().getFactory().makeAppl(new StrategoConstructor("NoNS", 0));
		//enable source-path resolution. Requires external definition
		strategies.add(new GetSourcePath_0_0(environment.getSourcePath().get(0).getAbsolutePath()));
		//register Java-execution strategy
		strategies.add(CallJavaStatic_0_3.instance);
		//add strategies from base language
		strategies.addAll(getJavaStrategies());
		registerStrategies(); //register strategies to allow their usage in Sugar Libraries
	}

	@Override 
	/**
	 * Retrieve generated source of File content. Should not be overwritten by child classes if strategy-based compilation is used.
	 */
	public String getGeneratedSource() {
		if(!compileToTerms())
			return ""; //skip if regular compilation is used
		IStrategoList terms = getInterpreter().getFactory().makeList(
				termNS, //namespace declaration (optional)
				getInterpreter().getFactory().makeList(termsImport),
				getInterpreter().getFactory().makeList(termsBase)
				);
		return terms.toString();
	};

	@Override
	/**
	 * Process base declarations. Need to be called from child classes.
	 */
	public List<String> processBaseDecl(IStrategoTerm toplevelDecl)
			throws IOException {
		//add namespace decleration if languages compiles using a Strategy strategy
		if(compileToTerms()){
			//check if language supports namespace detection
			if(getLanguage() instanceof ExtendedAbstractBaseLanguage &&
					((ExtendedAbstractBaseLanguage)getLanguage()).isNamespaceDec(toplevelDecl)){
				termNS = toplevelDecl;
			}else{
				//language does not support namespace detection or declaration is not a namespace,
				//so add it to base declarations
				termsBase.add(toplevelDecl);
			}
		}
		return Collections.emptyList();
	}

	@Override
	/**
	 * Process import declarations. Need to be called from child classes.
	 */
	public void processModuleImport(IStrategoTerm toplevelDecl)
			throws IOException {
		//add import if language compiles using strategy strategies
		if(compileToTerms())
			termsImport.add(toplevelDecl);
	}

	/**
	 * Read file content to string
	 * @param path Full path to file
	 * @param encoding Encoding to use
	 * @return File content
	 * @throws IOException Coud not read file when thrown
	 */
	private static String readFile(String path, Charset encoding) 
			throws IOException 
			{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
			}

	@Override
	/**
	 * Compile input files. Should not be overwritten by child classes if strategy-based compilation is used.
	 */
	public List<Path> compile(List<Path> generatedSourceFiles, Path targetDir,
			List<Path> classpath) throws IOException, SourceCodeException {
		List<Path> generatedFiles = new ArrayList<Path>();
		//build output filename
		String outputDirWithSuffix = targetDir.getAbsolutePath();
		if(!outputDirWithSuffix.endsWith("/") && !outputDirWithSuffix.endsWith(File.separator))
			outputDirWithSuffix += File.separator;
		//iterate over each file that should be compiled
		for(Path inputFile : generatedSourceFiles){
			//parse term which was written to the input file before
			IStrategoTerm content = getInterpreter().getFactory().parseFromString(readFile(inputFile.getAbsolutePath(),Charset.defaultCharset()));
			if(
					content == null ||
					content.getSubtermCount() != 3 ||
					content.getSubterm(1).getTermType() != IStrategoTerm.LIST ||
					content.getSubterm(2).getTermType() != IStrategoTerm.LIST
					)
				throw new IOException("Could not parse file: " + inputFile.getAbsolutePath());
			//retrieve namespace
			IStrategoTerm ns = content.getSubterm(0);
			if(ns.getTermType() == IStrategoTerm.APPL && ((IStrategoAppl)ns).getName().equals("NoNS"))
				ns = null; //no namespace provided
			//build target filename
			String targetFile = 
					outputDirWithSuffix;
			//append namespace (if any)
			if(ns != null){
				String nsPath = getModulePathOfNamespace(ns);
				if(nsPath != null && nsPath.length() > 0){
					targetFile += nsPath.replace('/', File.separatorChar) + File.separatorChar;
				}
			}
			//append namespace 
			targetFile
					+= FileCommands.dropExtension(FileCommands.fileName(inputFile))
					+ "." + getLanguage().getBinaryFileExtension();
			
			//compile single file
			List<Path> resultFiles = compileFile(
					inputFile,
					outputDirWithSuffix,
					targetFile,
					ns,
					content.getSubterm(1).getAllSubterms(),
					content.getSubterm(2).getAllSubterms()
					);
			//add result of compilation to list of generated files
			generatedFiles.addAll(resultFiles);
		}
		return generatedFiles;
	}
}
