import java.io.IOException;
import java.util.ArrayList;


public class PerceptronTagger extends FirstTagger {

	public PerceptronTagger(String tM, String dFile) {
		super(tM, dFile);
		// TODO Auto-generated constructor stub
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

	public static void main (String[] args) throws IOException {
		PerceptronTagger pt = new PerceptronTagger("suffix_tagger.model", "tag_dev.dat");
		pt.readTagModel();
		pt.parseDevDat("tag_dev_percep.out");
	}
}
