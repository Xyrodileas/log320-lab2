import java.io.IOException;
public class Solve {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PegSolver test = new PegSolver();
		test.LoadPeg(args[0]);
		test.afficherBoard(test.board);
		if(test.findSolution(0))
		{
			System.out.println("Solution Trouve");
			
			test.afficherSolution();
		}
		else
		{
			System.out.println("Solution non Trouve");
			System.out.println("Temps d'execution : oo");
		}
		
		//test.afficherBoard(test.board);
	}

}
