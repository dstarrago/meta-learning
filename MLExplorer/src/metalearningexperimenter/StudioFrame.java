/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import javax.swing.JFrame;

/**
 *
 * @author Danel
 */
public class StudioFrame  extends JFrame {

  private Studio theApp;

  public StudioFrame(String Title, Studio theApp) {
    this.theApp = theApp;
    setTitle(Title);                            // Set the window title
    setDefaultCloseOperation(EXIT_ON_CLOSE);    // Default is exit the application
  }

}
