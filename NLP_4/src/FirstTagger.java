import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FirstTagger {

	protected String tagModel;
	protected String devFile;
	protected HashMap<String, Double> featureWeights = new HashMap<String, Double>();

	public FirstTagger(String tM, String dFile) {
		tagModel = tM;
		devFile = dFile;
	}

	public void readTagModel() throws IOException {

		FileReader in = new FileReader(tagModel);
		BufferedReader br = new BufferedReader(in);

		String input;
		String[] inputToArr;
		while ((input = br.readLine()) != null) {
			inputToArr = input.split(" ");
			String feature = inputToArr[0];
			double weight = Double.parseDouble(inputToArr[1]);
			featureWeights.put(feature, weight);
		}
		br.close();
	}

	public void parseDevDat(String resultFile) throws IOException {

		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);

		File result = new File(resultFile);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				result));

		TaggerHistoryGenerator gen = new TaggerHistoryGenerator();
		gen.startProcess(TaggerHistoryGenerator.ENUM);

		TaggerDecoder decoder = new TaggerDecoder();
		decoder.startProcess();

		String input;
		ArrayList<String> results;
		ArrayList<String> words = new ArrayList<String>();
		String sentence = "";

		while ((input = br.readLine()) != null) {

			input = input.trim();
			if (input.length() == 0) {
				// Done reading this current sentence. Now process it.
				sentence += "\r\n";
				results = gen.getEnum(sentence);
				ArrayList<String> scores = computeScores(results, words);
				
				String[] arr;
				//Use tagger_decoder.py
				ArrayList<String> bestHistory = decoder.decodeHistory(scores);
				for (String s : bestHistory) {
					arr = s.split(" ");
					int wordIndex = Integer.parseInt(arr[0]) - 1;
					if (wordIndex >= words.size())
						break;
					String word = words.get(wordIndex);
					String tag = arr[2];
					bufferedWriter.write(word + " " + tag + "\r\n");
				}
				
				bufferedWriter.write("\r\n");
				words = new ArrayList<String>();
				sentence = "";
			} else {
				sentence += input + "\r\n";
				words.add(input);
			}
		}

		br.close();
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	public ArrayList<String> computeScores(ArrayList<String> results, ArrayList<String> words) {
		String[] arr;
		
		ArrayList<String> scores = new ArrayList<String>();

		for (String s : results) {

			ArrayList<String> featureList = new ArrayList<String>();
			String trimmed = s.replaceAll(" +", " ");
			arr = trimmed.split(" ");
			int wordIndex = Integer.parseInt(arr[0]) - 1;
			String tag1 = arr[1];
			String tag2 = arr[2];
			String bigramFeature = "BIGRAM:" + tag1 + ":" + tag2;
			String tagFeature = "TAG:" + words.get(wordIndex) + ":" + tag2;

			featureList.add(bigramFeature);
			featureList.add(tagFeature);

			double score = 0.0;
			double curScore;

			for (String feature : featureList) {
				try {
					curScore = featureWeights.get(feature);
				} catch (NullPointerException e) {
					curScore = 0.0;
				}
				score += curScore;
			}

			String scoreString = (wordIndex + 1) + " " + tag1 + " " + tag2
					+ " " + score;
			scores.add(scoreString);
		}
		return scores;
	}
	
	public static void main(String[] args) throws IOException {
		FirstTagger ft = new FirstTagger("tag.model", "tag_dev.dat");
		ft.readTagModel();
		ft.parseDevDat("tag_dev.out");
	}
}
