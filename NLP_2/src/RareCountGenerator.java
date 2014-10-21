import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;

/**
 * 
 * @author Dhruv
 *
 */
public class RareCountGenerator {

	public static void main(String[] args) throws IOException, JSONException {
		
		//Run part 1
		HashMap<String, Integer> wordToCount = RareCountHelpers
				.wordToCount("cfg.counts");
		RareCountHelpers.rareCounter("parse_train.dat", wordToCount);
	}
}
