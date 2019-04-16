/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import weka.core.Instances;

/**
 *
 * @author Danel
 */
public class SimpleRule extends Rule {

  public String measure;
  public double begin;
  public double end;

  public SimpleRule(String id, String measure, double begin, double end, int behavior) {
    super (id, behavior);
    this.measure = measure;
    this.begin = begin;
    this.end = end;
  }
  
  @Override
  public String toString() {
    String s = new String();
    if (begin > 0 && end < 1)
      s = String.valueOf(begin) + " < ";
    s = s + measure;
    if (begin > 0 && end == 1)
      s = s + " > " + String.valueOf(begin);
    if (end < 1)
      s = s + " < " + String.valueOf(end);
    //s = s + " then " + behavior + " behavior";
    return s;
  }

  public Set execute(Instances data) {
    elements = new Set();
    for (int i = 0; i < data.numInstances(); i++) {
      double val = data.instance(i).value(data.attribute(measure));
      if (val >= begin && val <= end)
        elements.add(data.instance(i));
    }
    return elements;
  }

}
