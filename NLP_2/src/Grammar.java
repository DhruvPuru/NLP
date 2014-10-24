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
public class Grammar {

	private HashMap<String, Double> qParams;
	private HashMap<String, HashSet<String>> rules;
	private HashMap<String, HashSet<String>> wordToTags;
	private String countFile;

	public static final String NON_TERMINAL = "NONTERMINAL";
	public static final String BINARY_RULE = "BINARYRULE";
	public static final String UNARY_RULE = "UNARYRULE";

	public Grammar(String countFile) throws IOException {
		qParams = new HashMap<String, Double>();
		rules = new HashMap<String, HashSet<String>>();
		wordToTags = new HashMap<String, HashSet<String>>();
		this.countFile = countFile;
		this.computeQParamsAndRules();
	}

	private void computeQParamsAndRules() throws IOException {

		FileReader in = new FileReader(countFile);
		BufferedReader br = new BufferedReader(in);
		HashMap<String, Integer> nTCounts = new HashMap<String, Integer>();
		

		String input;
		String[] inputToArray;

		// Make one pass of file and compute counts
		while ((input = br.readLine()) != null) {
			inputToArray = input.split(" ");
			if (!inputToArray[1].equals(NON_TERMINAL))
				break;
			String nonTerm = inputToArray[2];
			int count = Integer.parseInt(inputToArray[0]);
			nTCounts.put(nonTerm, count);
		}

		in = new FileReader(countFile);
		br = new BufferedReader(in);

		// Make second pass to compute maximum likelihood parameters
		while ((input = br.readLine()) != null) {
			inputToArray = input.split(" ");
			String countType = inputToArray[1];
			
			if (!countType.equals(NON_TERMINAL)) {
				int ruleCount = Integer.parseInt(inputToArray[0]);
				String rule = "";
				String nonTerminal = inputToArray[2];

				for(int i = 2; i < inputToArray.length; i++) {
					rule += inputToArray[i];
					if(i < inputToArray.length - 1) {
						rule += " ";
					}
				}
				int nTCount = nTCounts.get(nonTerminal);
				double qValue = ((double)ruleCount)/((double)nTCount);
				qParams.put(rule, qValue);
				
				//update set of rules 
				String rhs = "";
				for(int i = 3; i < inputToArray.length; i++) {
					rhs += inputToArray[i];
					if(i < inputToArray.length - 1) {
						rhs += " ";
					}
				}
				
//				System.out.println(nonTerminal + "-->" + rhs);
				HashSet<String> rhsSet; 
				if (rules.containsKey(nonTerminal)) {
					rhsSet = rules.get(nonTerminal);
					rhsSet.add(rhs);
				}
				else {
					rhsSet = new HashSet<String>();
					rhsSet.add(rhs);
					rules.put(nonTerminal, rhsSet);
				}
				
				//Create a set of possible tags for every word in the corpus
				if (countType.equals(UNARY_RULE)) {
					String word = inputToArray[3];
					if(word.equals("."))
						System.out.println(nonTerminal);
					HashSet<String> tagSet; 
					if (wordToTags.containsKey(word)) {
						if(word.equals(".")) {
							System.out.println("wordToTags contains .");
							System.out.println(nonTerminal);
						}
						tagSet = wordToTags.get(word);
						tagSet.add(nonTerminal);
					}
					else {
						if(word.equals(".")) {
							System.out.println("wordToTags does NOT contain .");
							System.out.println(nonTerminal);
						}
						tagSet = new HashSet<String>();
						tagSet.add(nonTerminal);
						wordToTags.put(word, tagSet);
					}
				}
			}
		}
	}

	public HashMap<String, HashSet<String>> getRules() {
		return rules;
	}
	
	public HashMap<String, Double> getQParams() {
		return qParams;
	}
	
	/**
	 * 
	 * @return set of possible tags for every word in the corpus
	 */
	public HashMap<String, HashSet<String>> getTagSets() {
		return wordToTags;
	}
}
