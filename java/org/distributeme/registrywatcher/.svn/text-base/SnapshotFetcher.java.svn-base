package org.distributeme.registrywatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * The SnapshotFetcher class fetches DistributeMe registry snapshots via the HTTP.
 * @see Snapshot
 */
public class SnapshotFetcher {
	private static final String URI = "/distributeme/registry/list";
	private URL url;
	private int connectTimeout;
	private int readTimeout;
	
	private static Logger LOG = LoggerFactory.getLogger(SnapshotFetcher.class);

	/**
	 * Creates and configures the SnapshotFetcher object.
	 * 
	 * @param host name or address of the DistributeMe registry host.
	 * @param port port number listened by the DistributeMe registry service.
	 * @param connectTimeout timeout in milliseconds to establish connection to
	 *        the host.
	 * @param readTimeout timeout in milliseconds to read data after connection
	 *        established.
	 * @throws FetcherException if host address or/and port number are malformed.
	 */
	public SnapshotFetcher(String host, int port, int connectTimeout, int readTimeout) throws FetcherException {
		if(host == null)
			throw new IllegalArgumentException("host can not be null");
		if(port <= 0)
			throw new IllegalArgumentException("port must have positive value");
		
		try {
			url = new URL("http", host, port, URI);
		} catch(MalformedURLException e) {
			throw new FetcherException("host and/or port are incorrect", e);
		}
		
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}
	
	/**
	 * Fetches current snapshot of the DistributeMe registry.
	 * 
	 * @return actual snapshot of the registry.
	 * @throws FetcherException if timeout elapsed or I/O error occur.
	 */
	public Snapshot fetch() throws FetcherException {
		assert url != null;
		
		HttpURLConnection connection = null;
		InputStreamReader stream = null;
		BufferedReader reader = null;
		
		try {
			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("GET");
			
			connection.connect();
			
			stream = new InputStreamReader(connection.getInputStream());
			reader = new BufferedReader(stream);
			StringBuilder str = new StringBuilder();
			
			String line;
			while((line = reader.readLine()) != null)
				str.append(line).append('\n');
			
			return new Snapshot(str.toString());
		} catch(SocketTimeoutException e) {
			throw new FetcherException("connection to '" + url.toString() + "' timed out", e);
		} catch(IOException e) {
			throw new FetcherException(e);
		} finally {
			try {
				if(reader != null)
					reader.close();
				else
					if(stream != null)
						stream.close();
			} catch(IOException e) {
				LOG.error("failed to close http connection stream: " + e.getMessage(), e);
			}
			
			if(connection != null)
				connection.disconnect();
		}
	}
	
	/**
	 * Indicates possible failures occurred during fetching snapshots or
	 * configuring SnapshotFetcher.
	 */
	public static class FetcherException extends Exception {
		private static final long serialVersionUID = -1062655159562016677L;

		public FetcherException(Throwable cause) {
			super(cause);
		}

		public FetcherException(String message) {
			super(message);
		}

		public FetcherException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
