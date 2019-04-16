/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.util.Observer;
import java.util.Observable;
import java.awt.*;                                   // For Graphics
import java.awt.geom.*;
import weka.core.Instances;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 *
 * @author Danel
 */
public class StudioView extends JComponent implements Observer {

  private Instances data;
  private Studio theApp;
  private LandMark actualLM = null;
  private Marker marker1 = new Marker(0);
  private Marker marker2 = new Marker(0);
  private XAxis xAxis = new XAxis();
  private YAxis yAxis = new YAxis();
  private double dx;
  double scaleY;
  double baseY;
  boolean intervalStarted = false;
  boolean fixingInterval = false;
  double baseLine;
  private final int ledge = 5;
  private final int textSpace = 4;
  private int Attr1;
  private int Attr2;
  private double rightMargin = 40;
  private double leftMargin = 60;
  private double topMargin = 80;
  private double bottomMargin = 80;
  private final int INTERVAL_GAP = 4;
  private final int MIN_INTERVAL_WIDTH = 10;
  private final Color BackGrid = new Color(182, 168, 112);
  private final int arrowSize = 5;
  private ArrowLeft arrowLeft = new ArrowLeft(0, 0);
  private ArrowRight arrowRight = new ArrowRight(0, 0);
  private boolean Verbose = true;
  private float fontSize = 12f;
  private float lineWidth = 2.2f;
  private double maxOrdinate = 100;

  //MessageStr msg = new SpanishStr();
  MessageStr msg = new EnglishStr();

  public StudioView(Studio theApp) {
    this.theApp = theApp;
    MouseHandler handler = new MouseHandler();    // Create the listener
    addMouseListener(handler);               // Monitor mouse button presses
    addMouseMotionListener(handler);         // as well as movement
    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }

