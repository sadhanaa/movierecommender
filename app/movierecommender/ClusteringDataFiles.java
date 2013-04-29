package movierecommender;

import java.io.File;

import play.Play;

public class ClusteringDataFiles {
	private static final String CLUSTERED_FILE_NAME_PREFIX = "datasetscluster";
	private static final String CLUSTERED_FILE_NAME_SUFFIX = ".csv";
	
	private static enum AgeRange {
		FROM_13_TO_17(13, 17),
		FROM_18_TO_27(18, 27),
		FROM_28_TO_38(28, 38),
		FROM_39_TO_55(39, 55),
		FROM_56_TO_75(56, 75);
		
		private int min;
		private int max;
		
		private AgeRange(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public static AgeRange getRange(int age) {
			for(AgeRange r : AgeRange.values()) {
				if(age >= r.min && age <= r.max) {
					return r;
				}
			}
			return null;
		}

		public int getMin() {
			return min;
		}

		public int getMax() {
			return max;
		}
	}

	public static File getFile(String gender, int age) {
		AgeRange r = AgeRange.getRange(age);
		return Play.application().getFile("/datafiles/" +
				CLUSTERED_FILE_NAME_PREFIX + 
				gender.toUpperCase() + 
				r.getMin() + 
				r.getMax() + 
				CLUSTERED_FILE_NAME_SUFFIX);
	}
}
