import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * @author Dhruv
 *
 */
public class CKYTagger { 
	
	public static void main(String[] args) throws IOException {
		ckyTagger("parse_dev.dat");
	}
	
	public static void ckyTagger(String devFile) throws IOException {
		
		Grammar g = new Grammar("cfg_rare.counts");
		HashMap<String, Double> qParams = g.getQParams();
		
		FileReader in = new FileReader(devFile);
		BufferedReader br = new BufferedReader(in);
//		
//		String sentence;
//		while ((sentence = br.readLine()) != null) {
//			
//		}
	}
	
}
