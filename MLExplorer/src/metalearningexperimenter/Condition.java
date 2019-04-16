/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

/**
 *
 * @author danels
 */
public class Condition {
  /**
   * Relational operators
   */
  public static final int roGreaterThan = 1;
  public static final int roLesserThan = 2;
  public static final int roGreaterEqualThan = 3;
  public static final int roLesserEqualThan = 4;

  String attribute;
  int relation;
  double value;

  public Condition(String attribute, int relation, double value) {
    this.attribute = attribute;
    this.relation = relation;
    this.value = value;
  }

  public boolean satisfyWith(double aVal) {
    switch (relation) {
      case roGreaterThan:       return (aVal > value);
      case roLesserThan:        return (aVal < value);
      case roGreaterEqualThan:  return (aVal >= value);
      case roLesserEqualThan:   return (aVal <= value);
      default: return false;
    }
  }

  public String print() {
    String s = attribute;
    switch (relation) {
      case roGreaterThan:       s += " > "; break;
      case roLesserThan:        s += " < "; break;
      case roGreaterEqualThan:  s += " >= "; break;
      case roLesserEqualThan:   s += " <= "; break;
    }
    return s + String.valueOf(value);
  }
  
}
