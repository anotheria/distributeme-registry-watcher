package org.distributeme.registrywatcher;

import java.util.LinkedList;

import org.distributeme.registrywatcher.diff_match_patch.Diff;

/**
 * The SnapshotComparator class compares two registry snapshots and generates
 * difference string in certain format.
 */
public class SnapshotComparator {
	private Comparator comparator;

	/**
	 * Creates and configures the SnapshotComparator object.
	 * @param style Style of the difference string. Possible values are
	 *        "UNIFIED" and "HTML".
	 */
	public SnapshotComparator(String style) {
		comparator = getComparator(style);
	}
	
	/**
	 * Compares two registry snapshots.
	 * 
	 * @param previous previous snapshot.
	 * @param current current snapshot.
	 * @return difference string.
	 */
	public String getDiff(Snapshot previous, Snapshot current) {
		return comparator.getDiff(previous, current);
	}
	
	/**
	 * Returns type of the file corresponding to the style of the difference
	 * string. Can be used as file name extension.
	 */
	public String getFileType() {
		return comparator.getFileType();
	}

	/**
	 * Creates suitable comparator object.
	 * @param style Style of the difference string. Possible values are
	 *        "UNIFIED" and "HTML".
	 * @return instance of the object implementing the Comparator interface.
	 */
	private static Comparator getComparator(String style) {
		if(style.equals("UNIFIED"))
			return new UnifiedComparator();
		else if(style.equals("HTML"))
			return new HtmlComparator();
		else
			throw new IllegalArgumentException("diff style '" + style + "' is not known");
	}

	/**
	 * The Comparator interface provides polymorphic access to the comparator
	 * object.
	 */
	private static interface Comparator {
		/**
		 * Compares two registry snapshots.
		 * 
		 * @param previous previous snapshot.
		 * @param current current snapshot.
		 * @return difference string.
		 */
		String getDiff(Snapshot previous, Snapshot current);

		/**
		 * Returns type of the file corresponding to the style of the difference
		 * string. Can be used as file name extension.
		 */
		String getFileType();
	}
	
	private static abstract class BasicComparator implements Comparator {
		protected diff_match_patch dmp = new diff_match_patch();
		
		protected LinkedList<Diff> diff(Snapshot previous, Snapshot current) {
			if(previous == null)
				throw new IllegalArgumentException("previous can not be null");
			if(current == null)
				throw new IllegalArgumentException("current can not be null");
			
			return dmp.diff_main(previous.getData(), current.getData());
		}		
	}
	
	/**
	 * The UnifiedComparator class generates unified diff sting comparing two
	 * registry snapshots.
	 */
	private static class UnifiedComparator extends BasicComparator {
		@Override
		public String getDiff(Snapshot previous, Snapshot current) {
			return dmp.patch_toText(dmp.patch_make(super.diff(previous, current)));
		}
		
		@Override
		public String getFileType() {
			return ".diff";
		}
	}

	/**
	 * The UnifiedComparator class generates HTML sting comparing two
	 * registry snapshots.
	 */
	private static class HtmlComparator extends BasicComparator {
		@Override
		public String getDiff(Snapshot previous, Snapshot current) {
			return "<html>" + dmp.diff_prettyHtml(super.diff(previous, current)) + "</html>";
		}
		
		@Override
		public String getFileType() {
			return ".html";
		}
	}
}
