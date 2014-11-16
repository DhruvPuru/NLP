import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author Dhruv
 *
 */
public class IBM2 extends IBM1 {

	protected HashMap<String, HashMap<String, Double>> qParams;
	public static final String T_PARAMS = "ibm2_T";
	public static final String Q_PARAMS = "ibm2_Q";

	public IBM2(String englishFile, String foreignFile) {
		super(englishFile, foreignFile);
		qParams = new HashMap<String, HashMap<String, Double>>();
	}

	public void computeUniformQParams() throws IOException {
		FileReader inE = new FileReader(eFile);
		BufferedReader brE = new BufferedReader(inE);

		FileReader inF = new FileReader(fFile);
		BufferedReader brF = new BufferedReader(inF);

		String inputE;
		String inputF;
 
		while ((inputF = brF.readLine()) != null
				&& (inputE = brE.readLine()) != null) {

			String[] eArr = inputE.split(" ");
			String[] fArr = inputF.split(" ");

			int l = eArr.length;
			int m = fArr.length;
			String key = l + " " + m;

			if (!qParams.containsKey(key)) {
				HashMap<String, Double> q_lm = new HashMap<String, Double>();
				qParams.put(key, q_lm);

				for (int i = 0; i < m; i++) {
					double q = 1.0 / ((double) (l + 1));
					for (int j = 0; j <= l; j++) {
						String i_jKey = j + " " + i;
						q_lm.put(i_jKey, q);
					}
				}
			}
		}

		brE.close();
		brF.close();
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
			int l = eArr.length - 1;
			int m = fArr.length;
			ArrayList<Integer> alignments = findAlignments(fArr, eArr);

			System.out.println(inputE);
			System.out.println(inputF);
			System.out.println(alignments);
			System.out.println("----------------" + "\n");
			numLines++;
		}
	}
	
	public ArrayList<Integer> findAlignments(String[] fArr, String[] eArr) {
		int l = eArr.length - 1;
		int m = fArr.length;
		ArrayList<Integer> alignments = new ArrayList<Integer>();

		for (int i = 0; i < fArr.length; i++) {
			String f = fArr[i];
			int a_i = 0;
			double max = 0.0;
			for (int j = 0; j < eArr.length; j++) {
				String e = eArr[j];
				double arg = qParams.get(l + " " + m).get(j + " " + i)
						* tParams.get(e).get(f);
				if (arg > max) {
					a_i = j;
					max = arg;
				}
			}
			alignments.add(a_i);
		}
		return alignments;
	}

	public void serializeQ(String serializedQParams) throws IOException {
		FileOutputStream fOut = new FileOutputStream(new File(serializedQParams));
		ObjectOutputStream out = new ObjectOutputStream(fOut);
		out.writeObject(qParams);
		out.close();
		fOut.close();
	}

	public void serializeT(String serializedTParams) throws IOException {
		FileOutputStream fOut = new FileOutputStream(new File(serializedTParams));
		ObjectOutputStream out = new ObjectOutputStream(fOut);
		out.writeObject(tParams);
		out.close();
		fOut.close();
	}

	public void deserializeQ(String serializedQParams) throws IOException,
			ClassNotFoundException {

		FileInputStream fIn = new FileInputStream(new File(serializedQParams));
		ObjectInputStream in = new ObjectInputStream(fIn);
		qParams = (HashMap<String, HashMap<String, Double>>) in.readObject();
		in.close();
		fIn.close();
	}

	public void deserializeT(String serializedTParams) throws IOException,
			ClassNotFoundException {

		FileInputStream fIn = new FileInputStream(new File(serializedTParams));
		ObjectInputStream in = new ObjectInputStream(fIn);
		tParams = (HashMap<String, HashMap<String, Double>>) in.readObject();
		in.close();
		fIn.close();
	}

	private void EM2(int s) throws IOException {
		EM.ibm2EmParams(s, eFile, fFile, tParams, qParams);
	}

	private void printQs() {
		int c = 0;
		for (String s : qParams.keySet()) {
			System.out.println("For (l, m) = : " + s);
			for (String t : qParams.get(s).keySet()) {
				System.out.println("(j, i)= " + t + " :"
						+ qParams.get(s).get(t).doubleValue());
			}
			c++;
			if (c > 10)
				break;
		}
	}

	public static void main(String[] args) throws IOException {
		IBM2 ibm2 = new IBM2("corpus.en", "corpus.de");
		ibm2.computeUniformTParams();
		ibm2.EM1(5);
		ibm2.computeUniformQParams();
		ibm2.EM2(5);
		ibm2.serializeT(IBM2.T_PARAMS);
		ibm2.serializeQ(IBM2.Q_PARAMS);
		ibm2.printAlignments(20);
	}
}
