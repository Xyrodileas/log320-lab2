import java.util.Vector;

public class Board {
	
	final int PIONNOIR = 2;
	final int PIONBLANC = 4;
	final boolean BLANC = true;
	final boolean NOIR = false;
	final int HomeGroundValue = -1500;
	final int PieceAlmostWinValue = 150;
	final int PieceColumnHoleValue = 500;
	final int WinValue = 100;
	final int VALUE_VOID = 0;
	final int VALUE_PAWN = 5;
	final int PieceConnectionHValue = 50;
	final int PieceConnectionVValue = 50;
	final int kEnnemyNumberValue = 500;
	final int kDefenseValue = 50;
	final int PieceDangerValue = 50;
	Board[] CoupsPossible;
	int[][] board = new int[8][8];
	int nbrBlack = 0;
	int nbrWhite = 0;
	int Win = 0;
	boolean turn;
	boolean WhiteWins = false;
	boolean BlackWins = false;
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
	    for (byte x= 0; x < 8; x++){
	    	int WhitePiecesOnColumn = 0;
	    	int BlackPiecesOnColumn = 0;
	        for (byte y = 0; y< 8; y++){
	            int square = this.GetPosition(x,y);
	            if (square == 0) continue;
	            if (square == PIONBLANC)
	            {
	            	nbrWhite++;
	            	WhitePiecesOnColumn++;
	                Value += GetPieceValue(square, x, y);
	                boolean ThreatA = false;
	                boolean ThreatB = false;
	                if(color && y == 0)
	                	{
	                		WhiteWins = true;
	                	}
	                if(!color && y == 8)
	                {
	                	BlackWins = true;
                	}
	                if(y == 0)
	                	Value += HomeGroundValue;
	                if (x > 0 && y > 0) 
	                	ThreatA = (this.GetPosition(y - 1, 7)==0);
	                if (x < 7 && y < 7) 
	                	ThreatB = (this.GetPosition(y + 1, 7)==0);
	                if (ThreatA && ThreatB) // almost win
	                	Value += PieceAlmostWinValue;

	            } 
	            else if (square == PIONNOIR)
	            {
	            	nbrBlack++;
	            	BlackPiecesOnColumn++;
	                Value -= GetPieceValue(square, x, y);
	                
	                
	            	
	            }
	            //Value += (nbrWhite - nbrBlack)*kEnnemyNumberValue;
    	        if (WhitePiecesOnColumn == 0)
    	        	Value -= PieceColumnHoleValue;
    	        if (BlackPiecesOnColumn == 0)
    	        	Value += PieceColumnHoleValue;
	        }

	    }
	    // if no more material available
	    if (nbrWhite == 0) 
	    	BlackWins = true; 
	    if (nbrBlack == 0) 
	    	WhiteWins = true;

	    // winning positions
	    if (WhiteWins)
	    	Value += WinValue;
	    if (BlackWins)
	    	Value -= WinValue;
	    return Value;
	}
	// Get the value of the piece, depending on the position on the board
	public int GetPieceValue(int square, int Column, int Row)
	{
		int Value = 0;
		switch(square)
		{
		case(0):
	
			Value += VALUE_VOID;
		break;
		case(2):
		case(4):
			Value += VALUE_PAWN;
			// Si on est connecté en H ou en V
	    	if ((Column > 0 && (board[Column-1][Row]) != 0) || (Column < 7 && board[Column+1][Row] != 0)) 
	    		Value += PieceConnectionHValue;
	    	if ((Row > 0 && board[Column][Row-1] != 0) || (Row < 7 && board[Column][Row+1] != 0)) 
	    		Value += PieceConnectionVValue;

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
		
	    // Valeur de dangé
	    if (square == PIONBLANC)
	        Value += (8-Row) * PieceDangerValue;
	    else
	        Value += Row * PieceDangerValue;

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
							Boards.add(new Board(copyBoard, !IsWhiteTurn));
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
							Boards.add(new Board(copyBoard, !IsWhiteTurn));
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
							Boards.add(new Board(copyBoard, !IsWhiteTurn));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}
        }
        //System.out.println("CAS POSSIBLES \n");
        //for(Board x : Boards)
        //	x.PrintBoard();
        //System.out.println("\n\n");
        CoupsPossible = Boards.toArray(new Board[0]);
        return CoupsPossible;
		
	}
	public String CalcPlay(Board Secondboard, boolean WhiteTurn)
	{
		PrintBoard();
		System.out.println("To \n");
		Secondboard.PrintBoard();
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
		if(nbrBlack == 0 || Win == 2)
		{
			return 2;
		}
		else if(nbrWhite == 0 || Win == 1)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
