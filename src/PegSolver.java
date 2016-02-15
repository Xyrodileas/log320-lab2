import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;



public class PegSolver {

	public enum Direction {
	    Nord, Sud, Est, Ouest //
	}
	
	public int NBR_LIMITE = 10000000;
	public int[][] board;
	public static Vector<int[][]> solutions;
	public int taille;
	public static int nbrPiece;
	public int nbrMaxCoup;
	public static int nbrNode;
		
	@SuppressWarnings("resource")
	public int[][] LoadPeg(String file) throws IOException
	{	
		solutions = new Vector<int[][]>();
        String line;
        BufferedReader in;
        in = new BufferedReader(new FileReader(file));
        line = in.readLine();
        board = new int[7][];
        for (int i = 0; i < 7; i++) {
          board[i] = new int[7];
        }
        nbrNode = 0;
        nbrPiece = 0;
        // On commence à lire le board
        int tmp = 0;
        while(line != null)
        {
        	for(int i =0; i< 7; i++)
        	{
        		int current = ((int)line.charAt(i)) - 48;
        		if(current == 1)
        		{
        			nbrPiece++;
        		}
        		board[i][tmp] = current;
        	}
        	tmp++;
        	line = in.readLine();
        }
        taille = tmp;
        nbrMaxCoup = nbrPiece - 1;
        return board;
	}
	
	public boolean Jump(int x, int y, Direction dir )
	{
		int newPosX;
		int newPosY;
 
		switch (dir)
		{
		case Nord: 
            newPosX = x;
            newPosY = y - 1;
            break;

        case Sud: 
            newPosX = x;
            newPosY = y + 1;
            break;

        case Est:
            newPosX = x + 1;
            newPosY = y;
            break;
        case Ouest:
            newPosX = x - 1;
            newPosY = y;
        	break;
        default: 
        	newPosX = 0;
        	newPosY = 0;
            break;
		}
		
		int destinationX = newPosX-x+newPosX;
		int destinationY = newPosY-y+newPosY;
		if(! (isInBound(x, y) && isInBound(newPosX, newPosY) && isInBound(destinationX, destinationY) ))
			return false;
		
		if(board[x][y] == 1 && board[newPosX][newPosY] == 1 && board[destinationX][destinationY] == 2 )
		{
			board[x][y] = 2;
			board[newPosX][newPosY] = 2;
			board[destinationX][destinationY] = 1;
			nbrPiece--;
			
			return true;
		}
		
		return false;
	}
	
	public boolean JumpBack(int x, int y, Direction dir )
	{
		int newPosX;
		int newPosY;
		
		switch (dir)
		{
		case Nord: 
            newPosX = x;
            newPosY = y - 1;
            break;

        case Sud: 
            newPosX = x;
            newPosY = y + 1;
            break;

        case Est:
            newPosX = x + 1;
            newPosY = y;
            break;
        case Ouest:
            newPosX = x - 1;
            newPosY = y;
        	break;
        default: 
        	newPosX = 0;
        	newPosY = 0;
            break;
		}
		
		int destinationX = newPosX-x+newPosX;
		int destinationY = newPosY-y+newPosY;
		

		board[x][y] = 1;
		board[newPosX][newPosY] = 1;
		board[destinationX][destinationY] = 2;
		nbrPiece++;
			
		return true;
		
	}
	
	public boolean isInBound(int x, int y)
	{
		return (x >= 0 && x < taille && y >= 0 && y < taille);
	}
	
	public boolean estLibre(int x, int y)
	{
		if(board[x][y] == 2)
		{
			return true;
		}
		return false;
	}
	
	public boolean findSolution(int recursion) {
		nbrNode++;
		long startTime = System.currentTimeMillis();
        for (int x = 0; x < taille; x++) {
                for (int y = 0; y < taille; y++) {
                        for (int i = 0; i < 4; i++ ) {
                                if (Jump(x, y, Direction.values()[i])) {
                                		//int[][] save = board.clone();
                                        solutions.add(CopyBoard(board));
                                        //afficherBoard(board);
                                        // Condition finale
                                        if ((nbrPiece > 1 )) {
                                                if ( nbrNode > NBR_LIMITE || findSolution(recursion + 1)) {
                                                	
                                                        return true;
                                                } else {
                                                		if(nbrNode > NBR_LIMITE)
                                                			return false;
                                                		solutions.remove(solutions.size()-1);
                                                        JumpBack(x, y, Direction.values()[i]);
                                                }
                                        } else 
                                        {
                                        	if(nbrNode > NBR_LIMITE)
                                        	{
                                            	long StopTime = System.currentTimeMillis();
                                            	double timer = StopTime - startTime;
                                            	System.out.println("Temps d'execution (ms) : " + timer);
                                            	System.out.println("Solution en " + (recursion+2) + " coups.");
                                    			return false;
                                        	}
                                        	long StopTime = System.currentTimeMillis();
                                        	double timer = StopTime - startTime;
                                        	System.out.println("Temps d'execution (ms) : " + timer);
                                        	System.out.println("Solution en " + (recursion+2) + " coups.");
                                        		
                                            return true;
                                        }
                                }
                        }
                }                       
        }

        return false;
}
	
	public static void afficherBoard(int[][] board)
	{
		System.out.println("-------");
		for(int x =0; x < board.length ; x++)
		{
			for(int y = 0; y < board[x].length ; y++)
			{
				System.out.print(board[x][y]);
			}
			System.out.print('\n');
		}
	}
	
	public int[][] CopyBoard(int[][] element)
	{
		int[][] saved = new int[7][];;
		
		for(int i=0; i < element.length; i++)
		{
			saved[i] = new int[element[i].length];
			for(int j=0; j < element[i].length; j++)
			{
				saved[i][j]=element[i][j];
			}
		}

				
		return saved;
	}
	public static void afficherSolution()
	{
		for(int[][] boardSol : solutions)
		{
			afficherBoard(boardSol);
		}
		System.out.println("Nombre de noeuds exploré : " + nbrNode);
	}

}
