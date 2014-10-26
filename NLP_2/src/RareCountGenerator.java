import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;

/**
 * This class generates a new training file that accounts for rare counts, making use of the helper methods
 * in RareCountHelpers.java
 * @author Dhruv
 *
 */
public class RareCountGenerator {

	public static void main(String[] args) throws IOException, JSONException {
		
//		HashMap<String, Integer> wordToCount = RareCountHelpers
//				.wordToCount("cfg.counts");
//		RareCountHelpers.rareCounter("parse_train.dat", "parse_train_rare.dat", wordToCount);
		
		//Run part 4
		HashMap<String, Integer> wordToCount = RareCountHelpers
				.wordToCount("cfg_vert.counts");
		RareCountHelpers.rareCounter("parse_train_vert.dat", "parse_train_vert_rare.dat", wordToCount);
	}
}
