
import java.util.Formatter;
import java.util.Random;
import java.awt.Point;
import java.io.File;
import java.util.Scanner;

public class ConnectFourModel {

    /**
     * this is an enumerated type called slot.
     * We hold the positions of the tiles in an array and instead of having them
     * be 0, 1 ,2 we made an enumerated type because now the code is easier to read
     * it can have values of empty, red, or blue
     */
    enum Slot {
        Empty,
        Red,
        Blue
    }

    /**
     * The game state will represent all the possible stages the game can possible be in
     */
    enum GameState {
        MainMenu, CustomGame, PvP, PvCPU
    }

    enum GameProgress {
    	tieGame, blueWon, redWon, inProgress;
    }

    private GameState gameState;
    private Slot[][] boardConfiguration;
    private Slot currentTurn;
    private String errorMessage = "";
    private int rowSize;
    private Scanner input;
    private Formatter output;
    private final String PATHTOSAVEDGAME = "Project/data/savedGame.txt";

    public ConnectFourModel(int rows, int columns) {
    	if (rows < 4 || columns < 4) {
            System.out.println("Game board must be at least be of size 4x4. Board "
                    + " created with default values.");
            rows = 4;
            columns = 4;
        }
    	rowSize = columns;
        gameState = GameState.MainMenu;
        Random rand = new Random();
        currentTurn = (rand.nextFloat() < 0.5) ? Slot.Red : Slot.Blue;
        this.boardConfiguration = new Slot[rows][columns];
        this.resetConfiguration();
    }//end constructor
    
