import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;


public class Unscramble {

	public static final String UNSCRAMBLED_FILE = "unscrambled.en";
	
	public Unscramble(String scrambledE, String german, IBM2 ibm2) throws IOException, ClassNotFoundException {
		
		FileReader sE = new FileReader(scrambledE);
		BufferedReader brE = new BufferedReader(sE);

		FileReader inF = new FileReader(german);
		BufferedReader brF = new BufferedReader(inF);
		
		File eUnscrambled = new File(UNSCRAMBLED_FILE);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				eUnscrambled));
		
		LinkedList<String> eSentences= new LinkedList<String>();
		LinkedList<String> fSentences = new LinkedList<String>();
		
		String inputE;
		String inputF;
		
		//Load q and t params
		ibm2.deserializeQ(IBM2.Q_PARAMS);
		ibm2.deserializeT(IBM2.T_PARAMS);
		
		//Load sentences into Linked list
		while ((inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {
			eSentences.add(inputE);
			fSentences.add(inputF);
		}
		
		brE.close();
		brF.close();
		
		//For every foreign sentence, find the best english match
		for (String f: fSentences) {

			String[] fArr = f.split(" ");
			int m = fArr.length;
			String eMatch;
			double maxProb = 0;

			for (String e : eSentences) {
				double logProb = 0;
				String eng = "NULL" + e;
				String[] eArr = eng.split(" ");
				int l = eArr.length;
				ArrayList<Integer> alignments = ibm2.findAlignments(fArr, eArr);
				
				for (int i = 0; i < fArr.length; i++) {
					String f_i = fArr[i];
					int j = alignments.get(i);
					
					double t = ibm2.tParams.get(e).get(f);
					double q = ibm2.qParams.get(l + " " + m).get(j + " " + i);
					double logParam = Math.log(q*t);
				}
				
			}
		}
		
	}
	
	public static void main (String[] args) {
		IBM2 ibm2 = new IBM2("corpus.en", "corpus.de");
	}
}
