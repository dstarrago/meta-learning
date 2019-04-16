/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.FastVector;
import weka.core.Instances;
import java.io.Serializable;

/**
 *
 * @author Danel
 */
public class MeasureCharacter implements Serializable {

  public String name;
  private FastVector intervals = new FastVector();
  private Instances data;    // OJO con la serialización
  private int attrIndex;

  public MeasureCharacter(Instances data, int attrIndex) {
    this.data = data;
    this.attrIndex = attrIndex;
  }

  public MeasureCharacter(String name, Instances data, int attrIndex) {
    this.name = name;
    this.data = data;
    this.attrIndex = attrIndex;
  }

  public String getAttrName() {
    return data.attribute(attrIndex).name();
  }

  public int getIntervalsCount() {
    return intervals.size();
  }

  public void addInterval(int left, int right) {
    intervals.addElement( new Interval(this, left, right));
  }

  public void removeInterval(int index) {
    intervals.removeElementAt(index);
  }

  public void removeInterval(Interval interval) {
    int index = intervals.indexOf(interval);
    intervals.removeElementAt(index);
  }

  public int getAttrIndex() {
    return attrIndex;
  }

  public Interval getInterval(int index) {
    Interval m = (Interval)intervals.elementAt(index);
    return m;
  }

  public Instances getData() {
    return data;
  }

  public double getMean() {
    return data.meanOrMode(attrIndex);
  }

}
