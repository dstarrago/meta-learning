/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.io.*;
import weka.core.converters.ArffSaver;

/**
 *
 * @author Danel
 */
public class Studio {

  private StudioModel studio;                     // The data model for the sketch
  private StudioView view;                        // The view of the sketch
  private StudioControl control;
  private static StudioFrame window;              // The application window
  private static Studio theApp;                 // The application object
  //private static String sourceDir = "C:/Users/Danel/Documents/Investigación/SOFTCOMPUTING/Tesis/LAB/ANALYSIS";
  private static String sourceDir = "C:/Users/Danel/Documents/Investigación/RST Measures/";
  //private static String fileName = "/AllChar/Characterization";
  //private static String fileName = "/AllCharFixed/AllCharFixed";
  //private static String fileName = "/Feb09Kappa/Feb09AUC";Feb09AUC+IR greater than 1.5
  //private static String fileName = "Feb09AUC+IR lesser or equal than 1.5";
  private static String fileName = "Feb09AUC+IR";
  //private static String fileName = "/Feb09Kappa/Feb09AUC+IR";
  //private static String fileName = "/Feb09Fixed/Feb09Fixed";
  //private static String fileName = "/Roughness Size16Fixed/MeasuresRoughness";
  //private static String fileName = "/Roughness Size36Fixed/MeasuresRoughness";
  //private static String fileName = "/Feb10Fixed/Feb10Fixed";
  //private static String fileName = "/Feb09Fixed/Feb09Fixed indicadores";
  //private static String fileName = "/Feb09Fixed/Feb09Fixed";
  //private static String fileName = "/mezcla1/mezcla1";
  //private static String fileName = "/AllCharFixed/AllCharFixed indicadores";
  //private static String fileName = "/AllCharFixed/AllCharFixed descriptores";

  public static void main(String[] args) {
    theApp = new Studio();                      // Create the application object
    theApp.init();                                // ... and initialize it
    File dataFile = new File(sourceDir, fileName + ".arff");
    theApp.studio.loadFromFile(dataFile);
    theApp.control.setMeasures(theApp.studio.listAttrNames());
  }

  public void init() {
    window = new StudioFrame("Data Studio", this);    // Create the app window
    Toolkit theKit = window.getToolkit();          // Get the window toolkit
    Dimension wndSize = theKit.getScreenSize();    // Get screen size

    // Set the position to screen center & size to 1/2 screen size
    window.setBounds(wndSize.width/8, wndSize.height/8,        // Position
                     3* wndSize.width/4, 3 * wndSize.height/4);   // Size

    window.addWindowListener(new WindowHandler()); // Add window listener

    studio = new StudioModel();               // Create the model
    view = new StudioView(this);              // Create the view
    control = new StudioControl(this);
    studio.addObserver((Observer)view);       // Register the view with the model
    BorderLayout border = new BorderLayout(2, 2);        // Create a layout manager
    Container content = window.getContentPane();   // Get the content pane
    content.setLayout(border);                    // Set the container layout mgr
    content.add(view, BorderLayout.CENTER);
    content.add(control, BorderLayout.SOUTH);
    window.setVisible(true);
  }

  // Return a reference to the application window
  public StudioFrame getWindow() {
     return window;
  }

  // Return a reference to the model
  public StudioModel getModel() {
     return studio;
  }

  // Return a reference to the view
  public StudioView getView() {
     return view;
  }

  public void saveModel() {
    File theFile = new File(sourceDir, fileName + ".std");
    // Check out the file...

    // Create the object output stream for the file
    ObjectOutputStream objectOut = null;
    try {
      objectOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(theFile)));
      objectOut.writeObject(studio);
    } catch(IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    // Close the stream
    try {
       objectOut.close();                          // Close the output stream

    } catch(IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  public void loadModel() {
    File theFile = new File(sourceDir, fileName + ".std");
    // Perhaps check out the file...

    // Create the object output stream for the file
    ObjectInputStream objectIn = null;
    try {
      objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(theFile)));
      studio = (StudioModel)(objectIn.readObject());
      studio.addObserver((Observer)view);       // Register the view with the model
      studio.selectMeasure(0);
      //control.btnAccuracyTraining.doClick();
    } catch(ClassNotFoundException e) {
      e.printStackTrace(System.err);
      System.exit(1);

    } catch(IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
    // Close the stream
    try {
      objectIn.close();                          // Close the input stream

    } catch(IOException e) {
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  public void saveIntervals() {
    File rulesFile = new File(sourceDir, fileName + "_Intervals.arff");
    try {
      ArffSaver saver = new ArffSaver();
      saver.setFile(rulesFile);
      saver.setDestination(rulesFile);
      saver.setInstances(studio.getIntervals(fileName.substring(fileName.lastIndexOf("/") + 1)));
      saver.setRetrieval(ArffSaver.BATCH);
      saver.writeBatch();
      } catch (Exception e) {
           System.err.println(e.getMessage());
     }
  }

  // Handler class for window events
  class WindowHandler extends WindowAdapter {
    // Handler for window closing event
    public void windowClosing(WindowEvent e) {
      // Code to be added here later...
    }
  }

}