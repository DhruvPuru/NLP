import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class TaggerDecoder {

	public static final String TAGGER_DECODER = "tagger_decoder.py";
	public static final String HISTORY = "HISTORY";
	
	private BufferedReader pReader;
	private BufferedWriter pWriter;
	private ProcessBuilder pb;
	private Process process;
	
	public void startProcess() throws IOException{
		pb = new ProcessBuilder(TaggerHistoryGenerator.PYTHON_PATH, TAGGER_DECODER, HISTORY);
		process = pb.start();
		pReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		pWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}
	
	public ArrayList<String> decodeHistory(ArrayList<String> inputStrings) throws IOException {
		ArrayList<String> results = new ArrayList<String>();
		
		String sentence = toSentence(inputStrings);
		
	    pWriter.write(sentence);
	    pWriter.flush();
	    	    
	    String output;
	    while ((output = pReader.readLine()).length() > 0) {
	    	results.add(output);
	    }
		return results;
	}
	
	public String toSentence(ArrayList<String> inputStrings) {
		
		String sentence = "";
		for (String s: inputStrings) {
			sentence += s + "\r\n";
		}
		sentence += "\r\n";
		return sentence;
	}
 }
