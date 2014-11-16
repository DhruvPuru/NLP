import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Dhruv
 *
 */

public class IBM1 {

	protected String eFile;
	protected String fFile;
	protected HashMap<String, HashMap<String, Double>> tParams;

	public IBM1(String englishFile, String foreignFile) {
		eFile = englishFile;
		fFile = foreignFile;
		tParams = new HashMap<String, HashMap<String, Double>>();
	}

	public HashMap<String, HashMap<String, Double>> tParams() {
		return tParams;
	}

	public void computeUniformTParams() throws IOException {

		FileReader inE = new FileReader(eFile);
		BufferedReader brE = new BufferedReader(inE);

		FileReader inF = new FileReader(fFile);
		BufferedReader brF = new BufferedReader(inF);

		String inputE;
		String inputF;

		while ((inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {

			inputE = "NULL " + inputE;
			String[] eArr = inputE.split(" ");
			String[] fArr = inputF.split(" ");

			// First create a hashmap that keeps track of all possible
			// translations of a given english word. Later fill in the uniform
			// probabilities for each french translation.
			for (String e : eArr) {
				HashMap<String, Double> fToProb;
				if (tParams.containsKey(e)) {
					fToProb = tParams.get(e);
				} else {
					fToProb = new HashMap<String, Double>();
					tParams.put(e, fToProb);
				}

				for (String f : fArr) {
					fToProb.put(f, 0.0);
				}
			}
		}

		brE.close();
		brF.close();

		// Now iterate through the map and assign probabilities t(f|e) for all
		// e-->f combinations seen in the corpus

		for (String e : tParams.keySet()) {
			HashMap<String, Double> fToProb = tParams.get(e);
			int n_e = fToProb.size();
			double t = 1 / ((double) n_e);
			for (String f : fToProb.keySet()) {
				// Replace t params with new probabilities
				fToProb.put(f, t);
			}
		}
	}

	public void printT() {
		int count = 0;
		for (String e : tParams.keySet()) {
			System.out.println(e);
			HashMap<String, Double> h = tParams.get(e);
			for (String f : h.keySet()) {
				System.out.println(e + "-->" + f + ": " + h.get(f));
			}
			count++;
			if (count > 15)
				return;
		}
	}

	public void EM1(int s) throws IOException {
		EM.ibm1EmParams(s, eFile, fFile, tParams);
	}

	public void printBestTranslations(String devFile, int n) throws IOException {
		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);

		String input;
		while ((input = br.readLine()) != null) {
			HashMap<String, Double> fToProb = tParams.get(input);
			TreeMap<Double, String> orderedT = new TreeMap<Double, String>();

			for (String f : fToProb.keySet()) {
				orderedT.put(fToProb.get(f), f);
			}
			System.out.println("Best translations for: " + input);
			
			for (int i = 0; i < n; i++) {
				Entry<Double, String> topEntry = orderedT.pollLastEntry();
				System.out.println(topEntry.getKey() + ": " + topEntry.getValue());
			}
			System.out.println("------------");
		}
		br.close();
	}

	public void printAlignments(int n) throws IOException {
		FileReader inE = new FileReader(eFile);
		BufferedReader brE = new BufferedReader(inE);

		FileReader inF = new FileReader(fFile);
		BufferedReader brF = new BufferedReader(inF);
		
		String inputE;
		String inputF;
		int numLines = 0;
		
		while (numLines < n && (inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {
			
			inputE = "NULL " + inputE;
			String[] eArr = inputE.split(" ");
			String[] fArr = inputF.split(" ");
			ArrayList<Integer> alignments = new ArrayList<Integer>();
			
			for (String f: fArr) {
				int a_i = 0;
				double max = 0.0;
				for (int j = 0; j < eArr.length; j++) {
					String e = eArr[j];
					double t = tParams.get(e).get(f);
					if (t > max) {
						a_i = j;
						max = t;
					}
				}
				alignments.add(a_i);
			}
			
			System.out.println(inputE);
			System.out.println(inputF);
			System.out.println(alignments);
			System.out.println("----------------" + "\n");
			numLines++;
		}

	}
	
	public static void main(String[] args) throws IOException {
		IBM1 ibm1 = new IBM1("corpus.en", "corpus.de");
		ibm1.computeUniformTParams();
		ibm1.EM1(5);
		ibm1.printBestTranslations("devwords.txt", 10);
		ibm1.printAlignments(20);
	}

}
