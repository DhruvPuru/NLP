import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 
 * @author Dhruv
 *
 */
public class RareCountHelpers {

	public static final String UNARY_RULE = "UNARYRULE";
	public static final String RARE = "_RARE_";

	public static HashMap<String, Integer> wordToCount(String countFile)
			throws IOException {

		FileReader in = new FileReader(countFile);
		BufferedReader br = new BufferedReader(in);

		HashMap<String, Integer> wordToCount = new HashMap<String, Integer>();

		String input;
		int count;
		while ((input = br.readLine()) != null) {
			String[] inputToArray = input.split(" ");
			String countType = inputToArray[1];
			count = 0;

			if (countType.equals(UNARY_RULE)) {
				String word = inputToArray[inputToArray.length - 1];
				count = Integer.parseInt(inputToArray[0]);
				if (wordToCount.containsKey(word)) {
					count += wordToCount.get(word).intValue();
				}
				wordToCount.put(word, count);
			}
		}

		br.close();
		return wordToCount;
	}

	public static void rareCounter(String trainingFile,
			HashMap<String, Integer> wordToCount) throws IOException,
			JSONException {

		FileReader in = new FileReader(trainingFile);
		BufferedReader br = new BufferedReader(in);

		File rareCounts = new File("parse_train_rare.dat");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
				rareCounts));

		String input;
		while ((input = br.readLine()) != null) {
			JSONArray arr = new JSONArray(input);
			GTreeNode root = getChild(arr, wordToCount);
			String sentenceWithRare = deconstructGTree(root, false);
			bufferedWriter.write(sentenceWithRare + "\n");
		}
		br.close();
		bufferedWriter.close();
	}

	/*
	 * Recursive constructs a grammar tree for the sentence and replaces rare
	 * words during construction itself.
	 */
	private static GTreeNode getChild(JSONArray arr,
			HashMap<String, Integer> wordToCount) throws JSONException {
		GTreeNode n = new GTreeNode(arr.getString(0));

		// Check for unary rule or binary rule
		if (arr.length() == 2) {
			String word = arr.getString(1);
			if ((wordToCount.containsKey(word) && wordToCount.get(word)
					.intValue() < 5)) {
				word = RARE;
			}
			n.left = new GTreeNode(word);
			n.right = null;

		} else if (arr.length() == 3) {
			n.left = getChild(arr.getJSONArray(1), wordToCount);
			n.right = getChild(arr.getJSONArray(2), wordToCount);
		}
		return n;
	}

	private static String deconstructGTree(GTreeNode root, boolean isLeft) {
		String s = "";
		
		if (root == null)
			return "";
		else if (root.left == null && root.right == null) {
			return "\"" + root.value + "\"";
		}

		else if(!isLeft){
			s += "[\"" + root.value + "\"" + ", " + deconstructGTree(root.left, true)
					+ deconstructGTree(root.right, false) + "]";
		}
		else{
			s += "[\"" + root.value + "\"" + ", " + deconstructGTree(root.left, true)
					+ deconstructGTree(root.right, false) + "], ";
		}
		return s;
	}
}
