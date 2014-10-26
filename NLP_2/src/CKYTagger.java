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
	private static final String PREDICTION_FILE = "prediction_file.dat";
	
	private static final String VERT_COUNT_FILE = "cfg_vert_rare.counts";
	private static final String PREDICTION_VERT = "prediction_file_vert.dat";

	public static void main(String[] args) throws IOException {
		ckyTagger(DEV_FILE, PREDICTION_FILE, RARE_COUNT_FILE);
		
		ckyTagger(DEV_FILE, PREDICTION_VERT, VERT_COUNT_FILE);
	}

	public static void ckyTagger(String devFile, String predictionFile, String countFile) throws IOException {

		Grammar g = new Grammar(countFile);

		HashMap<String, Integer> wordToCount = RareCountHelpers
				.wordToCount(countFile);
		HashMap<String, Double> qParams = g.getQParams();
		HashMap<String, HashSet<String>> binaryRules = g.getBinaryRules();
		HashMap<String, HashSet<String>> wordToTags = g.getTagSets();

		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);

		File rareCounts = new File(predictionFile);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		String sentence;
		String[] sentenceToArr;
		while ((sentence = br.readLine()) != null) {
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

				String countWord = word;
				if (!wordToCount.containsKey(word)) {
					countWord = RARE;
				}
				tagSet = wordToTags.get(countWord);

				piValues = new HashMap<String, BackPointer>();
				for (String tag : tagSet) {
					String rule = tag + " " + countWord;
					Double q = qParams.get(rule);
					BackPointer b = new BackPointer(q, i, word);
					piValues.put(tag, b);
					// bufferedWriter.write(word + ", " + tag + " :" + b +
					// "\n");
				}
				piTable[0][i] = piValues;
			}
			// bufferedWriter.close();

			// Recursive pi-table building
			// for (int l = 1; l < n - 1; l++) {

			for (int l = 1; l < n; l++) {
				for (int i = 0; i < n - l; i++) {
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
							// System.out.println(i + ": " + yTag + " " + zTag);
							String rule = nonTerminal + " " + rhs;
							double q = qParams.get(rule);
							// System.out.println(q);
							double piCurrent;

							// For every possible split point
							for (int s = i; s <= j - 1; s++) {
								piCurrent = q * pi(i, s, yTag, piTable)
										* pi(s + 1, j, zTag, piTable);
								
								if (piCurrent > maxQ) {
									maxQ = piCurrent;
									bpRhs = rhs;
									splitPoint = s;
								}
							}
						}
						
						BackPointer b = new BackPointer(maxQ, splitPoint, bpRhs);
						piValues.put(nonTerminal, b);
					}
					piTable[l][i] = piValues;
				}
			}

			// Using pi values and backpointers, reconstruct the sentence
			HashMap<String, BackPointer> spiValues = piTable[n - 1][0];
			BackPointer bp = null;
			String rootVal = "";
			if (pi(0, n - 1, "S", piTable) > 0) {
				bp = spiValues.get("S");
				rootVal = "S";
			} else {
				double max = 0.0;
				for (String nT : spiValues.keySet()) {
					if (spiValues.get(nT).pi > max) {
						max = spiValues.get(nT).pi;
						bp = spiValues.get(nT);
						rootVal = nT;
					}
				}
			}

			GTreeNode root = new GTreeNode(rootVal);

			//If S --> NP VP
			String rhs = bp.rhs;
			String[] rhsToArr = rhs.split(" ");
			String rhs1 = rhsToArr[0]; //NP
			String rhs2 = rhsToArr[1]; //VP

			int s = bp.s;
			int i1 = 0;
			int j1 = s;
			
			int i2 = s+1;
			int j2 = n-1;

			root.left = constructGTree(rhs1, i1, j1, piTable);
			root.right = constructGTree(rhs2, i2, j2, piTable);

			String jsonFormat = RareCountHelpers.deconstructGTree(root, false);
			bufferedWriter.write(jsonFormat + "\n");	
		}
		bufferedWriter.close();
	}

	private static double pi(int i, int j, String nonTerminal,
			HashMap<String, BackPointer>[][] piTable) {
		int l = j - i;
		HashMap<String, BackPointer> piValues = piTable[l][i];
		if (!piValues.containsKey(nonTerminal)) {
			return 0.0;
		}
		// System.out.println(piValues.get(nonTerminal).q);
		return piValues.get(nonTerminal).pi;
	}

	private static GTreeNode constructGTree(String rootVal, int i, int j,
			HashMap<String, BackPointer>[][] piTable) {
		
		HashMap<String, BackPointer> piVals = piTable[j-i][i];
		GTreeNode root = new GTreeNode(rootVal);
		BackPointer bp = piVals.get(rootVal);
		
		String rhs = bp.rhs;
		
		if (rhs.contains(" ")) {
			String[] rhsToArr = rhs.split(" ");
			String rhs1 = rhsToArr[0];
			String rhs2 = rhsToArr[1];

			int s = bp.s;
			
			int i1 = i;
			int j1 = s;
			
			int i2 = s+1;
			int j2 = j;

			root.left = constructGTree(rhs1, i1, j1, piTable);
			root.right = constructGTree(rhs2, i2, j2, piTable);	
		}
		else {
			root.left = new GTreeNode(rhs);
		}
		return root;
	}
}
