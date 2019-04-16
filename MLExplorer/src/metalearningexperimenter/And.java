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
public class And extends Rule {
  Rule A, B;

  public And(Rule A, Rule B) {
    super ("");
    this.A = A;
    this.B = B;
  }

  public And(String name, Rule A, Rule B) {
    super (name);
    this.A = A;
    this.B = B;
  }

  public And(String name, int behavior, Rule A, Rule B) {
    super (name, behavior);
    this.A = A;
    this.B = B;
  }

  public Set execute(Instances data) {
    Intersection o = new Intersection(A.execute(data), B.execute(data));
    return elements = o.operate();
  }

  @Override
  public String toString() {
    String Alabel, Blabel;
    if (A.ID() != null) Alabel = A.ID();
    else Alabel = A.toString();
    if (B.ID() != null) Blabel = B.ID();
    else Blabel = B.toString();
    return "(" + Alabel + " AND " + Blabel + ")";
  }


}
