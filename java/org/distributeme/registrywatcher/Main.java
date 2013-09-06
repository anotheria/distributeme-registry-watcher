package org.distributeme.registrywatcher;

import org.configureme.ConfigurationManager;
import org.distributeme.registrywatcher.RegistryWatcher.WatcherException;
import org.slf4j.LoggerFactory;

/**
 * The Main class allows run registry watcher as java application.
 */
public class Main {
		public static void main(String[] args) {
		WatcherConfig watcherConfig = new WatcherConfig();
		ConfigurationManager.INSTANCE.configure(watcherConfig);
		
		try {
			new RegistryWatcher(watcherConfig).check();
		} catch(WatcherException e) {
			LoggerFactory.getLogger(Main.class).error("fatal error occured", e);
		}
	}
}
