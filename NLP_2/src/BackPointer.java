
public class BackPointer {

	double pi;
	int s;
	String rhs;
	
	public BackPointer(double qParam, int splitPoint, String rhs) {
		pi = qParam;
		s = splitPoint;
		this.rhs = rhs;
	}
	
	public String toString(){ 
		return "q: " + pi + ", s: " + s + ", rhs: " + rhs;
	}
}
