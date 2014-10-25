import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
		
		File rareCounts = new File("initPiValues.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		// for (String nT: binaryRules.keySet()) {
		// for (String rhs: binaryRules.get(nT)) {
		// System.out.println(nT + " --> " + rhs);
		// }
		// }

		String sentence;
		String[] sentenceToArr;
		int sentenceCount = 0;
		while (sentenceCount < 1 && (sentence = br.readLine()) != null) {
			sentenceToArr = sentence.split(" ");
			int n = sentenceToArr.length;

			// every length 'l' corresponds to a row, and every 'i' then
			// corresponds to a column. The stored hashmap element then contains
			// pi values for any tag spanning i to j = i+l
			HashMap<String, BackPointer>[][] piTable = new HashMap[n][n];

			// Initialization step: First compute pi(i, i, X)
			String word;
			HashMap<String, BackPointer> piValues;
			for (int i = 0; i < n; i++) {
				word = sentenceToArr[i];
				HashSet<String> tagSet;

				if (!wordToCount.containsKey(word)) {
					word = RARE;
				}
				tagSet = wordToTags.get(word);

				piValues = new HashMap<String, BackPointer>();
				for (String tag : tagSet) {
					String rule = tag + " " + word;
					Double q = qParams.get(rule);
					BackPointer b = new BackPointer(q, i, word);
					piValues.put(tag, b);
					bufferedWriter.write(word + ", " + tag + " :" + b + "\n");
				}
				piTable[0][i] = piValues;
			}
			bufferedWriter.close();
			
			// Recursive pi-table building
//			for (int l = 1; l < n - 1; l++) {

			for (int l = 1; l < 2; l++) {
				for (int i = 0; i < n - l - 1; i++) {
					int j = i + l;
					piValues = new HashMap<String, BackPointer>();

					// For every nonterminal
					for (String nonTerminal : binaryRules.keySet()) {
						HashSet<String> rhsSet = binaryRules.get(nonTerminal);
						double maxQ = 0.0;
						int splitPoint = i;
						String bpRhs = "";

						for (String rhs : rhsSet) {
							
							String[] rhsToArr = rhs.split(" ");
							String yTag = rhsToArr[0];
							String zTag = rhsToArr[1];
//							System.out.println(i + ": " + yTag + " " + zTag);
							String rule = nonTerminal + " " + rhs;
							double q = qParams.get(rule);
							// System.out.println(q);
							double piCurrent;

							// For every possible split point
							for (int s = i; s <= j - 1; s++) {
								piCurrent = q * pi(i, s, yTag, piTable)
										* pi(s + 1, j, zTag, piTable);
								
								if (pi(i, s, yTag, piTable) > 0.0
										&& pi(s + 1, j, zTag, piTable) > 0.0)
									System.out
											.println("We should have a non-zero pi");
								
								// System.out.println(piCurrent);
								
								if (piCurrent > maxQ) {
									maxQ = piCurrent;
									bpRhs = rhs;
									splitPoint = s;
								}
							}
						}

						if (maxQ > 0)
							System.out.println("q: " + maxQ + "split: "
									+ splitPoint + "rhs: " + bpRhs);
						BackPointer b = new BackPointer(maxQ, splitPoint, bpRhs);
						piValues.put(nonTerminal, b);
					}
					piTable[l][i] = piValues;
				}
			}
			sentenceCount++;
		}
	}

	private static double pi(int i, int j, String nonTerminal,
			HashMap<String, BackPointer>[][] piTable) {
		int l = j - i;
		HashMap<String, BackPointer> piValues = piTable[l][i];
		if (!piValues.containsKey(nonTerminal)) {
			return 0.0;
		}
		// System.out.println(piValues.get(nonTerminal).q);
		return piValues.get(nonTerminal).q;
	}

}