  // Method called by Observable object when it changes
  public void update(Observable o, Object rectangle) {
    // Code to respond to changes in the model...
    data = theApp.getModel().data;
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    if (data != null) {
      Attr1 = theApp.getModel().TrainAttr;
      Attr2 = theApp.getModel().TestAttr;
      fontSize = 7 + 0.015f * (float)getHeight();
      lineWidth = 1.1f * getWidth() / data.numInstances();    //1.2f
      rightMargin = getWidth() / 12;
      leftMargin = getWidth() /10;
      topMargin = getHeight() /6;
      bottomMargin = getHeight() /6;
      Graphics2D g2D = (Graphics2D)g;                // Get a Java 2D device context
      //g.setFont(g.getFont().deriveFont(fontSize));
      g.setFont(new Font("Arial", Font.PLAIN, (int)fontSize));
      FontMetrics fmtrs = g.getFontMetrics(g2D.getFont());
      baseY = getHeight() - bottomMargin;
      scaleY = (getHeight() - topMargin - bottomMargin) / maxOrdinate;
      dx = (getWidth() - leftMargin - rightMargin) / (double)data.numInstances();
      baseLine = topMargin + (getHeight() - topMargin - bottomMargin) * 0.55;
      g2D.setPaint(Color.WHITE);
      g2D.fillRect(0, 0, getWidth(), getHeight());

      xAxis.draw(g2D);
      yAxis.draw(g2D);
      g2D.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2D.setPaint(Color.BLACK);
      double x = leftMargin;
      Point2D.Double p1 = new Point2D.Double();
      p1.x = leftMargin;
      p1.y = baseY - data.instance(0).value(Attr1) * scaleY;
      for (int i = 1; i < data.numInstances(); i++) {
        Point2D.Double p2 = p1;
        p1 = new Point2D.Double();
        x += dx;
        p1.x = x;
        p1.y = baseY - data.instance(i).value(Attr1) * scaleY;
        g2D.draw(new Line2D.Double(p1, p2));
      }
      g2D.setColor(Color.lightGray);
      x = leftMargin;
      p1 = new Point2D.Double();
      p1.x = leftMargin;
      p1.y = baseY - data.instance(0).value(Attr2) * scaleY;
      for (int i = 1; i < data.numInstances(); i++) {
        Point2D.Double p2 = p1;
        p1 = new Point2D.Double();
        x += dx;
        p1.x = x;
        p1.y = baseY - data.instance(i).value(Attr2) * scaleY;
        g2D.draw(new Line2D.Double(p1, p2));
      }
      // Draw legend
      g2D.setPaint(Color.black);                   // Set the color
      int yLine = (int)(topMargin + (getHeight() - topMargin - bottomMargin) * 0.95);
      int xTraining = (int)2*getWidth()/7;
      int xTest = (int)4*getWidth()/7;
      g2D.drawString(msg.sTrainingAccuracy(), xTraining, yLine + fmtrs.getHeight() / 3);
      g2D.drawString(msg.sTestAccuracy(), xTest, yLine + fmtrs.getHeight() / 3);
      g2D.draw(new Line2D.Double(xTraining - 30, yLine, xTraining - 10, yLine));
      g2D.setColor(Color.lightGray);
      g2D.draw(new Line2D.Double(xTest - 30, yLine, xTest - 10, yLine));
      if (intervalStarted) {
        g2D.setPaint(Color.black);                   // Set the color
        g2D.setStroke(new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2D.draw(new Line2D.Double(marker1.x, baseLine, marker2.x, baseLine));
        marker1.draw(g2D);
        marker2.draw(g2D);
        int left, right, posx;
        if (marker1.x < marker2.x) {
          left = coordsToIndex(marker1.x);
          right = coordsToIndex(marker2.x);
          posx = (int)marker1.x + 10;
        } else {
          left = coordsToIndex(marker2.x);
          right = coordsToIndex(marker1.x);
          posx = (int)marker2.x + 10;
        }
        if (Verbose)
          drawIntervalStats(g2D, posx, Color.ORANGE, left, right);
      }
      // Draw the interval's marks and info
      g2D.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      for (int i = 0; i < theApp.getModel().getIntervalsCount(); i++) {
        double x1 = indexToCoords(theApp.getModel().intervalLeft(i));
        double x2 = indexToCoords(theApp.getModel().intervalRight(i));
        g2D.setPaint(Color.black);                   // Set the color
        g2D.draw(new Line2D.Double(x1, baseLine, x2, baseLine));  //////
        int intR = theApp.getModel().intervalRight(i) - 1;
        if (intR < 0) intR = 0;
        String s1 = String.format("%.3f", theApp.getModel().measureVal(theApp.getModel().intervalLeft(i)));
        String s2 = String.format("%.3f", theApp.getModel().measureVal(intR));
        g2D.drawString(s1, (int)x1 + 10, (int)topMargin - 10);
        g2D.drawString(s2, (int)x2 - 10 - fmtrs.stringWidth(s2), (int)topMargin - 10);
        
        g2D.setPaint(Color.red);                   // Set the color
        g2D.draw(new Line2D.Double(x1, topMargin - ledge, x1, getHeight() - bottomMargin + ledge));
        g2D.draw(new Line2D.Double(x2, topMargin - ledge, x2, getHeight() - bottomMargin + ledge));
        if (Verbose)
          drawIntervalStats(g2D, (int)x1 + 15, Color.red, theApp.getModel().intervalLeft(i), theApp.getModel().intervalRight(i));
        // Draw the arrows
        g2D.setPaint(Color.black);                   // Set the color
        g2D.fill(arrowLeft.atLocation((float)x1 + 1, (float)baseLine));  // En x incremento el grosor de la linea
        g2D.fill(arrowRight.atLocation((float)x2, (float)baseLine));
        
      }
      // Draw Graph Title
      if (theApp.getModel().measureSelected()) {
        //g.setFont(g.getFont().deriveFont(24f));
        g2D.setPaint(Color.black);                   // Set the color
        FontMetrics fm = g.getFontMetrics(g2D.getFont());
        String s = theApp.getModel().selectedMeasure.name;
        int posx = (getWidth() - fm.stringWidth(s)) / 2;
        g2D.drawString(s, posx, (int)topMargin / 2);
        //g2D.drawString(g2D.getFont().getFontName(), posx, (int)topMargin / 2 + 20);
      }
    }
  }

  private int coordsToIndex(double coord) {
    if (theApp.getModel().data == null) return 0;
    int ind = (int)((coord - leftMargin) / dx);
    if (ind < 0) ind = 0; else
    if (ind > theApp.getModel().data.numInstances()) ind = theApp.getModel().data.numInstances();
    return ind;
  }

  private double indexToCoords(int index) {
   return leftMargin + index * dx;
  }

  private LandMark markAround(int d) {
    LandMark lm = null;
    int numIntervals = theApp.getModel().getIntervalsCount();
    for (int i = 0; i < numIntervals; i++) {
      Interval in = theApp.getModel().getInterval(i);
      if (Math.abs(in.getLeft()-d) < INTERVAL_GAP)
        lm = in.left; else
      if (Math.abs(in.getRight()-d) < INTERVAL_GAP)
        lm = in.right;
    }
    return lm;
  }

  private void drawIntervalStats(Graphics2D g2D, int xpos, Color c, int left, int right) {
        double tam = theApp.getModel().getTestAccuracyMeanAtInterval(left, right);
        double dif = theApp.getModel().getTestAccuracyMean() - tam;
        double sup = maxOrdinate * (right - left) / theApp.getModel().data.numInstances();
        g2D.setPaint(c);                   // Set the color
        //String s = String.format("test accuracy  %.3f    test difference  %.3f", tam, dif);
        String s = String.format("Acc %.3f    dif %.3f", tam, dif);
        g2D.drawString(s, xpos, (int)baseLine - textSpace);
        //s = String.format("data sets percent  %.3f", sup);
        s = String.format("sup %.3f", sup);
        g2D.drawString(s, xpos, (int)baseLine + 4 * textSpace);
  }

class MouseHandler extends MouseInputAdapter {

    @Override
  public void mousePressed(MouseEvent e) {
      if (fixingInterval) {
        Interval interval = actualLM.getInterval();
        if (Math.abs(interval.getLeft() - interval.getRight()) < MIN_INTERVAL_WIDTH) {
          theApp.getModel().removeInterval(interval);
          actualLM = null;
          repaint();
        }
        fixingInterval = false;
      }      else {
        if (!intervalStarted) {
          // there is not any mark
          if (actualLM == null) {
            // beginning new interval
            marker1.x = e.getX();
            marker2.x = marker1.x;
            intervalStarted = true;
          } else {
            // fixing an interval
            fixingInterval = true;
          }
        } else {
          // there is already one mark
          if (Math.abs(marker1.x - e.getX()) < 10) {
            // moving the existing mark
            //marker1.x = e.getX();
            marker1.x = 0;
            marker2.x = 0;
            intervalStarted = false;
          } else {
            // complete the interval
            if (e.getX() > marker1.x)
              theApp.getModel().addInterval(coordsToIndex(marker1.x), coordsToIndex(e.getX()));
            else
              theApp.getModel().addInterval(coordsToIndex(e.getX()), coordsToIndex(marker1.x));
            marker1.x = 0;
            marker2.x = 0;
            intervalStarted = false;
          }
        }
      repaint();                             // Redraw pane contents
      }
        
  }

    @Override
  public void mouseDragged(MouseEvent e) {
    //marker1.x = e.getX();
    //repaint();                             // Redraw pane contents
  }

    @Override
  public void mouseMoved(MouseEvent e) {
    if (fixingInterval) {
      actualLM.setValue(coordsToIndex(e.getX()));
      repaint();
    } else {
      if (intervalStarted) {
        marker2.x = e.getX();
        repaint();
      }
      else {
        actualLM = markAround(coordsToIndex(e.getX()));
        if (actualLM == null)
          setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); else
          setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
      }
    }
  }
}

