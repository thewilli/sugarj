package org.sugarj;


public class JasminLibFactory extends LanguageLibFactory {

	private JasminLibFactory() { }
	
	private static JasminLibFactory instance = new JasminLibFactory();
	
	public static JasminLibFactory getInstance() {
		return instance;
	}
	
	/**
	 * @see org.sugarj.LanguageLibFactory#createLanguageLibrary()
	 */
	@Override
	public LanguageLib createLanguageLibrary() {
		return new JasminLib();
	}

}
