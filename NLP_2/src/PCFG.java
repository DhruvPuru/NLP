import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;

public class PCFG {

	public static void main(String[] args) throws IOException, JSONException {
		HashMap<String, Integer> wordToCount = RareCountHelpers
				.wordToCount("cfg.counts");
		RareCountHelpers.rareCounter("parse_train.dat", wordToCount);
	}
}