  class Marker {

    public Marker(double x)  {
      this.x = x;
    }

    public void draw(Graphics2D g2D) {
      if (x > leftMargin) {
        g2D.setPaint(Color.ORANGE);                   // Set the color
        g2D.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2D.draw(new Line2D.Double(x, topMargin - ledge, x, getHeight() - bottomMargin + ledge));
        //int i = (int)((x - leftMargin) / dx);
        //g2D.drawString(String.valueOf(i), (int)x + 15, (int)baseLine - 2);
      }
    }

    public double x;

  }

  class YAxis {

    private final double stickLength = 2;

    public void draw(Graphics2D g2D) {
      g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      FontMetrics fm = getFontMetrics(g2D.getFont());
      double inc = maxOrdinate / 10;
      for (double i = inc; i <= maxOrdinate; i += inc) {
        double y = baseY - (double)(i * scaleY);
        g2D.setPaint(BackGrid);
        // Set the color
        g2D.draw(new Line2D.Double(leftMargin, y, getWidth() - rightMargin, y));
        g2D.setPaint(Color.BLACK);                   // Set the color
        //String s = String.valueOf(i);
        String s = String.format("%.1f", i);
        g2D.drawString(s, (int)(leftMargin - 10 - fm.stringWidth(s)), (int)(y + 5));
        g2D.draw(new Line2D.Double(leftMargin, y, leftMargin - stickLength, y));
      }
      g2D.setPaint(BackGrid);                   // Set the color
      g2D.draw(new Line2D.Double(getWidth() - rightMargin, topMargin, getWidth() - rightMargin, getHeight() - bottomMargin));
      g2D.setPaint(Color.black);                   // Set the color
      g2D.draw(new Line2D.Double(leftMargin, topMargin, leftMargin, getHeight() - bottomMargin));
      AffineTransform at1 = g2D.getTransform();
      AffineTransform at2 = g2D.getTransform();
      int posY = (int)(topMargin + (getHeight() - topMargin - bottomMargin) * 0.45 + fm.stringWidth(msg.sAxisYLabel()) / 2)  ;
      at1.rotate(-Math.PI/2, (int)(leftMargin / 3), posY);
      g2D.setTransform(at1);
      g2D.drawString(msg.sAxisYLabel(), (int)(leftMargin / 3) , posY);
      g2D.setTransform(at2);
    }

  }

