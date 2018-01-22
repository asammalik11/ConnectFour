import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ConnectFourController {
    private ConnectFourView view;       // reference to the view
    private ConnectFourModel model;     // reference to the model

    //the contructor, takes in a view and a model and displays it on the screen
    public ConnectFourController(ConnectFourView v, ConnectFourModel m){
        //updates the variables
        view = v;
        model = m;

        Listener listener = new Listener(); // create a listener
        view.addCalculateListener(listener, listener); // add the listener to the view
    }//end constructor

    //Purpose: shows the winner by displaying a popup which says the winners name and also updates the text on the screen to say the winner
    public void showWinner(){
        //finds the winner and calls the view accordingly
        if (model.getGameProgess() == (ConnectFourModel.GameProgress.blueWon)){      view.setMessageText("Blue Won");       view.displayMessageAsPopup("Blue Won");      }
        if (model.getGameProgess() == (ConnectFourModel.GameProgress.redWon)){       view.setMessageText("Red Won");        view.displayMessageAsPopup("Red Won");       }
        else if (model.getGameProgess() == (ConnectFourModel.GameProgress.tieGame)){ view.setMessageText("You Both Lose");  view.displayMessageAsPopup("You both lose"); }
    }

    //PURPOSE: switches the current turn of the game
    public void switchTurn(){
        //if its blue makes it red, if its red it makes it the blue players turn
        //calls the model and the view to inform them about the change
        if (model.getTurn() == ConnectFourModel.Slot.Blue) {
            model.setTurn(ConnectFourModel.Slot.Red);
            view.setTurn(ConnectFourModel.Slot.Red);
        }
        else if (model.getTurn() == ConnectFourModel.Slot.Red) {
            model.setTurn(ConnectFourModel.Slot.Blue);
            view.setTurn(ConnectFourModel.Slot.Blue);
        }

        if(model.getGameState() == ConnectFourModel.GameState.PvCPU && model.getTurn() == ConnectFourModel.Slot.Blue){
            if(model.getGameProgess() != ConnectFourModel.GameProgress.inProgress) return;
            Point tilePosition = model.doTurn();
            view.insertDisc(model.nextAvailableSlot(tilePosition), model.getTurn());
            view.setMessageText("vs CPU game is in progress...");
            return;
        }

    }

    //PURPOSE: this function needs to return the current configuration of the board
        // this function is called by the view to get the configuration so that the view can draw it
        // this function works by getting the board configuration from the model and then returns it
    public ConnectFourModel.Slot[][] getConfiguration(){ return model.getBoardConfiguration(); }

    //creates a class called Listener that handles listener events such as actionPerformed, and handles all the mouse events which is called from the user input
    class Listener implements ActionListener, MouseInputListener {
        public Listener(){}//empty constructor

        //Purpose: when the user presses the main menu button it will call this function, and it will either go to the game stage or the custom game depending on what is clicked
        @Override
        public void actionPerformed(ActionEvent e) {
            //will get called for the menu buttons stuff
            //check what button is pressed
            String buttonPressedIconString = ((JButton) e.getSource()).getIcon().toString();//gets the image name of the buttons
            if (buttonPressedIconString.contains(ConnectFourView.mainPlayButtonImageName)) {
                //the play button in the main menu
                model.setGameState(ConnectFourModel.GameState.PvP);    // updates the game state
                model.resetConfiguration();                             // resets the board
                view.switchScreen(ConnectFourModel.GameState.PvP);     // tells the view to switch the screen to game screen
                view.setTurn(model.getRandomTurn());                    // gives the view a random turn
                view.setMessageText("PvP game is in progress");             // displays the message game is in progress in the text field
            }else if(buttonPressedIconString.contains(ConnectFourView.mainPlayAIButtonImageName)){
                model.setGameState(ConnectFourModel.GameState.PvCPU);    // updates the game state
                model.resetConfiguration();                             // resets the board
                view.switchScreen(ConnectFourModel.GameState.PvCPU);     // tells the view to switch the screen to game screen
                view.setTurn(model.getRandomTurn()); // gives the view a random turn
                view.setMessageText("vs CPU game is in progress");             // displays the message game is in progress in the text field

                if(model.getTurn() == ConnectFourModel.Slot.Blue) {
                    Point tilePosition = model.doTurn();
                    view.insertDisc(model.nextAvailableSlot(tilePosition), model.getTurn());
                }
            } else if (buttonPressedIconString.contains(ConnectFourView.mainCustomButtonImageName)) {
                // the custom game button in the main menu
                model.setGameState(ConnectFourModel.GameState.CustomGame);      // updates the game state
                model.resetConfiguration();                                     // resets the board
                view.switchScreen(ConnectFourModel.GameState.CustomGame);       // tells the view to switch the screen to game screen
                view.setTurn(model.getRandomTurn());                            // gives the view a random turn

            }else if (buttonPressedIconString.contains(ConnectFourView.saveStateImage)) {
                //Save progress.
                if(model.checkBoardConfiguration()) { //if the board is not valid then it does not store the board configuration
                	model.saveState();                                      // saves the game state
                	view.displayMessageAsPopup("The game has been saved");  // displays the message as a popup
                } else view.displayMessageAsPopup("Can not save the current state, the current state is not valid");    // the current state is not possible so tells the user that it is not going to save
            }else if(buttonPressedIconString.contains(ConnectFourView.mainLoadSavedStateImageName)){
                model.loadState();                                      // loads the state from model
                model.setGameState(ConnectFourModel.GameState.PvP);    // sets the current state to the game
                view.switchScreen(ConnectFourModel.GameState.PvP);     // switches the game screen to the game
                view.setTurn(model.getTurn());                          // shows the correct image on the view class
            }else if(buttonPressedIconString.contains(ConnectFourView.gameMainMenuImageName)){
                model.setGameState(ConnectFourModel.GameState.MainMenu);        // sets the state to main menu
                view.switchScreen(ConnectFourModel.GameState.MainMenu);         // switches the screen to that state
                view.setMessageText("");                                        // removes any previous message in the text field
            }else if(buttonPressedIconString.contains(ConnectFourView.customGameResetImageName)){
                model.resetConfiguration();                     // resets the configuration of the board
                view.setBoard(model.getBoardConfiguration());   // resets the board
                view.setMessageText("");                        // removes any error message that was displayed in the text field
                view.setTurn(model.getRandomTurn());
            }else if(model.getGameState() == ConnectFourModel.GameState.CustomGame) {
                //if it is in the custom game then it handles these buttons or else it does not, this is because things like the red and blue button should not be working in the game
                if (buttonPressedIconString.contains(ConnectFourView.gameRedButtonImageName)) {
                    model.setTurn(ConnectFourModel.Slot.Red);   // shows the current turn red
                    view.setTurn(model.getTurn());              // tells the model the current turn
                } else if (buttonPressedIconString.contains(ConnectFourView.gameBlueButtonImageName)) {
                    model.setTurn(ConnectFourModel.Slot.Blue);  // shows the current turn
                    view.setTurn(model.getTurn());              // tells the model the current turn
                } else if (buttonPressedIconString.contains(ConnectFourView.customGameCheckStateImageName)) {
                    //if the current state is valid then it says Yay no error or else it will display the error messages that have occured
                    if (!model.checkBoardConfiguration()) view.setMessageText(model.getErrorMessage());
                    else view.setMessageText("Yay! No errors :D");
                }//end of the if else block inside the custom game
            }//end of the if else block
        }//end function

        //Purpose: this function handles all the mouse events that occur, such as you clicking the board
        public void handleCustomGameState(MouseEvent e){
            //get the mouse position of the click and then convert that to the board position where it will be in a different coordinate system
            Point mousePosition = new Point(e.getX(), e.getY());
            Point tilePosition = view.getBoardCoordinateOfPoint(mousePosition);

            //if the click is outside the board just end the function
            if(tilePosition.x >= model.getBoardConfiguration()[0].length || tilePosition.x < 0) return;
            if(tilePosition.y >= model.getBoardConfiguration().length || tilePosition.y < 0) return;    
            //x corresponds to the rows in the array and y corresponds to the columns

            //check if this configuration is possible by calling the model
            ConnectFourModel.Slot[][] newBoardConfiguration = model.getBoardConfiguration();
       
            //make that tile of type the players turn, but if that tile is already there then remove it
            if(newBoardConfiguration[tilePosition.y][tilePosition.x] == model.getTurn())newBoardConfiguration[tilePosition.y][tilePosition.x] = ConnectFourModel.Slot.Empty;
            else newBoardConfiguration[tilePosition.y][tilePosition.x] = model.getTurn();

            //adjust the board and update the switchScreen
            view.adjustBoard(newBoardConfiguration);
            view.switchScreen(model.getGameState());
        }

        //Purpose: this function will be called when the user presses a button, it will be responsible for handling the outcome of the button press.
        @Override
        public void mouseClicked(MouseEvent e) {

        	//if its in the main menu stop running the rest of the code
            if(model.getGameState() == ConnectFourModel.GameState.MainMenu) return;
            if(model.getGameState() == ConnectFourModel.GameState.CustomGame) { handleCustomGameState (e); return;}
            if(view.isAnimating()) return;

            Point mousePosition = new Point(e.getX(), e.getY());
            Point tilePosition = view.getBoardCoordinateOfPoint(mousePosition);
            //if the click is outside the board just end the function
            if(tilePosition.x >= model.getBoardConfiguration()[0].length || tilePosition.x < 0) return;
            if(tilePosition.y >= model.getBoardConfiguration().length || tilePosition.y < 0) return;
            //x corresponds to the rows in the array and y corresponds to the columns

            //make that tile of type the players turn, but if that tile is already there then remove it
            if (model.getGameProgess() != (ConnectFourModel.GameProgress.inProgress)) return;
            //adjust the game and update the switchScreen
            view.insertDisc(model.nextAvailableSlot(tilePosition), model.getTurn());
            if (model.getGameState() == ConnectFourModel.GameState.PvCPU) {
                view.setMessageText("vs CPU game is in progress...");
            } else {
                view.setMessageText("Game is in progress...");
            }
        }

        //all these functions were mandatory because if we do not have them then this class would be implementing an interface it does not have functions to
        @Override public void mouseReleased(MouseEvent e) {}//unused
        @Override public void mousePressed(MouseEvent e) {} //unused
        @Override public void mouseEntered(MouseEvent e) {}//unused
        @Override public void mouseExited(MouseEvent e) {}//unused
        @Override public void mouseDragged(MouseEvent e) {}//unused
        @Override public void mouseMoved(MouseEvent e) {}//unused
    }//end calculate listener class


}//end controller