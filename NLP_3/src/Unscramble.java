import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class Unscramble {

	public static final String UNSCRAMBLED_FILE = "unscrambled.en";
	public static final int NEG_CONSTANT = Short.MIN_VALUE;

	public static void unscramble(String scrambledE, String german, IBM2 ibm2)
			throws IOException, ClassNotFoundException {

		FileReader sE = new FileReader(scrambledE);
		BufferedReader brE = new BufferedReader(sE);

		FileReader inF = new FileReader(german);
		BufferedReader brF = new BufferedReader(inF);

		File eUnscrambled = new File(UNSCRAMBLED_FILE);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				eUnscrambled));

		HashSet<String> eSentences = new HashSet<String>();
		LinkedList<String> fSentences = new LinkedList<String>();

		String inputE;
		String inputF;

		// Load q and t params
		ibm2.deserializeQ(IBM2.Q_PARAMS);
		ibm2.deserializeT(IBM2.T_PARAMS);

		// Load sentences into Linked list
		while ((inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {
			eSentences.add(inputE);
			fSentences.add(inputF);
		}

		brE.close();
		brF.close();

		// For every foreign sentence, find the best english match
		for (String f : fSentences) {

			String[] fArr = f.split(" ");
			int m = fArr.length;
			String eMatch = "";
			double maxLogProb = Integer.MIN_VALUE;

			for (String e : eSentences) {
				double logProb = 0;
				String eng = "NULL " + e;
				String[] eArr = eng.split(" ");
				int l = eArr.length-1;

				for (int i = 0; i < fArr.length; i++) {
					String f_i = fArr[i];
					double maxP = 0;

					for (int j = 0; j < eArr.length; j++) {
						double t;
						double q;
						double param;
						String e_j = eArr[j];

						try {
							t = ibm2.tParams.get(e_j).get(f_i);
							q = ibm2.qParams.get(l + " " + m).get(j + " " + i);
							param = q * t;
						} catch (NullPointerException npe) {
							t = 0;
							q = 0;
							param = 0;
						}
						
						if (param > maxP) {
							maxP = param;
						}
					}
					
					if (maxP == 0) {
						logProb += NEG_CONSTANT;
					}
					else {
						logProb += Math.log(maxP);
					}
				}

				if (logProb > maxLogProb) {
					maxLogProb = logProb;
					eMatch = e;
				}
			}
			bufferedWriter.write(eMatch + "\n");
		}
		bufferedWriter.close();
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		IBM2 ibm2 = new IBM2("corpus.en", "corpus.de");
		unscramble("scrambled.en", "original.de", ibm2);
	}
}
