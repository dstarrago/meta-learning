/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import javax.swing.border.EtchedBorder;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
//import javax.swing.jl


/**
 *
 * @author Danel
 */
public class StudioControl  extends JPanel {

  public Studio theApp;
  public JButton btnLoadData;
  public JButton btnSaveData;
  public JButton btnSaveGraph;
  public JButton btnExportIntervals;
  public JList measureList;

  public StudioControl(Studio theApp) {
    this.theApp = theApp;
    MouseHandler handler = new MouseHandler();    // Create the listener
    //addMouseListener(handler);               // Monitor mouse button presses
    EtchedBorder edge = new EtchedBorder(EtchedBorder.RAISED);  // Button border
    setBorder(edge);
    Dimension size = new Dimension(getWidth(), 120);
    setPreferredSize(size);
    GridLayout gridLayout = new GridLayout(0, 1);
    JPanel left = new JPanel();
    left.setLayout(gridLayout);
    left.setBorder(edge);
    btnLoadData = new JButton("Load Data");
    btnLoadData.addMouseListener(handler);               // Monitor mouse button presses
    left.add(btnLoadData);
    btnSaveData = new JButton("Save Data");
    btnSaveData.addMouseListener(handler);               // Monitor mouse button presses
    left.add(btnSaveData);
    btnSaveGraph = new JButton("Save Graph");
    btnSaveGraph.addMouseListener(handler);               // Monitor mouse button presses
    left.add(btnSaveGraph);
    btnExportIntervals = new JButton("Export Intervals");
    btnExportIntervals.addMouseListener(handler);               // Monitor mouse button presses
    left.add(btnExportIntervals);
    JPanel right = new JPanel();
    right.setLayout(gridLayout);
    right.setBorder(edge);
    measureList = new JList();
    measureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    measureList.addListSelectionListener(new ListSelectionHandler());
    JScrollPane spane = new JScrollPane(measureList);
    spane.setBorder(edge);
    right.add(spane);
    setLayout(new BorderLayout());
    add(left, BorderLayout.WEST);
    add(right, BorderLayout.CENTER);
  }

  public void setMeasures(String[] measureNames) {
    measureList.setListData(measureNames);
  }

  class ListSelectionHandler implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      theApp.getModel().selectMeasure(measureList.getSelectedIndex());
    }

  }

  class MouseHandler extends MouseInputAdapter {

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getComponent() == btnLoadData)
        theApp.loadModel(); else
      if (e.getComponent() == btnSaveData)
        theApp.saveModel(); else
      if (e.getComponent() == btnExportIntervals)
        theApp.saveIntervals();
    }

  }
}
