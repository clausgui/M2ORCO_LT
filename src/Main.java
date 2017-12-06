public class Main {
	
	public static void main(String [] args) {
		Parser p = new Parser("Deneme2");
		Heuristic h = p.createSolver();
		h.Solve();
	}

}
