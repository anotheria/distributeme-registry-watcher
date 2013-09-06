package org.distributeme.registrywatcher;

/**
 * The Snapshot class keeps XML data snapshot retrieved from the
 * DistributeMe registry.
 */
public class Snapshot {
	private long timestamp;
	private String data;
	
	/**
	 * Constructs snapshot from the data stored persistently.
	 * 
	 * @param data XML data read from the persistent storage. Can not be null.
	 * @param timestamp in milliseconds when the snapshot was fetched.
	 */
	public Snapshot(String data, long timestamp) {
		if(data == null)
			throw new IllegalArgumentException("data can not be null");
		
		this.data = data;
		this.timestamp = timestamp;
	}
	
	/**
	 * Constructs snapshot from the newly fetched data.
	 * Timestamps the snapshot with the current system time.
	 * 
	 * @param data XML data fetched from the registry. Can not be null.
	 */
	public Snapshot(String data) {
		this(data, System.currentTimeMillis());
	}

	/**
	 * Returns timestamp of the snapshot.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Returns XML data of the snapshot.
	 */
	public String getData() {
		return data;
	}

	@Override
	public int hashCode() {
		return (data == null) ? 0 : data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || !(obj instanceof Snapshot))
			return false;

		Snapshot other = (Snapshot)obj;
		
		if(data == null) {
			if(other.data != null)
				return false;
		} else
			if(!data.equals(other.data))
				return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Snapshot [data=");
		builder.append(data);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		
		return builder.toString();
	}
}
