import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EM1 {

	public static HashMap<String, HashMap<String, Double>> emParams(int s,
			String eFile, String fFile,
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
		
		return tParams;
	}
}
