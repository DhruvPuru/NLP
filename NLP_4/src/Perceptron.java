import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Perceptron {

	private String trainFile;
	private String modelFile;
	private ArrayList<String> corpus;
	private ArrayList<ArrayList<String>> sentenceWords;
	private TaggerHistoryGenerator goldGen;
	private TaggerHistoryGenerator enumGen;
	private TaggerDecoder decoder;
	private HashMap<String, Double> featureWeights;

	public Perceptron(String tF, String mF) throws IOException {
		trainFile = tF;
		modelFile = mF;
		featureWeights = new HashMap<String, Double>();
		corpus = new ArrayList<String>();
		sentenceWords = new ArrayList<ArrayList<String>>();
		goldGen = new TaggerHistoryGenerator();
		enumGen = new TaggerHistoryGenerator();
		decoder = new TaggerDecoder();

		FileReader in = new FileReader(trainFile);
		BufferedReader br = new BufferedReader(in);

		// First read in the corpus in the desired format
		String input;
		ArrayList<String> pairs = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();

		while ((input = br.readLine()) != null) {
			if (input.length() > 0) {
				pairs.add(input);
				input = input.replaceAll("\t", " ");
				input = input.replaceAll(" +", " ");
				String[] arr = input.split(" ");
				words.add(arr[0]);
			} else {
				String sentence = TaggerDecoder.toSentence(pairs);
				corpus.add(sentence);
				sentenceWords.add(words);
				pairs = new ArrayList<String>();
				words = new ArrayList<String>();
			}
		}
		br.close();
	}

	public void trainParams(int iterations) throws IOException {

		goldGen.startProcess(TaggerHistoryGenerator.GOLD);
		enumGen.startProcess(TaggerHistoryGenerator.ENUM);
		decoder.startProcess();
		
		File outputModel = new File(modelFile);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				outputModel));

		for (int i = 0; i < iterations; i++) {
			for (int j = 0; j < corpus.size(); j++) {

				String s = corpus.get(j);
				// Generate the GOLD tag (y_i) history
				ArrayList<String> gold = goldGen.getGold(s);
				// Get all histories
				ArrayList<String> allHist = enumGen.getEnum(s);
				ArrayList<String> words = sentenceWords.get(j);
				ArrayList<String> scores = computeScores(allHist, words);
				ArrayList<String> bestHistory = decoder.decodeHistory(scores);
								
				//Now update the v params				
				if (!sameTags(gold, bestHistory)) {
					//Add the feature counts for gold history
					for (int k = 0; k < gold.size(); k++) {
						String g = gold.get(k);
						ArrayList<String> goldFeatures = getFeatureList(g, words);
//						System.out.println(goldFeatures);
						
						for (String feature: goldFeatures) {
							double weight = 1.0;
							if (featureWeights.containsKey(feature)) {
								weight += featureWeights.get(feature);
							}
							featureWeights.put(feature, weight);
						}
					}
					
					//Subtract feature counts from wrong history
					for (int k = 0; k < gold.size(); k++) {
						String b = bestHistory.get(k);
						ArrayList<String> bFeatures = getFeatureList(b, words);
//						System.out.println(bFeatures);

						for (String feature: bFeatures) {
							double weight = -1.0;
							if (featureWeights.containsKey(feature)) {
								weight += featureWeights.get(feature);
							}
							featureWeights.put(feature, weight);
						}
					}
				}
			}
		}
		
		for (String feature: featureWeights.keySet()) {
			String out = feature + " " + featureWeights.get(feature) + "\r\n";
			bufferedWriter.write(out);
		}
		
		bufferedWriter.close();
	}

	/*
	 * Check if z_i == y_i
	 */
	public boolean sameTags(ArrayList<String> t1, ArrayList<String> t2) {
		for (int i = 0; i < t1.size(); i++) {
			if (!(t1.get(i).equals(t2.get(i)))) {
//				System.out.println("NO WINNER GO HOME");
				return false;
			}
		}
//		System.out.println("We have a winner!");
		return true;
	}
	
	public ArrayList<String> computeScores(ArrayList<String> results, ArrayList<String> words) {

		ArrayList<String> scores = new ArrayList<String>();
		for (String s : results) {

			ArrayList<String> featureList = new ArrayList<String>();
			String trimmed = s.replaceAll(" +", " ");
			String[] arr = trimmed.split(" ");
			int wordIndex = Integer.parseInt(arr[0]) - 1;
			String tag1 = arr[1];
			String tag2 = arr[2];
			
			String bigramFeature = "BIGRAM:" + tag1 + ":" + tag2;
			String curWord = words.get(wordIndex);
			String tagFeature = "TAG:" + curWord + ":" + tag2;
			
			featureList.add(bigramFeature);
			featureList.add(tagFeature);
			
			String suffFeature;
			for (int j = 1; j <= Math.min(3, curWord.length()); j++) {
				String u = curWord.substring(curWord.length() - j);
				suffFeature = "SUFF:" + u + ":" + j + ":" + tag2;
				featureList.add(suffFeature);
			}

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

	public ArrayList<String> getFeatureList(String s, ArrayList<String> words) {
		ArrayList<String> featureList = new ArrayList<String>();
		String trimmed = s.replaceAll(" +", " ");
		String[] arr = trimmed.split(" ");
		int wordIndex = Integer.parseInt(arr[0]) - 1;
		
		String tag1 = arr[1];
		String tag2 = arr[2];
		
		String bigramFeature = "BIGRAM:" + tag1 + ":" + tag2;
		String curWord = words.get(wordIndex);
		String tagFeature = "TAG:" + curWord + ":" + tag2;
		
		featureList.add(bigramFeature);
		featureList.add(tagFeature);
		
		String suffFeature;
		for (int j = 1; j <= Math.min(3, curWord.length()); j++) {
			String u = curWord.substring(curWord.length() - j);
			suffFeature = "SUFF:" + u + ":" + j + ":" + tag2;
			featureList.add(suffFeature);
		}	
		return featureList;
	}
	
	public static void main(String[] args) throws IOException {
		Perceptron p = new Perceptron("tag_train.dat", "suffix_tagger.model");
		p.trainParams(5);
	}
}