    //Determines how many columns there are (the length of a row = # of columns)
    public int getRowSize() {
    	return rowSize;
    }
    
//    public boolean checkBoardConfiguration(){ return checkBoardConfiguration(boardConfiguration); }
    public boolean checkBoardConfiguration(){    	
    	this.errorMessage = "<html>";
    	/* Checking the parameter for errors */
    	if (boardConfiguration.length != boardConfiguration.length) {
    		System.out.println("Invalid number of rows!");
    	}
    	if (boardConfiguration[0].length != boardConfiguration[0].length) {    	
    		System.out.println("Invalid number of columns!");
    	}
    	
    	/* Check if there's too many of either discs */
    	if (this.getBlueDiscsCount() > this.getRedDiscsCount() + 1) {
    		this.errorMessage += "ERROR: Too many blue dics on the board<br>";//"ERROR: Too many blue dics on the board\n";
    		System.out.println("Too many blue");
    	} else if (this.getBlueDiscsCount() + 1 < this.getRedDiscsCount()) {
    		this.errorMessage += "ERROR: Too many red dics on the board<br>";//"ERROR: Too many red dics on the board\n";
    		System.out.println("Too many red");
    		//return false;
    	}
    	
    	/* Check for floating discs */
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				if (i+1 < boardConfiguration.length) {
					if (!boardConfiguration[i][j].equals(Slot.Empty) && 
							boardConfiguration[i+1][j].equals(Slot.Empty)) {
						
						if (boardConfiguration[i][j].equals(Slot.Red)) {
							this.errorMessage += "ERROR: Red floating disc detected "
									+ "at row: " + i + " column: " + j + "<br>";
						} else if (boardConfiguration[i][j].equals(Slot.Blue)) {
							this.errorMessage += "ERROR: Blue floating disc detected "
									+ "at row: " + i + " column: " + j + "<br>";
						}
						
						System.out.println("Floating");
					}
				}
				
			}
		}
    	
    	/* Check if there are four discs in a row of the same color */
    	if (this.getWinState()) {
    		this.errorMessage += "ERROR: Four " + currentTurn.toString() + " discs "
    				+ "are together<br>";
    		System.out.println("Four discs");
    	}
        errorMessage += "</html>";
    	if(!this.errorMessage.equals("<html></html>"))
    		return false;
    	
        return true;
    }

    /** PRIVATE: Count the number of blue discs inside the configuration **/
    private int getBlueDiscsCount() {
    	int blue = 0;
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				if (boardConfiguration[i][j].equals(Slot.Blue)) blue++;
			}
		}
    	return blue;
    }
    
    /** PRIVATE: Count the number of red discs inside the configuration **/
    private int getRedDiscsCount() {
    	int red = 0;
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				if (boardConfiguration[i][j].equals(Slot.Red)) red++;
			}
		}
    	return red;
    }
    
    /** PRIVATE: Check if the game is in a winning state **/
    public boolean getWinState() {
    	boolean horizontal=false, vertical=false, diagLRD=false, diagRLD=false;
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				if (boardConfiguration[i][j].equals(Slot.Red)) {
					vertical = checkRed(i+1,j) 
							&& checkRed(i+2,j)
							&& checkRed(i+3,j);
					horizontal = checkRed(i,j+1) 
							&& checkRed(i,j+2)
							&& checkRed(i,j+3);
					diagLRD = checkRed(i+1,j+1) 
							&& checkRed(i+2,j+2)
							&& checkRed(i+3,j+3);
					diagRLD = checkRed(i+1,j-1) 
							&& checkRed(i+2,j-2)
							&& checkRed(i+3,j-3);
					
				} else if (boardConfiguration[i][j].equals(Slot.Blue)) {
					vertical = checkBlue(i+1,j) 
							&& checkBlue(i+2,j)
							&& checkBlue(i+3,j);
					horizontal = checkBlue(i,j+1) 
							&& checkBlue(i,j+2)
							&& checkBlue(i,j+3);
					diagLRD = checkBlue(i+1,j+1) 
							&& checkBlue(i+2,j+2)
							&& checkBlue(i+3,j+3);
					diagRLD = checkBlue(i+1,j-1) 
							&& checkBlue(i+2,j-2)
							&& checkBlue(i+3,j-3);					
				}
				
				if (horizontal || vertical || diagLRD || diagRLD) return true; // Someone won
			}
		}
    	return false;//"";
    }
    
    /** PRIVATE: Check if certain slot has red disc **/
    private boolean checkRed(int i, int j) {
    	if (i < 0 || j < 0 || i > 
    		this.getRows()-1 || j > this.getColumns()-1) 
    			return false;
    	if (boardConfiguration[i][j].equals(Slot.Red)) return true;
    	return false;
    }
    
    /** PRIVATE: Check if certain slot has red disc **/
    private boolean checkBlue(int i, int j) {
    	if (i < 0 || j < 0 || i > 
    		this.getRows()-1 || j > this.getColumns()-1) 
    			return false;
    	if (boardConfiguration[i][j].equals(Slot.Blue)) return true;
    	return false;
    }
    
    /** PRIVATE: Return number of rows **/
    private int getRows() { return boardConfiguration.length; }
    
    /** PRIVATE: Return number of columns **/
    private int getColumns() { return boardConfiguration[0].length; }

    /**
	 * Initialize the input file scanner
	 * @param name - Name/Path of the input file
	 */
	private void openInputFile(String name) {
		try {
			input = new Scanner(new File(name));
		} catch (Exception e) {
			System.out.println("File not found!");
		}
	}

	/**
	 * Close the input file scanner
	 */
	private void closeInputFile() { input.close(); }
	
	// Opens the output file if it exists
	private void openOutputFile(String file) {
		try {
			output = new Formatter(file);
		} catch (Exception e) {
			System.out.println("An error occurred");
		}
	}
		
	private void closeOutputFile() { output.close(); }
    
	/** Saves the current state of the game into a text file **/
    public void saveState () {
    	openOutputFile(PATHTOSAVEDGAME);
    	
    	for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getColumns(); j++) {
				output.format(boardConfiguration[i][j].toString());
				if (j < this.getColumns()-1) output.format(" ");
			}
			if (i < this.getRows()-1) output.format("\n");
		}

        output.format("");
    	
    	closeOutputFile();
    }

    /** Loads the saved state of the game from the text file **/
    public void loadState() {
    	openInputFile(PATHTOSAVEDGAME);
    	
    	int rowNum = 0, colNum = 0;
    	
    	while (input.hasNext()) {
    		String[] currentRow = input.nextLine().split(" ");
    		
    		for (int i = 0; i < currentRow.length; i++) {				
    			boardConfiguration[rowNum][colNum++] = Slot.valueOf(currentRow[i]);
			}
    		rowNum++;
    		colNum = 0;
    	}
    	closeInputFile();
    }
	
	/** Returns current state of the Game **/
    public GameProgress getGameProgess() {
    	if (this.getWinState()) {
    		if (this.getTurn() == Slot.Blue) return GameProgress.blueWon;
    		else return GameProgress.redWon;
    	}
    	else if ((this.getBlueDiscsCount() + this.getRedDiscsCount()) == (this.getRows() * this.getColumns())) {
    		return GameProgress.tieGame;
    	}
    	else {
    		return GameProgress.inProgress;
    	}
    }

    /** Returns whose turn it is as a String (i.e. "red" or "blue") **/
    public Slot getTurn(){ return currentTurn; }
    public void setTurn(Slot newTurn){ if(currentTurn!=Slot.Empty) currentTurn = newTurn;}
    
    /** Sets the turn to either Red or Blue randomly and returns it **/
    public Slot getRandomTurn() {
    	Random r = new Random();
		int randomNum = r.nextInt(3-1) + 1; // Generate random number that is [1,3)
		
		if (randomNum == 1) {
			this.setTurn(Slot.Blue);
			return Slot.Blue;
		}
		else {
			this.setTurn(Slot.Red);
			return Slot.Red;
		}
    }
    
    /** Returns the String with the error message **/
    public String getErrorMessage() { return this.errorMessage; }
    
    public Slot[][] getBoardConfiguration(){ return boardConfiguration; }

    public void setGameState(GameState g){ gameState = g; }
    public GameState getGameState(){return gameState; }
    
    /** Makes the entire board configuration empty slots **/
    public void resetConfiguration() {
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				boardConfiguration[i][j] = Slot.Empty;
			}
		}
    }
    
    /** PRIVATE: Visually see the board's configuration **/
    /** -- Used for Debugging -- **/
    private void show() {
    	for (int i = 0; i < boardConfiguration.length; i++) {
			for (int j = 0; j < boardConfiguration[0].length; j++) {
				System.out.printf(boardConfiguration[i][j] + "\t");
			}
			System.out.printf("\n");
		}
    }
    
    //the user clicks somewhere.  That somewhere will give us a point.  The point has (x,y) coordinates.
    //This method will return the coordinates of the first EMPTY slot available in that column
    // returns point as Point(col, row)
    public Point nextAvailableSlot(int row, int col){ return nextAvailableSlot(new Point(row,col)); }
    public Point nextAvailableSlot(Point p) {
    	int counter = boardConfiguration.length-1;
    	Point insertHere = new Point(p.x, counter);
    	while (counter >= 0) {
    		Slot currentSlot = boardConfiguration[counter][p.x];//boardConfiguration[p.x][counter];//Point(x,counter);
            if ( currentSlot != Slot.Empty){//Slot.Red || currentSlot == Slot.Blue) { //if the slot is full;
    			counter--;
    		}else{
    			return new Point(p.x, counter);//insertHere; //from bottom-to-top, it will return the first EMPTY slot.
    		}
    	}
	    return null; //This part is only reachable if all slots in this column are full.
    }

