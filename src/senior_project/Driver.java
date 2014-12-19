///////////////////////////////////////////////////////////
//
// Senior Project: Integration Application
// Written By: Tyler Estro
// SUNY Farmingdale (Fall 2014)
//
// File Name: Driver.java
// Purpose: Includes main. Instantiates the main window
//			of the application.
//
///////////////////////////////////////////////////////////

package senior_project;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Driver {

	public static void main(String[] args) {
	    try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    	// handle exception
	    }
	    catch (ClassNotFoundException e) {
	    	// handle exception
	    }
	    catch (InstantiationException e) {
	    	// handle exception
	    }
	    catch (IllegalAccessException e) {
	    	// handle exception
	    }		
		Window mainWindow;
		mainWindow = new Window();
		mainWindow.setVisible(true);
	}

}
