import javax.swing.*;

public class ConnectFourMain {

    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ConnectFourModel theModel = new ConnectFourModel(6,7);
                ConnectFourView theView = new ConnectFourView(800,600);//, theModel.getBoardConfiguration(), theModel.getGameState());
                ConnectFourController theController = new ConnectFourController(theView,theModel);
                theView.setController(theController);
                theView.setVisible(true);
                theView.repaint();
            } 
        });
    }//end main function

}//end class
