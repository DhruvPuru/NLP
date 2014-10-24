import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author Dhruv
 *
 */
public class CKYTagger {

	public static final String RARE_COUNT_FILE = "cfg_rare.counts";
	public static final String DEV_FILE = "parse_dev.dat";
	private static final String RARE = RareCountHelpers.RARE;

	public static void main(String[] args) throws IOException {
		ckyTagger(DEV_FILE);
	}

	public static void ckyTagger(String devFile) throws IOException {

		Grammar g = new Grammar(RARE_COUNT_FILE);

		HashMap<String, Integer> wordToCount = RareCountHelpers
				.wordToCount(RARE_COUNT_FILE);
		HashMap<String, Double> qParams = g.getQParams();
		HashMap<String, HashSet<String>> rules = g.getRules();
		HashMap<String, HashSet<String>> wordToTags = g.getTagSets();

		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);

		String sentence;
		String[] sentenceToArr;
		int sentenceCount = 0;
		while (sentenceCount < 5 && (sentence = br.readLine()) != null) {
			System.out.println(sentence);
			sentenceToArr = sentence.split(" ");
			int n = sentenceToArr.length;
			// every length 'l' corresponds to a row, and every 'i' then
			// corresponds to a column. The stored hashmap element then contains
			// pi values for any tag at
			// spanning i to j = i+l
			HashMap<String, Double>[][] piTable = new HashMap[n][n];

			// Initialization step: First compute pi(i, i, X)
			String word;
			for (int i = 0; i < n; i++) {
				word = sentenceToArr[i];
				HashSet<String> tagSet;
				
				if (!wordToCount.containsKey(word)) {
					word = RARE;
				}
				tagSet = wordToTags.get(word);
				
				HashMap<String, Double> piValues = new HashMap<String, Double>();
				for (String tag : tagSet) {
					String rule = tag + " " + word;
					Double q = qParams.get(rule);
					System.out.println(rule + ": " + q);
					piValues.put(tag, q);
				}
				piTable[0][i] = piValues;
			}

			sentenceCount++;
			System.out.println("--------------------------------------");
//			//Recurisve pi-table building
//			for (int l = 1; l < n - 1; l++) {
//
//			}
		}
	}

}
