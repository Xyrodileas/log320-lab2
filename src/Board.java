import java.util.Vector;

public class Board {
	
	Board[] CoupsPossible;
	int[][] board = new int[8][8];
	int nbrBlack = 0;
	int nbrWhite = 0;
	int Win = 0;
	boolean turn;
	boolean WhiteWins = false;
	boolean BlackWins = false;
	final int PIONNOIR = 2;
	final int PIONBLANC = 4;
	final boolean BLANC = true;
	final boolean NOIR = false;
	
	
	//TWEAK ICI


	final int HomeGroundValue = 50000;
	final int PieceAlmostWinValue = 38000000;//80000;
	final int PieceColumnHoleValue = 1200;
	final int WinValue = 2000000000;
	final int VALUE_VOID = 0;
	final int VALUE_PAWN = 5;
	final int PieceConnectionHValue = -8000;
	final int PieceConnectionVValue = 0;
	final int kEnnemyNumberValue = 100550;
	final int kDefenseValue = 1200;
	final int PieceDangerValue = 91500;
	final int kUnderAttack = 955000;

	public Board(int[][] myboard, boolean isWhiteTurn)
	{
		turn = isWhiteTurn;
		this.board = myboard.clone();
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board.length; y++)
			{
				if(board[x][y] == 2)
				{
					nbrBlack ++;
					if(y == 7)
					{
						Win = 2;
					}
				}
				else if(board[x][y] == 4)
				{
					nbrWhite ++;
					if(y == 0)
					{
						Win = 1;
					}
				}
			}
		}
	}
	
	public void PrintBoard()
	{
        for(int i=0; i<8;i++){
			for(int j=0; j<8 ;j++)
			{
				System.out.print(board[j][i]);
            }
            System.out.print("\n");
        }
	}
	
	public int GetPosition(int x, int y)
	{
		return board[x][y];
	}
	// Get the value of the board (heuristic)
	public int GetValue(boolean color){
		nbrBlack = 0;
		nbrWhite = 0;
		int Value = 0;
		int avant = color ? -1 : 1;
		int colorInt = color ? PIONBLANC : PIONNOIR;
		int homeSquare = color ? 7 : 0;
	    for (byte x= 0; x < 8; x++){
	    	int WhitePiecesOnColumn = 0;
	    	int BlackPiecesOnColumn = 0;
	        for (byte y = 0; y< 8; y++){
	            int square = this.GetPosition(x,y);
	            if (square == 0) continue;
	            
                if(square == PIONBLANC && y == 0)
            	{
            		WhiteWins = true;
            	}
	            if(square == PIONNOIR && y == 7)
	            {
	            	BlackWins = true;
	        	}
            
	            if (square == colorInt)
	            {
	            	
	            	
	                Value += GetPieceValue(square, x, y);
	                boolean ThreatA = false;
	                boolean ThreatB = false;

	                if(y == homeSquare)
	                	Value += HomeGroundValue;
//	                if (x > 0 && y > 0 && y < 7) 
//	                	ThreatA = (this.GetPosition(x - 1, y+avant)==0);
//	                if (x < 7 && y < 7 && y > 0) 
//	                	ThreatB = (this.GetPosition(x + 1, y + avant)==0);
//	                if (ThreatA && ThreatB) // almost win
//	                	Value += PieceAlmostWinValue;
	                
	                int dangerLine = square == PIONBLANC ? 1 : 6;
	                if((square == PIONBLANC && y == dangerLine) || (square == PIONNOIR && y == dangerLine))
	                {
		                if (x > 0) 
		                	ThreatA = (this.GetPosition(x - 1, y+avant)==0);
		                if (x < 7) 
		                	ThreatB = (this.GetPosition(x + 1, y+avant)==0);
		                if (ThreatA && ThreatB) // almost win
		                	Value += PieceAlmostWinValue*10;
	                }
	        	    // Valeur de dangé
	        	    if (square == PIONBLANC)
	        	        Value += (8-y) * (8-y) * PieceDangerValue*0.8;
	        	    else 
	        	        Value += y * y * PieceDangerValue*0.8;

	            } 
	            else if (square != 0)
	            {
	                Value -= GetPieceValue(square, x, y)*2;
	                
	        	    // Valeur de dangé
	        	    if (square == PIONBLANC)
	        	        Value -= (8-y) * (8-y) * PieceDangerValue;
	        	    else 
	        	        Value -= y * y * PieceDangerValue;
	        	    
	                boolean ThreatA = false;
	                boolean ThreatB = false;

	                if(y == homeSquare)
	                	Value -= HomeGroundValue;
//	                if (x > 0 && y > 0 && y < 7) 
//	                	ThreatA = (this.GetPosition(x - 1, y - avant)==0);
//	                if (x < 7 && y < 7 && y > 0) 
//	                	ThreatB = (this.GetPosition(x + 1, y - avant)==0);
//	                if (ThreatA && ThreatB) // almost win
//	                	Value -= PieceAlmostWinValue*10;
	                
	                int dangerLine = square == PIONBLANC ? 1 : 6;
	                if((square == PIONBLANC && y == dangerLine) || (square == PIONNOIR && y == dangerLine))
	                {
//		                if (x > 0) 
//		                	ThreatA = (this.GetPosition(x - 1, y-avant)==0);
//		                if (x < 7) 
//		                	ThreatB = (this.GetPosition(x + 1, y-avant)==0);
//		                if (ThreatA && ThreatB) // almost win
//		                	Value -= PieceAlmostWinValue*10;
	                }

	                
	                
	            	
	            }
	            if(square == PIONBLANC)
	            {
	            	WhitePiecesOnColumn++;
	            	nbrWhite++;
	            }
	            	
	            if(square == PIONNOIR)
	            {
	            	nbrBlack++;
	            	BlackPiecesOnColumn++;
	            }

	        }
	        if(color)
	        {
	        	Value += ((-(nbrWhite - nbrBlack-2))*(nbrWhite - nbrBlack-2)*kEnnemyNumberValue);
	        }
	        else
	        {
	        	Value += -((-(nbrWhite - nbrBlack-2))*(nbrWhite - nbrBlack-2)*kEnnemyNumberValue);
	        }
            
            
            
	        if (WhitePiecesOnColumn == 0)
	        	Value -= PieceColumnHoleValue;
	        if (BlackPiecesOnColumn == 0)
	        	Value += PieceColumnHoleValue;

	    }
	    // if no more material available
	    if (nbrWhite == 0) 
	    	BlackWins = true; 
	    if (nbrBlack == 0) 
	    	WhiteWins = true;

	    // winning positions
	    if (WhiteWins && color)
	    	Value += WinValue;
	    else if(WhiteWins && !color)
	    	Value -= WinValue*1.5;
	    else if (BlackWins && !color)
	    	Value += WinValue;
	    else if (BlackWins && color)
	    	Value -= WinValue*1.5;
	    return Value;
	}
	public int Defense(int square, int Column, int Row, int avant, int n)
	{
		if(n > 5)
		{
			return n+1;
		}
		int def2 = 0;
		int def3 = 0;
		
    	if (Column > 0 && Row < 7 && Row > 0 && (board[Column-1][Row-avant] == square)) 
    	{
    		def2 += Defense(square, Column-1, Row-avant, avant, n+1) ;
    		
    	}
    	if (Column < 7 && Row < 7 && Row > 0 && (board[Column+1][Row-avant] == square)) 
    	{
    		def3 += Defense(square, Column+1, Row-avant, avant, n+1) ;
    	}
    	
    	if(def2 > def3)
    	{
    		return n+def2;
    	}
    	else
    	{
    		return n+def2;
    	}

	}
	// Get the value of the piece, depending on the position on the board
	public int GetPieceValue(int square, int Column, int Row)
	{
		int Value = 0;
		int avant = square==4 ? -1 : 1;
		switch(square)
		{
		case(0):
	
			Value += VALUE_VOID;
		break;
		case(2):
		case(4):
			//Value += VALUE_PAWN;
			
			//Value += Defense(square, Column, Row, avant, 0)*kDefenseValue;
			
		
			// Si on est connecté en V
	    	if (Column > 0 && Row < 7 && Row > 0 && (board[Column-1][Row+avant] != 0)) 
	    	{
	    		if(square == board[Column-1][Row+avant])
	    		{
	    			Value += kDefenseValue;
	    		}
	    		else
	    		{
	    			Value += kUnderAttack;
	    		}
	    	}
	    	if (Column < 7 && Row < 7 && Row > 0 && (board[Column+1][Row+avant] != 0)) 
	    	{
	    		if(square == board[Column+1][Row+avant])
	    		{
	    			Value += kDefenseValue;
	    		}
	    		else
	    		{
	    			Value += kUnderAttack;
	    		}
	    	}
	    		
	    	if (Row > 0 && Row < 7 && (board[Column][Row-avant] != 0 || Row < 7 && board[Column][Row-avant] != 0)) 
	    	{
	    		Value += PieceConnectionHValue;
	    	}
	    		

		break;
		}
	    
//	    //add to the value the protected value
//	    Value += kDefenseValue;
//
//	    // evaluate attack
//	    if (Piece.AttackedValue > 0)
//	    {
//	        Value -= Piece.AttackedValue;
//	        if (Piece.ProtectedValue == 0)
//	            Value -= Piece.AttackedValue;
//	    }else{
//	        if (Piece.ProtectedValue != 0){
//	            if (Piece.PieceColor == White){
//	                if (Row == 5) Value += PieceDangerValue;
//	                else if (Row == 6) Value += PieceHighDangerValue;
//	            }else{
//	                if (Row == 2) Value += PieceDangerValue;
//	                else if (Row == 1) Value += PieceHighDangerValue;
//	            }
//	        }
//	    }
		


//	    // mobility feature
//	    Value += Piece.ValidMoves.Count;
	    
	    return Value;
	}
	
	public Board[] GetPossibleBoard(boolean IsWhiteTurn)
	{
		Vector<Board> Boards = new Vector<Board>();
		int coef;
		int pawn;
		if(IsWhiteTurn)
		{
			pawn = 4;
			coef = -1;
		}
		else
		{
			pawn = 2;
			coef = +1;
		}
        for(int x=0; x<8;x++)
        {
			for(int y=0; y<8 ;y++)
			{
				if(GetPosition(x, y) == pawn)
				{
					try {
						// Si emplacement vide
						if(GetPosition(x,y+coef) == 0)
						{
							int[][] copyBoard = copyBoard();
							copyBoard[x][y] = 0;
							copyBoard[x][y+coef] = pawn;
							Boards.add(new Board(copyBoard, IsWhiteTurn));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					try {
						// Si on peut prendre en diagonale (gauche)
						if(GetPosition(x-1,y+coef) != pawn)
						{
							int[][] copyBoard = copyBoard();
							copyBoard[x][y] = 0;
							copyBoard[x-1][y+coef] = pawn;
							Boards.add(new Board(copyBoard, IsWhiteTurn));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					try {
						// Si on peut prendre en diagonale (droite)
						if(GetPosition(x+1,y+coef) != pawn)
						{
							int[][] copyBoard = copyBoard();
							copyBoard[x][y] = 0;
							copyBoard[x+1][y+coef] = pawn;
							Boards.add(new Board(copyBoard, IsWhiteTurn));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}
        }
        CoupsPossible = Boards.toArray(new Board[0]);
        return CoupsPossible;
		
	}
	public String CalcPlay(Board Secondboard, boolean WhiteTurn)
	{
		//PrintBoard();
		//System.out.println("To \n");
		//Secondboard.PrintBoard();
		int x = 0;
		int y = 0;
		int x2 = 0;
		int y2 = 0;
		boolean first = true;
		if(WhiteTurn)
		{
	        for(int i=7; i>=0;i--){
				for(int j=7; j>=0 ;j--)
				{
					if(GetPosition(j, i) != Secondboard.GetPosition(j, i) && first )
					{
						x2 = j;
						y2 = i;
						first = false;
					}
					else if(GetPosition(j, i) != Secondboard.GetPosition(j, i) && !first )
					{
						x = j;
						y = i;		
					}


	            }
	        }
		}
		else
		{
	        for(int i=0; i<8;i++){
				for(int j=0; j<8 ;j++)
				{
					if(GetPosition(j, i) != Secondboard.GetPosition(j, i) && first )
					{
						x2 = j;
						y2 = i;
						first = false;
					}
					else if(GetPosition(j, i) != Secondboard.GetPosition(j, i) && !first )
					{
						x = j;
						y = i;		
					}


	            }
	        }
		}
		char letter = (char)(65+x);
		String result = "";

		result += (char)(65+x2);
		result += 8-(y2);
		result += letter;
		result += 8-(y);
		
		return result;
		
	}
	public int[][] copyBoard()
	{
		int[][] newBoard = new int[board.length][];
		for(int x = 0; x < board.length; x++)
		{
			newBoard[x] = new int[board[x].length];
			for(int y = 0; y < board[x].length; y++)
			{
				newBoard[x][y] = board[x][y];
			}
		}
		return newBoard;
		
	}
	public int IsItAWin()
	{
		int nbrBlackTotal=0;
		int nbrWhiteTotal=0;
		for(int x = 0; x < board.length; x++)
		{
			for(int y = 0; y < board.length; y++)
			{
				if(board[x][y] == PIONNOIR)
				{
					nbrBlackTotal ++;
					if(y == 7)
					{
						return 1;
					}
				}
				else if(board[x][y] == PIONBLANC)
				{
					nbrWhiteTotal ++;
					if(y == 0)
					{
						return 2;
					}
				}
			}
		}
		if(nbrBlackTotal == 0 )
		{
			return 2;
		}
		else if(nbrWhiteTotal == 0)
		{
			return 1;
		}
			return 0;
		
	}
}
