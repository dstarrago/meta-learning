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
abstract public class Rule {

  static public int bhBad = 0;
  static public int bhGood = 1;

  private String ID;
  public int behavior;

  protected Set elements = null;

  public Rule(String id) {
    this.ID = id;
  }

  public Rule(String id, int behavior) {
    this.ID = id;
    this.behavior = behavior;
  }

  public String ID() {
    return ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public void setBehavior(int bh) {
    behavior = bh;
  }

  public int size() {
    return (elements == null)? 0 : elements.cardinal();
  }
  
  @Override
  public String toString() {
    return "";
  }

  abstract public Set execute(Instances data);

}
