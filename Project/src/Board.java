import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends JPanel implements ActionListener {

    //this JPanel holds all the data
    private int diameterOfDisk = 30;			//represents the diameter of one disk
    private int spaceBetweenDisks = 7;		    //represents the space between the disk
    private int rows;                           //represents the number of rows in the board
    private int columns;                        //represents the number of columns in the board
    private ConnectFourController controller;   // represents the controller

    // these three variable are used to animate the piece
    private Point animatingPoint;               // represents the position that the animation is currently on
    private ConnectFourModel.Slot animatingSlot;// represents the color of the slot, if the current animating piece is red or blue
    private Point stopAnimationPoint;           // the point at which the animation needs to reach when it stops animating

    public Board(int rows, int columns){
        super(null, true);//calls the super constructor

        //set its rows and columns
        this.rows = rows;
        this.columns = columns;

        //gives the animation variable default values
        animatingPoint = new Point(-1,-1);
        animatingSlot = ConnectFourModel.Slot.Empty;
        stopAnimationPoint = new Point(-1,-1);

        //make it not have a layout
        setLayout(null);
    }//end function

    public void setBoard(ConnectFourModel.Slot[][] slots){
        Graphics g = getGraphics();     //gets the graphics component
        drawTilesFromBoardConfiguration(getGraphics(), slots); //draws the board using that graphics component
    }//end function

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g); // clears the board

        if(controller != null) // if the controller exists draw the board
            drawTilesFromBoardConfiguration(g, controller.getConfiguration());
    }//end function
    public void setController(ConnectFourController c){ controller = c;} // sets the controller

    //Purpose: calls the drawTileAtPosition in a nested loop to tell the drawTileAtPosition(); so all the tiles can be drawn if there is something to draw
    private void drawTilesFromBoardConfiguration(Graphics g, ConnectFourModel.Slot[][] slotConfiguration){
        //gets the width and height of the board
        int widthOfBoard = getWidthOfBoard();
        int heightOfBoard =  getHeightOfBoard();

        g.setColor(Color.BLACK);//makes the color black
        //draws the board itself, which is just a black rectangle and then we will place the blank disks on top of it
        g.fillRect(0,0, widthOfBoard, heightOfBoard);

        //call the drawTileAtPosition an n number of time if it is not empty
        //draws all the disks on the board
        for(int rowCounter = 0; rowCounter < slotConfiguration.length; rowCounter++){
            for(int colCounter = 0; colCounter < slotConfiguration[0].length; colCounter++){
                if(animatingPoint.x == colCounter && animatingPoint.y == rowCounter) drawTileAtPosition(g, new Point(colCounter, rowCounter), animatingSlot, slotConfiguration.length);
                else drawTileAtPosition(g, new Point(colCounter, rowCounter), slotConfiguration[rowCounter][colCounter], slotConfiguration.length);
            }//end row counter loop
        }//end col counter loop
    }//end function
    //PURPOSE: draws one tile at the specified location
    private void drawTileAtPosition(Graphics g, Point pos, ConnectFourModel.Slot type, int numberOfRows){
        //need to convert the old position to new position
        pos = getGameCoordinate(pos, pos.y);

        //draw the tile depending on the type and position
        Color c;
        switch (type){
            case Blue :c = new Color(17, 45, 255);         break;
            case Red:   c = new Color(255, 2, 7);           break;
            case Empty:
            default:
                c = new Color(220, 220, 220);
                break;
        }
        g.setColor(c);//sets the color of the slot
        g.fillOval(pos.x, pos.y, diameterOfDisk, diameterOfDisk); // draws the disc
    }//end draw tile at position function

    //PURPOSE: gets the origin of the board meaning the bottom left coordinate of the board
    private Point getOriginOfBoard(){
        //get the origin again because this is broken
        //Dimension screenSize = this.getRootPane().getSize();//gets the screen size
        //calculate the bottom left coordinate of the board and return that point
        return new Point(0,0);//new Point(screenSize.width/2 - getWidthOfBoard()/2 ,screenSize.height/2 + getHeightOfBoard()/2);
    }

    //PURPOSE: converts the position with respect to the array to the position with respect to the game screen
    public Point getGameCoordinate(Point slotPosition, int numberOfRows){
        //this function will take in the position of the board and return the actual position on the screen
        int sizeOfSlotPlusExtraSpace = diameterOfDisk + spaceBetweenDisks;
        return new Point(
                getOriginOfBoard().x + slotPosition.x*sizeOfSlotPlusExtraSpace + spaceBetweenDisks,
                getOriginOfBoard().y + (slotPosition.y)*sizeOfSlotPlusExtraSpace + spaceBetweenDisks);
    }
    //PURPOSE: converts the position to becoming with respect to the array instead of the game screen
    public Point getBoardCoordinateOfPoint(Point mousePosition){
        //convert the point mousePosition into a tile position where the x represents the column and y represents the row
        Point tilePosition = new Point((mousePosition.x- getOriginOfBoard().x)/(diameterOfDisk + spaceBetweenDisks),
                (mousePosition.y-getOriginOfBoard().y)/(diameterOfDisk + spaceBetweenDisks));
        //this is just making the tilePosition transition from different grids
        return tilePosition;
    }

    //these two private functions are to get the width and height of the board, they are used to draw the board
    public int getWidthOfBoard(){  return (columns)*(diameterOfDisk +spaceBetweenDisks) + spaceBetweenDisks;}// calculates the width of the screen
    public int getHeightOfBoard(){return (rows)*(diameterOfDisk + spaceBetweenDisks) + spaceBetweenDisks; } // calculates the height of the screen

    //PURPOSE: to tell the controller not to allow any other movements while the program is animating
    public boolean isAnimating(){ return (animatingSlot != ConnectFourModel.Slot.Empty); } //

    //PURPOSE: this function inserts the disc at the specified location
    public void insertDisc(Point point, ConnectFourModel.Slot type){
    	if(point == null) {
    		return;}
    	Timer t = new Timer(100, this);//creates a new timer this

        //setup the animation variables
        animatingPoint = new Point(point.x, 0); //start it at the highest row but in the same column as the destination
        stopAnimationPoint = point; // stop when it reaches the point
        animatingSlot = type; // store the color of the disc that is falling
        t.start(); // start the timer
        repaint(); // draw the updated screen
    }

    //PURPOSE: this function is called to animate the pieces fall.
    // it gets called every 100ms, it will simply update the animation variables, (animationPoint, stopAnimationPoint, animationSlot)
    @Override public void actionPerformed(ActionEvent e) {
        Timer t = (Timer)e.getSource(); // get the timer
        if(animatingPoint.x == stopAnimationPoint.x && animatingPoint.y == stopAnimationPoint.y){
            ConnectFourModel.Slot[][] config = controller.getConfiguration();
            config[stopAnimationPoint.y][stopAnimationPoint.x] = animatingSlot;
            controller.showWinner();
            animatingSlot = ConnectFourModel.Slot.Empty;
            animatingPoint = new Point(-1,-1);
            stopAnimationPoint = animatingPoint;
            t.stop();//stop calling this function
            controller.switchTurn();
        }else animatingPoint.y++; // make animating point go down
        repaint();//repaint the entire screen
    }
}//end class
