import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author Dhruv
 *
 */
public class Grammar {

	private HashMap<String, Double> qParams;
	private HashMap<String, String> rules;
	private String countFile;

	public static final String NON_TERMINAL = "NONTERMINAL";
	public static final String BINARY_RULE = "BINARYRULE";
	public static final String UNARY_RULE = "UNARYRULE";

	public Grammar(String countFile) {
		qParams = new HashMap<String, Double>();
		rules = new HashMap<String, String>();
		this.countFile = countFile;
	}

	private void computeQParams() throws IOException {

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
				double qValue = ruleCount/nTCount;
				qParams.put(rule, qValue);
				System.out.println(rule);
			}
		}
	}

	public HashMap<String, Double> getQParams() throws IOException {
		this.computeQParams();
		return qParams;
	}
}