  class XAxis {

    private final double stickLength = 2;

    public void draw(Graphics2D g2D) {
      g2D.setPaint(Color.black);                   // Set the color
      g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      g2D.draw(new Line2D.Double(leftMargin, getHeight() - bottomMargin, getWidth() - rightMargin, getHeight() - bottomMargin));
      FontMetrics fm = g2D.getFontMetrics(g2D.getFont());
      String s;
      if (Verbose)
        s = String.format("%s   ( %s: %.3f )", msg.sAxisXLabel(), msg.sAxisXLabelDetail(), theApp.getModel().getTestAccuracyMean());
      else
        s = msg.sAxisXLabel();
      int posx = (getWidth() - fm.stringWidth(s)) / 2;
      g2D.drawString(s, posx, (int)(getHeight() - bottomMargin + 2.5 * fm.getHeight()));
      for (int i = 50; i < data.numInstances(); i += 50) {
        double x = leftMargin + dx * i;
        g2D.draw( new Line2D.Double(x, getHeight() - bottomMargin + stickLength, x, getHeight() - bottomMargin - stickLength));
        g2D.drawString(String.valueOf(i), (int)(x - 10), (int)(getHeight() - bottomMargin + fm.getHeight()));
      }
    }
  }

  abstract class Arrow {

  protected Point2D.Float start;                           // Start point for star
  protected GeneralPath p;                                 // Star path

  public Arrow(float x, float y) {
    start = new Point2D.Float(x, y);                      // store start point
    createArrow();
  }

  abstract protected void createArrow();

  Shape atLocation(float x, float y) {
    start.setLocation(x, y);                              // Store new start
    p.reset();                                            // Erase current path
    createArrow();                                         // create new path
    return p;                                             // Return the path
  }

}

  class ArrowLeft extends Arrow {

    public ArrowLeft(float x, float y) {
      super(x, y);
    }

  protected void createArrow() {
    Point2D.Float point = start;
    p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    p.moveTo(point.x, point.y);
    p.lineTo(point.x + 2 * arrowSize, point.y + arrowSize);
    p.lineTo(point.x + 2 * arrowSize, point.y - arrowSize);
    p.closePath();
  }

  }

  class ArrowRight extends Arrow {

    public ArrowRight(float x, float y) {
      super(x, y);
    }
    
  protected void createArrow() {
    Point2D.Float point = start;
    p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    p.moveTo(point.x, point.y);
    p.lineTo(point.x - 2 * arrowSize, point.y - arrowSize);
    p.lineTo(point.x - 2 * arrowSize, point.y + arrowSize);
    p.closePath();
  }

  }

}
