import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Dhruv
 *
 */

public class IBM1 {

	private String eFile;
	private String fFile;
	private HashMap<String, HashMap<String, Double>> tParams;

	public IBM1(String englishFile, String foreignFile) {
		eFile = englishFile;
		fFile = foreignFile;
		tParams = new HashMap<String, HashMap<String, Double>>();
	}

	public HashMap<String, HashMap<String, Double>> tParams() {
		return tParams;
	}
	
	public void eCounts() throws IOException {

		FileReader inE = new FileReader(eFile);
		BufferedReader brE = new BufferedReader(inE);

		FileReader inF = new FileReader(fFile);
		BufferedReader brF = new BufferedReader(inF);

		String inputE;
		String inputF;
		int count = 0;

		while ((inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {

			inputE = "NULL" + inputE;
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
			count++;
		}

		brE.close();
		brF.close();

		// Now iterate through the map and assign probabilities t(f|e) for all
		// e-->f combinations seen in the corpus
		
		for (String e: tParams.keySet()) {
			HashMap<String, Double> fToProb = tParams.get(e);
			int n_e = fToProb.size(); 
			double t = 1/((double)n_e);
			for (String f: fToProb.keySet()) {
				//Replace t params with new probabilities
				fToProb.put(f, t);
			}
		}
	}
	
	public void printT() {
		int count = 0;
		for (String e: tParams.keySet()) {
			System.out.println(e);
			HashMap<String, Double> h = tParams.get(e); 
			for (String f: h.keySet()) {
				System.out.println(e + "-->" + f + ": " + h.get(f));
			}
			count++;
			if (count > 15)
				return;
		}
	}
	
	public void EM(int s) throws IOException {
		tParams = EM1.emParams(s, eFile, fFile, tParams);
	}

	public static void main(String[] args) throws IOException {
		IBM1 ibm1 = new IBM1("corpus.en", "corpus.de");
		ibm1.eCounts();
		long start = System.currentTimeMillis();
		ibm1.EM(5);
		long end = System.currentTimeMillis();
		long time = (end - start) % 1000;
		System.out.println(time);
	}

}
