import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FirstTagger {

	protected String tagModel;
	protected String devFile;
	
	private HashMap<String, Double> featureWeights = new HashMap<String, Double>();
	
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
	
	public void parseDevDat(String resultFile) throws IOException{
		
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
		ArrayList<String> scores = new ArrayList<String>();
		String sentence = "";
		
		while ((input = br.readLine()) != null) {
			
			input = input.trim();
			if (input.length() == 0) {
				//Done reading this current sentence. Now process it.
				sentence += "\r\n";
				results = gen.getEnum(sentence);
//				System.out.println(results);
				HashMap<String, Double> featureMap = new HashMap<String, Double>();
				String[] arr;
				
				for (String s: results) {
					String trimmed = s.replaceAll(" +", " ");
					arr = trimmed.split(" ");
					int wordIndex = Integer.parseInt(arr[0]) - 1;
					String tag1 = arr[1];
					String tag2 = arr[2];
					String bigramFeature = "BIGRAM:" + tag1 + ":" + tag2;
//					System.out.println(bigramFeature);
//					String tagFeature = "TAG:" + words.get(wordIndex) + ":" + tag2;
					
					double featureCount = 1.0;
					if (featureMap.containsKey(bigramFeature)) {
						featureCount += featureMap.get(bigramFeature);
					}
					featureMap.put(bigramFeature, featureCount);
					
					double score;
					try {
						score = featureWeights.get(bigramFeature) * featureMap.get(bigramFeature);
					}
					catch (NullPointerException e) {
						score = 0.0;
					}
					
					String scoreString = (wordIndex+1) + " " + tag1 + " " + tag2 + " " + score;
					scores.add(scoreString);
					
//					System.out.println(scoreString);			
					
//					featureCount = 1.0;
//					if (featureMap.containsKey(tagFeature)) {
//						featureCount += featureMap.get(tagFeature);
//					}
//					featureMap.put(tagFeature, featureCount);		
				}
				
				ArrayList<String> bestHistory = decoder.decodeHistory(scores);
//				System.out.println(bestHistory);
				for (String s: bestHistory) {
					arr = s.split(" ");
					int wordIndex = Integer.parseInt(arr[0]) - 1;
					if (wordIndex >= words.size())
						break;
					String word = words.get(wordIndex);
					String tag = arr[2];
					bufferedWriter.write(word + " " + tag + "\r\n");
				}
				bufferedWriter.write("\r\n");
				
				scores = new ArrayList<String>();
				words = new ArrayList<String>();
				sentence = "";
			}
			else {
				sentence += input + "\r\n";
				words.add(input);
			}
		}
		
		br.close();
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	public static void main (String[] args) throws IOException {
		FirstTagger ft = new FirstTagger("tag.model", "tag_dev.dat");
		ft.readTagModel();
		ft.parseDevDat("tag_dev.out");
	}
}
