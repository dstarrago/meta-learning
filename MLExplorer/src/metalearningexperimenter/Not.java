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
public class Not extends Rule {
  Rule A;

  public Not(Rule A) {
    super ("");
    this.A = A;
  }

  public Not(String name, Rule A) {
    super (name);
    this.A = A;
  }

  public Not(String name, int behavior, Rule A) {
    super (name, behavior);
    this.A = A;
  }

  public Set execute(Instances data) {
    Set universe = new Set(data);
    Complement o = new Complement(A.execute(data), universe);
    return elements = o.operate();
  }

  @Override
  public String toString() {
    String Alabel;
    if (A.ID() != null) Alabel = A.ID();
    else Alabel = A.toString();
    return "NOT (" + Alabel + ")";
  }


}
