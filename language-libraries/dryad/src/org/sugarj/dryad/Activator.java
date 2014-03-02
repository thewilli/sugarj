package org.sugarj.dryad;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.sugarj.DryadLanguage;
import org.sugarj.BaseLanguageRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.sugarj.dryad"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * Retrieve a plugin-internal path to a file or folder
	 * @param item path to retrieve
	 * @return null if not found or path as String
	 */
	public static String getPluginPath(String item){
	  URL fileUrl = FileLocator.find(
	      Platform.getBundle(PLUGIN_ID),
	      new org.eclipse.core.runtime.Path(item),
	      null
	  );
	  if(fileUrl == null)
	    return null; //not found
	  try {
	    //return resulting path (strip 'file:')
      return FileLocator.toFileURL(fileUrl).toString().substring(5);
    } catch (IOException e) {
     return null;
    }
	}
	
	/**
	 * The constructor
	 */
	public Activator() {
	  BaseLanguageRegistry.getInstance().registerBaseLanguage(DryadLanguage.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
