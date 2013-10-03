package org.sugarj;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static org.sugarj.common.ATermCommands.isApplication;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.path.Path;

public class JasminLanguage extends AbstractBaseLanguage {
  
  private static JasminLanguage instance = new JasminLanguage();
  
  public static JasminLanguage getInstance() {
    return instance;
  }

  @Override
  public AbstractBaseProcessor createNewProcessor() {
    return new JasminProcessor();
  }

  @Override
  public String getVersion() {
    return "jasmin-0.1a";
  }

  @Override
  public String getLanguageName() {
    return "Jasmin";
  }

  @Override
  public String getSugarFileExtension() {
    return "sj";
  }

  @Override
  public String getBinaryFileExtension() {
    return ".class";
  }

  @Override
  public String getBaseFileExtension() {
    return "j";
  }

  @Override
  public Path getInitGrammar() {
    return ensureFile("org/sugarj/jasmin/initGrammar.sdf");
  }

  @Override
  public String getInitGrammarModuleName() {
    return "org/sugarj/jasmin/initGrammar";
  }

  @Override
  public Path getInitTrans() {
    return ensureFile("org/sugarj/jasmin/initTrans.str");
  }

  @Override
  public String getInitTransModuleName() {
    return "org/sugarj/jasmin/initTrans";
  }

  @Override
  public Path getInitEditor() {
    return ensureFile("org/sugarj/jasmin/initEditor.serv");
  }

  @Override
  public String getInitEditorModuleName() {
    return "org/sugarj/jasmin/initEditor";
  }

  @Override
  public boolean isExtensionDecl(IStrategoTerm decl) {
    if(isApplication(decl,"JasminExtension"))
      return true;
    return false;
  }
      
  @Override
  public boolean isImportDecl(IStrategoTerm decl) {
    if(isApplication(decl,"JasminImport"))
      return true;
    return false;
  }

  @Override
  public boolean isBaseDecl(IStrategoTerm decl) {
    if(isApplication(decl,"JasminBody") || isNamespaceDec(decl))
      return true;
    return false;
  }

  @Override
  public boolean isPlainDecl(IStrategoTerm decl) {
    if(isApplication(decl,"PlainDec"))
      return true;
    return false;
  }
  
  public boolean isNamespaceDec(IStrategoTerm decl) {
    return isApplication(decl, "SugarModule");
  }
  
  @Override
  public List<Path> getPackagedGrammars() {
    List<Path> grammars = new LinkedList<Path>(super.getPackagedGrammars());
    grammars.add(ensureFile("org/sugarj/languages/SugarJasmin.def"));
    grammars.add(ensureFile("org/sugarj/languages/Jasmin.def"));
    return Collections.unmodifiableList(grammars);
  }

}
