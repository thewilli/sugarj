package org.sugarj;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static org.sugarj.common.ATermCommands.isApplication;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.sugarj.common.path.Path;

/**
 * SugarDryad Language
 * @author Willi Eggeling <thewilli@googlemail.com>
 *
 */
public class DryadLanguage extends ExtendedAbstractBaseLanguage {

  private static DryadLanguage instance = new DryadLanguage();

  public static DryadLanguage getInstance() {
    return instance;
  }

  @Override
  public AbstractBaseProcessor createNewProcessor() {
    return new DryadProcessor();
  }

  @Override
  public String getVersion() {
    return "dryad-0.2";
  }

  @Override
  public String getLanguageName() {
    return "Dryad";
  }

  @Override
  public String getSugarFileExtension() {
    return "sdr";
  }

  @Override
  public String getBinaryFileExtension() {
    return "class";
  }

  @Override
  public String getBaseFileExtension() {
    return "dr";
  }

  @Override
  public Path getInitGrammar() {
    return ensureFile("org/sugarj/dryad/initGrammar.sdf");
  }

  @Override
  public String getInitGrammarModuleName() {
    return "org/sugarj/dryad/initGrammar";
  }

  @Override
  public Path getInitTrans() {
    return ensureFile("org/sugarj/dryad/initTrans.str");
  }

  @Override
  public String getInitTransModuleName() {
    return "org/sugarj/dryad/initTrans";
  }

  @Override
  public Path getInitEditor() {
    return ensureFile("org/sugarj/dryad/initEditor.serv");
  }

  @Override
  public String getInitEditorModuleName() {
    return "org/sugarj/dryad/initEditor";
  }

  @Override
  public boolean isExtensionDecl(IStrategoTerm decl) {
    if (isApplication(decl, "DryadExtension"))
      return true;
    return false;
  }

  @Override
  public boolean isImportDecl(IStrategoTerm decl) {
    // FIXME: Handle imports (can be either Java- or Bytecode-based)
    if(isApplication(decl, "DryadJavaImport"))
      return true;
    else
      return false;
  }

  @Override
  public boolean isBaseDecl(IStrategoTerm decl) {
    if (isApplication(decl, "DryadJavaType") || isApplication(decl, "DryadBCType") || isNamespaceDec(decl))
      return true;
    return false;
  }

  @Override
  public boolean isPlainDecl(IStrategoTerm decl) {
    return false; //never used
  }

  @Override
  public boolean isNamespaceDec(IStrategoTerm decl) {
    return isApplication(decl, "DryadJavaPackage");
  }

  @Override
  public List<Path> getPackagedGrammars() {
    List<Path> grammars = new LinkedList<Path>(super.getPackagedGrammars());
    grammars.add(ensureFile("org/sugarj/languages/SugarDryad.def"));
    grammars.add(ensureFile("org/sugarj/languages/Dryad.def"));
    return Collections.unmodifiableList(grammars);
  }

}
