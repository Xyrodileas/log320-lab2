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
		}
		else
		{
			System.out.println("Solution non Trouve");
		}
		//test.afficherSolution();
		test.afficherBoard(test.board);
	}

}
