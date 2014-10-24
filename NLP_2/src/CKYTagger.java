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
		HashMap<String, HashSet<String>> binaryRules = g.getBinaryRules();
		HashMap<String, HashSet<String>> wordToTags = g.getTagSets();

		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);

		String sentence;
		String[] sentenceToArr;
		int sentenceCount = 0;
		while (sentenceCount < 5 && (sentence = br.readLine()) != null) {
			sentenceToArr = sentence.split(" ");
			int n = sentenceToArr.length;
			
			// every length 'l' corresponds to a row, and every 'i' then
			// corresponds to a column. The stored hashmap element then contains
			// pi values for any tag at
			// spanning i to j = i+l
			HashMap<String, Double>[][] piTable = new HashMap[n][n];

			// Initialization step: First compute pi(i, i, X)
			String word;
			HashMap<String, Double> piValues;
			for (int i = 0; i < n; i++) {
				word = sentenceToArr[i];
				HashSet<String> tagSet;
				
				if (!wordToCount.containsKey(word)) {
					word = RARE;
				}
				tagSet = wordToTags.get(word);
				
				piValues = new HashMap<String, Double>();
				for (String tag : tagSet) {
					String rule = tag + " " + word;
					Double q = qParams.get(rule);
					piValues.put(tag, q);
				}
				piTable[0][i] = piValues;
			}

			sentenceCount++;
 			//Recurisve pi-table building
			for (int l = 1; l < n - 1; l++) {
				
				for(int i = 0; i < n-l-1; i++) {
					int j = i + l;
					piValues = new HashMap<String, Double>();
					
					//For every nonterminal
					for (String nonTerminal: binaryRules.keySet()) {
						HashSet<String> rhsSet = binaryRules.get(nonTerminal);
						double max = 0.0;
						
						for (String rhs: rhsSet) {
							
							String[] rhsToArr = rhs.split(" ");
							String yTag = rhsToArr[0];
							String zTag = rhsToArr[1];
							String rule = nonTerminal + " " + rhs;
							double q = qParams.get(rule);
							double piCurrent;
							
							for (int s = i; s < j-1; s++) {
								piCurrent = q * pi(i, s, yTag, piTable) * pi(s+1, j, zTag, piTable);
								if (piCurrent > max) {
									max = piCurrent;
								}
							}
						}
						piValues.put(nonTerminal, max);
					}
					
					piTable[l][i] = piValues;
				}
			}
		}
	}
	
	private static double pi(int i, int j, String nonTerminal, HashMap[][] piTable) {
		int l = j-i;
		HashMap<String, Double> piValues = piTable[l][i];
		if (!piValues.containsKey(nonTerminal)) {
			return 0.0;
		}
		return piValues.get(nonTerminal);
	}

}
