package org.distributeme.registrywatcher;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe(name="registry-watcher-config")
public class WatcherConfig {
	/**
	 * Name or address of the DistributeMe registry host to fetch snapshots from.
	 */
	@Configure
	public String registryHost = "localhost";

	/**
	 * Port number listened by the DistributeMe registry service.
	 */
	@Configure
	public int registryPort = 9229;

	/**
	 * Path to the directory within the local file system to store registry
	 * snapshots at.
	 */
	@Configure
	public String localPath = ".";
	
	/**
	 * Timeout in milliseconds to establish connection to the host.
	 */
	@Configure
	public int connectTimeout = 15000;
	
	/**
	 * Timeout in milliseconds to read data after connection established.
	 */
	@Configure
	public int readTimeout = 15000;

	/**
	 * EMail address to send notifications to.
	 */
	@Configure
	public String notificationRecepientEmail = null;

	/**
	 * EMail address identifying the sender.
	 */
	@Configure
	public String notificationSenderEmail = null;
	
	/**
	 * Style of the snapshot difference file.
	 * Possible values are: "UNIFIED" and "HTML".
	 */
	@Configure
	public String diffStyle = "UNIFIED";
	
	/**
	 * Subject of the notification eMail message.
	 */
	@Configure
	public String notificationSubject = "DistributeMe registry watcher notification";
}
