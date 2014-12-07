import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Creating a class to handle python scripting for obtaining histories.
 * @author Dhruv
 *
 */
public class TaggerHistoryGenerator {

	public static final String PYTHON_PATH = "C:\\Python27\\python.exe";
	public static final String TAGGER_HISTORY_GENERATOR = "tagger_history_generator.py";
	public static final String ENUM = "ENUM";
	public static final String GOLD = "GOLD";
	
	private ProcessBuilder pb;
	private BufferedReader pReader;
	private BufferedWriter pWriter;
	private Process process;
	private String processType;
	
	public void startProcess(String processCode) throws IOException {
		this.processType = processCode;
		pb = new ProcessBuilder(PYTHON_PATH, TAGGER_HISTORY_GENERATOR, processCode);
		process = pb.start();
		pReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		pWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}
	
	public ArrayList<String> getEnum(String sentence) throws IOException {
		
		if (!processType.equals(ENUM)) {
			return null;
		}
		
		ArrayList<String> results = new ArrayList<String>();
	    pWriter.write(sentence);
	    pWriter.flush();
	    	    
	    String output;
	    while ((output = pReader.readLine()).length() != 0) {
	    	results.add(output.replaceAll(" +", " "));
	    }
	    
		return results;
	}
	
	public ArrayList<String> getGold(String sentence) throws IOException {
		
		if (!processType.equals(GOLD)) {
			return null;
		}
		
		ArrayList<String> results = new ArrayList<String>();
	    pWriter.write(sentence);
	    pWriter.flush();
	    	    
	    String output;
	    while ((output = pReader.readLine()).length() > 0) {
	    	results.add(output.replaceAll(" +", " "));
	    }    
		return results;
	}
}
