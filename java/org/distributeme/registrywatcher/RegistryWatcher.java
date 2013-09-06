package org.distributeme.registrywatcher;

import net.anotheria.communication.data.MailFileEntry;
import net.anotheria.util.NumberUtils;
import org.distributeme.registrywatcher.MailSender.SenderException;
import org.distributeme.registrywatcher.SnapshotFetcher.FetcherException;
import org.distributeme.registrywatcher.SnapshotStorage.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The RegistryWatcher class implements high-level logic and links all parts
 * together to fetch DistributeMe registry snapshots, store them locally in
 * files, compare and send notifications if any changes occurs.
 */
public class RegistryWatcher {
	private static final String TMP_FILE_PREFIX = "dime";
	private static final String ATTACHMENT_FILE_NAME = "registry-changes";
	
	private String registryServerAddress;
	
	private SnapshotStorage storage;
	private SnapshotFetcher fetcher;
	private SnapshotComparator comparator;
	private MailSender sender;
	
	private static Logger LOG = LoggerFactory.getLogger(RegistryWatcher.class);

	/**
	 * Creates and initializes RegistryWatcher object with the configuration data.
	 * 
	 * @param config configuration data.
	 * @throws WatcherException if fatal error occur.
	 */
	public RegistryWatcher(WatcherConfig config) throws WatcherException {
		if(config == null)
			throw new IllegalArgumentException("config can not be null");
		
		try {
			storage = new SnapshotStorage(config.localPath);
			fetcher = new SnapshotFetcher(config.registryHost, config.registryPort, config.connectTimeout, config.readTimeout);
			comparator = new SnapshotComparator(config.diffStyle);
			sender = new MailSender(config.notificationRecepientEmail, config.notificationSenderEmail, config.notificationSubject);
		} catch(FetcherException e) {
			LOG.error("failed to initialize snapshot fatcher: " + e.getMessage(), e);
			throw new WatcherException(e);
		}
		
		registryServerAddress = config.registryHost + ":" + config.registryPort;
	}
	
	/**
	 * Checks for the registry update.
	 * Fetches actual registry snapshot, store it locally in the XML file,
	 * compares with the previously fetched snapshot and send eMail notification
	 * if any changes were detected. EMail also will be sent if failed to fetch
	 * actual registry snapshot.
	 * 
	 * @throws WatcherException if fatal error occur.
	 */
	public void check() throws WatcherException {
		Snapshot current = fetchActualSnapshot();
		Snapshot previous = retrievePreviousSnapshot();

		storeSnapshot(current);
		
		if(previous != null && !previous.equals(current)) {
			String message = composeSuccessMessage(current.getTimestamp());
			MailFileEntry diffAttachment = createDiffAttachment(previous, current);
			
			sendMail(message, diffAttachment);
		}
	}
	
	private Snapshot fetchActualSnapshot() throws WatcherException {
		try {
			return fetcher.fetch();
		} catch(FetcherException e) {
			LOG.error("failed to fetch new registry snapshot: " + e.getMessage(), e);			
			sendMail(composeFetchFailureMessage());
			
			throw new WatcherException(e);
		}
	}
	
	private Snapshot retrievePreviousSnapshot() {
		try {
			List<Long> timestamps = storage.getTimestamps();
			if(!timestamps.isEmpty()) {
				long latest = timestamps.get(timestamps.size() - 1);
				return storage.get(latest);
			}
		} catch(StorageException e) {
			LOG.error("failed to retrieve previous registry snapshot: " + e.getMessage(), e);
		}

		return null;
	}
	
	private void storeSnapshot(Snapshot snapshot) {
		try {
			storage.put(snapshot);
		} catch(StorageException e) {
			LOG.error("failed to store snapshot: " + e.getMessage(), e);
		}
	}
	
	private void sendMail(String message, MailFileEntry... attachments) {
		assert message != null;
		
		try {
			sender.send(message, attachments);
		} catch(SenderException e) {
			LOG.error("failed to send mail: " + e.getMessage(), e);
		}
	}
	
	private MailFileEntry createDiffAttachment(Snapshot previous, Snapshot current) throws WatcherException {
		assert previous != null;
		assert current != null;
		
		String diff = comparator.getDiff(previous, current);
		try {
			return createAttachment(ATTACHMENT_FILE_NAME + comparator.getFileType(), diff);
		} catch(StorageException e) {
			LOG.error("failed to create message attachment: " + e.getMessage(), e);
			throw new WatcherException(e);
		}
	}
	
	private static MailFileEntry createAttachment(String filename, String filedata) throws StorageException {
		assert filename != null;
		assert filedata != null;
		
		File tmp;
		try {
			tmp = File.createTempFile(TMP_FILE_PREFIX, null);
		} catch(IOException e) {
			throw new StorageException("can not create temporary file", e);
		}
		
		tmp.deleteOnExit();
		SnapshotStorage.writeFile(tmp, filedata);
		
		return new MailFileEntry(tmp, filename);
	}
	
	private String composeSuccessMessage(long timestamp) {
		return new StringBuilder()
			.append("DistributeMe registry at the ")
			.append(registryServerAddress)
			.append(" update detected ")
			.append(NumberUtils.makeISO8601TimestampString(timestamp))
			.toString();
	}
	
	private String composeFetchFailureMessage() {
		return new StringBuilder()
			.append("Failed to fetch DistributeMe registry snapshot from the ")
			.append(registryServerAddress)
			.append(" ")
			.append(NumberUtils.makeISO8601TimestampString(System.currentTimeMillis()))
			.toString();
	}

	/**
	 * Indicates fatal unrecoverable error.
	 */
	public static class WatcherException extends Exception {
		private static final long serialVersionUID = -8084973336966243770L;
		
		public WatcherException(Throwable cause) {
			super(cause);
		}
	}
}
