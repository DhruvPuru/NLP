
public class BackPointer {

	double q;
	int s;
	String rhs;
	
	public BackPointer(double qParam, int splitPoint, String rhs) {
		q = qParam;
		s = splitPoint;
		this.rhs = rhs;
	}
	
	public String toString(){ 
		return "q: " + q + ", s: " + s + ", rhs: " + rhs;
	}
}
