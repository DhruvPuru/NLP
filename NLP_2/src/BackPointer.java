/**
 * The BackPointer object contains the (1) nonTerminals that maximize the
 * argument as well as the (2) q parameters and the (3) split points.
 * 
 * @author Dhruv
 *
 */
public class BackPointer {

	double pi;
	int s;
	String rhs;

	public BackPointer(double qParam, int splitPoint, String rhs) {
		pi = qParam;
		s = splitPoint;
		this.rhs = rhs;
	}

	public String toString() {
		return "q: " + pi + ", s: " + s + ", rhs: " + rhs;
	}
}
