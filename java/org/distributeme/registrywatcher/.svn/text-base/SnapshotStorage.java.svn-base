package org.distributeme.registrywatcher;

import org.distributeme.core.ServiceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The SnapshotStorage class provides persistence facilities for the registry
 * snapshots. All file operation are performed in the single local directory.
 */
public class SnapshotStorage {
	private static final String FILENAME_PREFIX = "dimeRegistry-";
	private static final String FILENAME_SUFFIX = ".xml";
	
	// Path toe the local directory where all snapshot files are stored.
	private String path;
	
	private static Logger LOG = LoggerFactory.getLogger(SnapshotStorage.class);

	/**
	 * Creates and configures the SnapshotStorage object. 
	 * @param path path to the local directory where the snapshot files will be
	 *        stored.
	 */
	public SnapshotStorage(String path) {
		if(path == null)
			throw new IllegalArgumentException("path can not be null");
		
		this.path = path;
	}
	
	/**
	 * Return list of timestamps of all registry snapshot stored locally.
	 * 
	 * @return list of timestamps measured in milliseconds.
	 * @throws StorageException if local storage directory is failed to read.
	 */
	public List<Long> getTimestamps() throws StorageException {
		assert path != null;
		
		File dir = new File(path);
		
		if(!dir.exists() && !dir.mkdir())
			throw new StorageException("can not create directory '" + path + "'");			
		if(!dir.isDirectory())
			throw new StorageException("path '" + path + "' is not a directory");
		if(!dir.canRead())
			throw new StorageException("directory '" + path + "' can not be read");

		List<Long> timestamps = new ArrayList<Long>();
		for(String name : dir.list())
			try {
				timestamps.add(getFileTimestamp(name));
			} catch(FilenameFormatException e) {
				LOG.debug("file name '" + name + "' is malformed: " + e.getMessage() + ". Skipping");
			}
			
		Collections.sort(timestamps);
		return timestamps;
	}
	
	/**
	 * Retrieves registry snapshot corresponding to the given timestamp.
	 * 
	 * @param timestamp timestamp in milliseconds of the registry snapshot to
	 *        retrieve from the local storage.
	 * @return Snapshot object read from the local storage.
	 * @throws StorageException if the snapshot file is failed to read.
	 */
	public Snapshot get(long timestamp) throws StorageException {
		if(timestamp <= 0)
			throw new IllegalArgumentException("timestamp must be positive value");
		
		return new Snapshot(readFile(new File(path, composeFileName(timestamp))), timestamp);
	}
	
	/**
	 * Puts registry snapshot to the local storage.
	 * Existent file with the same timestamp will be overwritten.
	 * 
	 * @param snapshot registry snapshot to store locally.
	 * @throws StorageException if the snapshot file can not be written.
	 */
	public void put(Snapshot snapshot) throws StorageException {
		if(snapshot == null)
			throw new IllegalArgumentException("snapshot can not be null");
		
		writeFile(new File(path, composeFileName(snapshot.getTimestamp())), snapshot.getData());
	}

	// Parses file name into the timestamp.
	private static long getFileTimestamp(String fileName) throws FilenameFormatException {
		assert fileName != null;

		if(!fileName.startsWith(FILENAME_PREFIX))
			throw new FilenameFormatException("file name does not starts with '" + FILENAME_PREFIX + "'");

		return parseTimestamp(fileName.substring(FILENAME_PREFIX.length()));
	}
	
	// Parses string representation of the timestamp into the numeric one.
	private static long parseTimestamp(String s) throws FilenameFormatException {
		long timestamp = ServiceDescriptor.parseTimeString(s);
		if(timestamp <= 0)
			throw new FilenameFormatException("timestamp string '" + s + "' is malformed");
		
		return timestamp;
	}
	
	// Composes file name from the timestamp.
	private static String composeFileName(long timestamp) {
		return FILENAME_PREFIX + ServiceDescriptor.getTimeString(timestamp) + FILENAME_SUFFIX;
	}
	
	// Internal exception indicating malformed file name.
	private static class FilenameFormatException extends Exception {
		private static final long serialVersionUID = -9172073084839373843L;
	
		public FilenameFormatException(String message) {
			super(message);
		}
	}

	// Reads content of the file with given name in to the string.
	static String readFile(File file) throws StorageException {
		assert file != null;
		
		if(!file.exists())
			throw new StorageException("file '" + file.getName() + "' does not exists in the directory '" + file.getPath() + "'");			
		if(!file.isFile())
			throw new StorageException("file '" + file.getName() + "' in the directory '" + file.getPath() + "' is not a valid file");
		if(!file.canRead())
			throw new StorageException("file '" + file.getName() + "' in the directory '" + file.getPath() + "' can not be read");

		long length = file.length();
		if(length > Integer.MAX_VALUE)
			throw new StorageException("file '" + file.getName() + "' in the directory '" + file.getPath() + "' is too large");

		byte[] buffer = new byte[(int)length];
		readFileToBuffer(file, buffer);
		
		return new String(buffer);
	}

	// Writes given string into the file with given name.
	static void writeFile(File file, String data) throws StorageException {
		assert file != null;
		
		if(file.exists() && !file.delete() && !file.canWrite())
			throw new StorageException("file '" + file.getName() + "' in the directory '" + file.getPath() + "' exists, can net be deleted and is not writable");

		writeBufferToFile(file, data.getBytes());
	}

	// Helper method to read file into the character buffer.
	private static void readFileToBuffer(File file, byte[] buffer) throws StorageException {
		assert file != null;
		assert buffer != null;
		
		BufferedInputStream buffStream = null;
		FileInputStream fileStream = null;
		
		try {
			fileStream = new FileInputStream(file);
			buffStream = new BufferedInputStream(fileStream);
			
			buffStream.read(buffer);
		} catch(FileNotFoundException e) {
			throw new StorageException("file '" + file.getName() + "' is nt found in the directory '" + file.getPath() + "'", e);
		} catch(IOException e) {
			throw new StorageException(e);
		} finally {
			try {
				if(buffStream != null)
					buffStream.close();
				else
					if(fileStream != null)
						fileStream.close();
			} catch(IOException e) {
				LOG.error("failed to close input file stream: " + e.getMessage(), e);
			}
		}
	}
	
	// Helper method to write character buffer into the file.
	private static void writeBufferToFile(File file, byte[] buffer) throws StorageException {
		assert file != null;
		assert buffer != null;
		
		BufferedOutputStream buffStream = null;
		FileOutputStream fileStream = null;
		
		try {
			fileStream = new FileOutputStream(file);
			buffStream = new BufferedOutputStream(fileStream);
			
			buffStream.write(buffer);
		} catch(FileNotFoundException e) {
			throw new StorageException("file '" + file.getName() + "' is nt found in the directory '" + file.getPath() + "'", e);
		} catch(IOException e) {
			throw new StorageException(e);
		} finally {
			try {
				if(buffStream != null)
					buffStream.close();
				else
					if(fileStream != null)
						fileStream.close();
			} catch(IOException e) {
				LOG.error("failed to close output file stream: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Indicates storage operation failure.
	 */
	public static class StorageException extends Exception {	
		private static final long serialVersionUID = 7376299573694209439L;

		public StorageException(String message) {
			super(message);
		}

		public StorageException(Throwable cause) {
			super(cause);
		}

		public StorageException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
