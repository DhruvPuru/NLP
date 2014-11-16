import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EM {

	public static void ibm1EmParams(int s, String eFile, String fFile,
			HashMap<String, HashMap<String, Double>> tParams)
			throws IOException {

		HashMap<String, Double> efCounts = new HashMap<String, Double>();
		HashMap<String, Double> eCounts = new HashMap<String, Double>();

		for (int i = 0; i < s; i++) {

			// Set all counts to 0
			for (String ef : efCounts.keySet()) {
				efCounts.put(ef, 0.0);
			}

			for (String e : eCounts.keySet()) {
				eCounts.put(e, 0.0);
			}

			System.out.println("Iteration: " + i);

			FileReader inE = new FileReader(eFile);
			BufferedReader brE = new BufferedReader(inE);

			FileReader inF = new FileReader(fFile);
			BufferedReader brF = new BufferedReader(inF);

			String inputF;
			String inputE;

			while ((inputF = brF.readLine()) != null
					&& (inputE = brE.readLine()) != null) {

				inputE = "NULL " + inputE;
				String[] eArr = inputE.split(" ");
				String[] fArr = inputF.split(" ");

				for (String f : fArr) {

					// Compute denominator for deltas
					double deltaDenom = 0;
					for (String e : eArr) {
						deltaDenom += tParams.get(e).get(f);
					}

					for (String e : eArr) {
						double deltaNum = tParams.get(e).get(f);
						double delta = deltaNum / deltaDenom;
						String ef = e + " " + f;
						double newCount;

						if (efCounts.containsKey(ef)) {
							newCount = efCounts.get(ef) + delta;
						} else {
							newCount = delta;
						}
						efCounts.put(ef, newCount);

						if (eCounts.containsKey(e)) {
							newCount = eCounts.get(e) + delta;
						} else {
							newCount = delta;
						}
						eCounts.put(e, newCount);
					}
				}
			}

			brF.close();
			brE.close();

			// Set tParams for this iteration
			for (String e : tParams.keySet()) {
				HashMap<String, Double> fToProb = tParams.get(e);
				for (String f : fToProb.keySet()) {
					double newT = efCounts.get(e + " " + f) / eCounts.get(e);
					fToProb.put(f, newT);
				}
			}
		}
	}

	public static void ibm2EmParams(int s, String eFile, String fFile,
			HashMap<String, HashMap<String, Double>> tParams,
			HashMap<String, HashMap<String, Double>> qParams)
			throws IOException {

		HashMap<String, Double> efCounts = new HashMap<String, Double>();
		HashMap<String, Double> eCounts = new HashMap<String, Double>();

		HashMap<String, Double> jilmCounts = new HashMap<String, Double>();
		HashMap<String, Double> ilmCounts = new HashMap<String, Double>();

		for (int n = 0; n < s; n++) {

			// Set all counts to 0
			for (String ef : efCounts.keySet()) {
				efCounts.put(ef, 0.0);
			}

			for (String e : eCounts.keySet()) {
				eCounts.put(e, 0.0);
			}

			for (String jilm : jilmCounts.keySet()) {
				jilmCounts.put(jilm, 0.0);
			}

			for (String ilm : ilmCounts.keySet()) {
				ilmCounts.put(ilm, 0.0);
			}

			System.out.println("Iteration: " + n);

			FileReader inE = new FileReader(eFile);
			BufferedReader brE = new BufferedReader(inE);

			FileReader inF = new FileReader(fFile);
			BufferedReader brF = new BufferedReader(inF);

			String inputF;
			String inputE;

			while ((inputF = brF.readLine()) != null
					&& (inputE = brE.readLine()) != null) {

				inputE = "NULL " + inputE;
				String[] eArr = inputE.split(" ");
				String[] fArr = inputF.split(" ");
				int l = eArr.length-1;
				int m = fArr.length;

				for (int i = 0; i < fArr.length; i++) {

					String f = fArr[i];
					// Compute denominator for deltas
					double deltaDenom = 0;
					for (int j = 0; j < eArr.length; j++) {
						String e = eArr[j];
						deltaDenom += qParams.get(l + " " + m).get(j + " " + i)
								.doubleValue()
								* tParams.get(e).get(f);
					}

					for (int j = 0; j < eArr.length; j++) {
						String e = eArr[j];
						double deltaNum = qParams.get(l + " " + m)
								.get(j + " " + i).doubleValue()
								* tParams.get(e).get(f);
						double delta = deltaNum / deltaDenom;
						String ef = e + " " + f;
						double newCount;

						if (efCounts.containsKey(ef)) {
							newCount = efCounts.get(ef) + delta;
						} else {
							newCount = delta;
						}
						efCounts.put(ef, newCount);

						if (eCounts.containsKey(e)) {
							newCount = eCounts.get(e) + delta;
						} else {
							newCount = delta;
						}
						eCounts.put(e, newCount);

						String ilm = i + " " + l + " " + m;
						String jilm = j + " " + ilm;

						if (jilmCounts.containsKey(jilm)) {
							newCount = jilmCounts.get(jilm) + delta;
						} else {
							newCount = delta;
						}
						jilmCounts.put(jilm, newCount);

						if (ilmCounts.containsKey(ilm)) {
							newCount = ilmCounts.get(ilm) + delta;
						} else {
							newCount = delta;
						}
						ilmCounts.put(ilm, newCount);
					}
				}
			}

			brF.close();
			brE.close();

			// Set tParams for this iteration
			for (String e : tParams.keySet()) {
				HashMap<String, Double> fToProb = tParams.get(e);
				for (String f : fToProb.keySet()) {
					double newT = efCounts.get(e + " " + f) / eCounts.get(e);
					fToProb.put(f, newT);
				}
			}

			// Set qParams for this iteration
			for (String lm : qParams.keySet()) {
				HashMap<String, Double> jiToProb = qParams.get(lm);
				for (String ji : jiToProb.keySet()) {
					String i = ji.substring(ji.indexOf(" ")+1);
					double newQ = jilmCounts.get(ji + " " + lm)
							/ ilmCounts.get(i + " " + lm);
					jiToProb.put(ji, newQ);
				}
			}
		}
	}
}