//    public static void main(String[] args) {
//    	ConnectFourModel model = new ConnectFourModel(5,5);
//    	
//    	Slot[][] slot = {{Slot.Empty, Slot.Empty, Slot.Empty, Slot.Empty, Slot.Empty},
//    					 {Slot.Empty, Slot.Blue, Slot.Blue, Slot.Empty, Slot.Empty},
//    					 {Slot.Empty, Slot.Red, Slot.Red, Slot.Blue, Slot.Empty},
//    					 {Slot.Empty, Slot.Red, Slot.Blue, Slot.Blue, Slot.Empty},
//    					 {Slot.Blue, Slot.Red, Slot.Red, Slot.Red, Slot.Blue}};
//    	
//    	show(slot);
//    	model.setBoardConfiguration(slot);
//    }

    //// --------- start of AI Code ----------

    //// this function returns the position where the AI will place their piece
    public Point doTurn(){
        //find the score at all the column values
        //absolute of the highest value

        int maxValue = -1;
        int col = 0;
        Point point = null;
        while (col < getBoardWidth()){
            Point currentPoint = nextAvailableSlot(col, 0); //new Point(col, 0);
            if(currentPoint == null){ col++; continue; }
//            System.out.println("currentPoint: " + currentPoint);
            int currentValue = calculateScoreForTileAt(currentPoint);
            if(currentValue > maxValue){
                maxValue = currentValue;
                point = currentPoint;
            }
            col++;
        }
        //now return this point
        //point = new Point(point.y, point.x);
        return point;
    }

    // how the AI works is that it finds the score at all the possible moves and puts the piece on the position with the highest value
    // this function is used to calculate the score of each position
    // Arugments
    //  point = the position that you want the score of
    private int calculateScoreForTileAt(Point point){
        int score = 0;
        for(Point vector : new Point[]{new Point(0,1), new Point(1,0), new Point(1,1), new Point(1,-1)}){
            int scoreOfCurrentDirection = findScoreAtDirection(vector.x, vector.y, point);
            if(scoreOfCurrentDirection > score){
                score = scoreOfCurrentDirection;
            }
        }
        return score;

    }


    //// the computer finds the score
    //// this function is used to calculate the score of a single position with a certain direction
    //// Arugments
    ////  point = the position that you want the score of.
    ////          convention of point used= (col, row)
    ////  rowIncrementor and colIncrementor = determines the direction.
    ////      For example if (rowIncrementor, colIncrementor) = (0,1) would mean the direction a horizontal line
    ////                     (1,0) would mean a vertical line
    ////                    (1,1) would mean a diagonal line face up as x increases -> /
    ////                    (1,-1) would mean a diagonal line facing down as x increases -> \
    private int findScoreAtDirection(int rowIncrementor, int colIncrementor, Point point){

        int currentLength = 0; // it starts of at one because if you put a piece down here it will have a sequence of itself
        int oponentLength = 0;

        //Slot previousColor = currentTurn;
        Point firstPoint = new Point(point.x + colIncrementor, point.y+rowIncrementor);//get position of points
        Slot previousColor;
        if(isInBounds(firstPoint)) {
            previousColor = boardConfiguration[firstPoint.y][firstPoint.x];
            if(previousColor != Slot.Empty) {
                //check the left side
                for (int discFromPoint = 1; true; discFromPoint++) {
                    Point currentPoint = new Point(point.x + colIncrementor * discFromPoint, point.y + rowIncrementor * discFromPoint);//get position of points
                    if (!isInBounds(currentPoint)) break;
                    Slot color = boardConfiguration[currentPoint.y][currentPoint.x]; // get the color
                    if (previousColor == color) {
                        if (color == currentTurn) currentLength++;
                        else oponentLength++;
                    } else
                        break;
                }
            }
        }
        //check the right side
//        previousColor = currentTurn;
        firstPoint = new Point(point.x - colIncrementor, point.y - rowIncrementor);//get position of points
        if(isInBounds(firstPoint)) {
            previousColor = boardConfiguration[firstPoint.y][firstPoint.x];
            if(previousColor != Slot.Empty) {

                for (int discFromPoint = 1; true; discFromPoint++) {
                    Point currentPoint = new Point(point.x - colIncrementor * discFromPoint, point.y - rowIncrementor * discFromPoint);
                    if (!isInBounds(currentPoint)) break;
                    Slot color = boardConfiguration[currentPoint.y][currentPoint.x];
                    if (previousColor == color) {
                        if (color == currentTurn) currentLength++;
                        else oponentLength++;
                    } else
                        break;

//                previousColor = color;
                }
            }
        }
        //convert the lengths into scores
        //make sure that the lengths does not exceed 4 or else it will break the concept of the g score
        currentLength = Math.min(currentLength, 4);
        oponentLength = Math.min(oponentLength, 4);

        if(currentLength+oponentLength <= 1) return currentLength+oponentLength;
        return (int)Math.pow(2, currentLength) + (int)Math.pow(2, oponentLength);
    }//end function

    //this function checks if the point that is passed in is inside the board or not
    private boolean isInBounds(Point point){
        if(point.x < 0 || point.y < 0) return false;
        if(point.x >= boardConfiguration[0].length || point.y >= boardConfiguration.length) return false;
        return true;
    }

    //just gets the width of the board
    //I used this function to get the width of the board because it
    //made it abstract
    private int getBoardWidth(){ return boardConfiguration[0].length; }
    //// ---------- end of AI Code ----------


    public static void main(String[] args){
        //this code is for testing the AI
        //we will keep this code here because later if we want to change the AI we can still test it using the same code
        ConnectFourModel m = new ConnectFourModel(6,7);
        m.resetConfiguration();
        m.boardConfiguration[5][0] = Slot.Blue;
        m.boardConfiguration[5][1] = Slot.Blue;
        m.boardConfiguration[5][2] = Slot.Red;

        System.out.println("nextAvailableSlot(2,5): " + m.nextAvailableSlot(2,5));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(1,1));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(2,2));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(3,3));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(4,4));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(5,5));
        System.out.println("nextAvailableSlot: " + m.nextAvailableSlot(6,6));

        System.out.println("the score for position (0,5) is: " + m.calculateScoreForTileAt(new Point(0,5)));
        System.out.println("the score for position (1,5) is: " + m.calculateScoreForTileAt(new Point(1,5)));
        System.out.println("the score for position (2,5) is: " + m.calculateScoreForTileAt(new Point(2,4)));
        System.out.println("the score for position (3,5) is: " + m.calculateScoreForTileAt(new Point(3,5)));
        System.out.println("the score for position (4,5) is: " + m.calculateScoreForTileAt(new Point(4,5)));
        System.out.println("the score for position (5,5) is: " + m.calculateScoreForTileAt(new Point(5,5)));
        System.out.println("the score for position (6,5) is: " + m.calculateScoreForTileAt(new Point(6,5)));

        System.out.println("the direction stuff for (0,5)");
        Point point = new Point(0,5);
        for(Point vector : new Point[]{new Point(0,1), new Point(1,0), new Point(1,1), new Point(1,-1)}) {
            int scoreOfCurrentDirection = m.findScoreAtDirection(vector.x, vector.y, point);
            System.out.println("for vx: " + vector.x + " vy: " + vector.y + " point: " + point + " score: " + scoreOfCurrentDirection);
        }

        System.out.println("\n\n");
        System.out.println("the next piece should be placed at : " + m.doTurn().toString());
    }

}//end class
