import java.io.*;
import java.net.*;
import java.util.Hashtable;



class Client {
	
	final static double TimeToMoveMs = 900;
	final static int kNbrDeepMax = 5000;
	
	public static void main(String[] args) 
	{
	
	
	Board newboard = null;    
	boolean isWhite = false;
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    int[][] board = new int[8][8];
	try {
		MyClient = new Socket("localhost", 8888);
	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));  
	   	while(1 == 1){
			char cmd = 0;
		   	
            cmd = (char)input.read();
            		
            // D�but de la partie en joueur blanc
            if(cmd == '1'){
                byte[] aBuffer = new byte[1024];
                System.out.println("Nouvelle partie! Vous jouer blanc ");
                isWhite = true;
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }
                // On créé le plateau
                newboard = new Board(board, isWhite);

                // On lance l'alphabeta
                Board bestBoard = AlphaBetaIterationEnProfondeur(newboard, kNbrDeepMax, isWhite);
                
                
                String move = null;
                System.out.println("Nouveau : \n");
                
                // Calcul du coup a jouer pour aller au bestboard 
                move = newboard.CalcPlay(bestBoard, isWhite);
                // Le best plateau deviens notre plateau
                newboard = bestBoard;
                System.out.println(move);
                // On envoit le coup joué
				output.write(move.getBytes(),0,move.length());
				output.flush();
            }
            // D�but de la partie en joueur Noir
            if(cmd == '2'){
            	byte[] aBuffer = new byte[1024];
            	System.out.println("Nouvelle partie! Vous jouer noir ");
            	isWhite = false;
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;
                for(int i=0; i<boardValues.length;i++){
                    board[x][y] = Integer.parseInt(boardValues[i]);
                    x++;
                    if(x == 8){
                        x = 0;
                        y++;
                    }
                }
                newboard = new Board(board, !isWhite);
                
                
                //Board[] boards = GetPossibleBoards(isWhite, MyBoard);
                //int choosen = (int) (Math.random()*boards.length);
                
                
                String move = null;
                //System.out.println("Nouveau : \n");
                //newboard = boards[choosen];
                
            }


			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup jou�.
			if(cmd == '3'){
				byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                System.out.println(s);
                String[] boardValues;
                boardValues = s.split(" ");
                int x=0,y=0;

                int origineX = ((int)(boardValues[0].toCharArray())[0])-65;
                
                String tmp =""+ boardValues[0].toCharArray()[1];
                int origineY = 8-Integer.parseInt(tmp);
                
                int destX = ((int)(boardValues[2].toCharArray())[0])-65;
                
                tmp =""+ boardValues[2].toCharArray()[1];
                int destY = 8-Integer.parseInt(tmp);
                //int destY = ((int)(boardValues[2].toCharArray())[1]);
                System.out.println("OrigineX : " + origineX);
                System.out.println("OrigineY : " + origineY);
                System.out.println("DestX : " + destX);
                System.out.println("DestY : " + destY);
                
                int tmpPawn = newboard.board[origineX][origineY];
                newboard.board[origineX][origineY] = 0;
                newboard.board[destX][destY] = tmpPawn;
                int Winner = newboard.IsItAWin();
                if(Winner == 1)
                {
                	System.out.println("Blanc Win");
                	return;
                }
                else if(Winner == 2)
                {
                	System.out.println("Noir Win");
                	return;
                }
                
                Board MyBoard = new Board(newboard.copyBoard(), isWhite);
                
                // On lance l'alphabeta
                Board bestBoard = AlphaBetaIterationEnProfondeur(MyBoard, kNbrDeepMax, isWhite);
                
                String move = null;
               
                
                // Calcul du coup a jouer pour aller au bestboard 
                move = MyBoard.CalcPlay(bestBoard, isWhite);
                // Le best plateau deviens notre plateau
                
                newboard = bestBoard;
                System.out.println(move);
                // On envois le coup
				output.write(move.getBytes(),0,move.length());
				output.flush();
				// On vérifie si on a win
				Winner = newboard.IsItAWin();
                if(Winner == 1)
                {
                	System.out.println("Blanc Win");
                	return;
                }
                else if(Winner == 2)
                {
                	System.out.println("Noir Win");
                	return;
                }
				
			}
			// Le dernier coup est invalide -> T0ODO : Lever une exception
			if(cmd == '4'){
				System.out.println("Coup invalide, entrez un nouveau coup : ");
		       	String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
	// Fonction d'appel de l'alpha beta, avec profondeur iterative
	static Board AlphaBetaIterationEnProfondeur(Board board, int ProfondeurMax, boolean WhosMove)
	{
		// Initialise la variable qui stockera le meilleurs coup trouvé
		Board BestBoard = null;
	    //Watch.Reset();
		// On get le timestamps actuel (Pour vérifier qu'on ne déborde pas niveau temps)
		long startTime = System.currentTimeMillis();
		// On va incrémenter la profondeur max petit à petit, pour pouvoir stopper l'execution
	    for (int Depth = 1; Depth < ProfondeurMax; Depth++)
	    {
	    	Board LastBoard = AlphaBetaRoot( Integer.MIN_VALUE, Integer.MAX_VALUE,  Depth, board, false, WhosMove, startTime);
	    	
	        if(BestBoard != null && BestBoard.GetValue(!WhosMove) < LastBoard.GetValue(!WhosMove))
	        	BestBoard = LastBoard;
	        if(BestBoard == null)
	        	BestBoard = LastBoard;
	    	long SecondTime = System.currentTimeMillis();
	        // Calcul du temps écoulé
	        double timer = SecondTime - startTime;
	        // Si on a pris trop de temps, on break et on retourne le meilleurs plateau trouvé !
	        if (timer >= TimeToMoveMs)
	            break; 

	    }
	    //Watch.Stop();
	    System.out.println("Valeur choisis :" + BestBoard.GetValue(WhosMove)+ "\n\n------------\n------------\n------------");
	    return BestBoard;	    
	}
	// FOnction racine pour le lancement de l'alpha beta
	static Board AlphaBetaRoot(int alpha, int beta, int remaining_depth, Board CurrentBoard, boolean overtime, boolean IsWhiteTurn, double timer)
	{
		if (overtime)
			return null;
		// On récupére les fils (Plateaux possibles depuis la position)
		Board[] Successors = GetPossibleBoards(IsWhiteTurn, CurrentBoard);
		double best = Integer.MIN_VALUE;
		
		Board nextPlay = null;
		// On it�re sur les fils
	    for (int i=0, c=Successors.length; i<c; i++)
	    {
	        int newbest = AlphaBeta(alpha, beta, remaining_depth - 1, Successors[i], true, timer, !IsWhiteTurn);
	        
	        if(nextPlay == null || best < newbest)
	        {
	        	best = newbest;
	        	nextPlay = Successors[i];
	        }

	    }
	    
	    return nextPlay;
	}
	// Fonction d'alpha beta pour l'élagage
	static int AlphaBeta(int alpha, int beta, int remaining_depth, Board CurrentBoard, boolean maximising, double timer, boolean IsWhiteTurn)
	{
		int bestValue;
	
	    
	    Board[] Successors = GetPossibleBoards(IsWhiteTurn, CurrentBoard);
	    
	    if (remaining_depth == 0 )
	    {
	        return CurrentBoard.GetValue(!IsWhiteTurn);
	    }
	    //
	    
	    else if (maximising) {
	        bestValue = alpha;
	        
	        // Recurse for all children of node.
	        for (int i=0, c=Successors.length; i<c; i++) {
	        	//System.out.println("Noeud visit� " +  Successors[i].GetValue(IsWhiteTurn));
	            int childValue = AlphaBeta(bestValue, beta, remaining_depth - 1, Successors[i], false, timer, !IsWhiteTurn);
	            //System.out.println("Child value " +  childValue);
	            bestValue = Math.max(bestValue, childValue);
	            if (beta <= bestValue) {
	                break;
	            }
	            long SecondTime = System.currentTimeMillis();
		        double timefuck = SecondTime - timer;
		        if (timefuck >= TimeToMoveMs)
		            break; // timeout
	        }
	    }
	    else {
	        bestValue = beta;
	        
	        // Recurse for all children of node.
	        for (int i=0, c=Successors.length; i<c; i++) {
	        	
	            int childValue = AlphaBeta(alpha, bestValue, remaining_depth - 1, Successors[i], true, timer, !IsWhiteTurn);
	            bestValue = Math.min(bestValue, childValue);
	            //System.out.println("Noeud visit� " +  Successors[i].GetValue(IsWhiteTurn));
	            //System.out.println("Child value " +  childValue);
	            
	            if (bestValue <= alpha) {
	                break;
	            }
	            long SecondTime = System.currentTimeMillis();
		        double timefuck = SecondTime - timer;
		        if (timefuck >= TimeToMoveMs)
		            break; // timeout
	        }
	    }
	    System.out.println("Best child value " +  bestValue + " Needed Max ? " + maximising + "\n------");
	    return bestValue;

	}
	

	static Board[] GetPossibleBoards(boolean IsWhiteTurn, Board theboard)
	{
		return theboard.GetPossibleBoard(IsWhiteTurn);
	}
}