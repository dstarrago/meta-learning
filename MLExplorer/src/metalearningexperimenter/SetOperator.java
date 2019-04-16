/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.util.Iterator;
import weka.core.Instance;



/**
 *
 * @author danels
 */
abstract class SetOperator {
  Set result;

  abstract public Set operate();
}
class Complement extends SetOperator {
  Set A, U;

  public Complement(Set A, Set U) {
    this.A = A;
    this.U = U;
  }

  @Override 
  public Set operate() {
    result = new Set();
    Iterator itr = U.iterator();
    while (itr.hasNext()) {
      Instance e = (Instance)itr.next();
      if (!A.contains(e))
        result.add(e);
    }
    return result;
  }
}

class Union extends SetOperator {
  Set A, B;

  public Union(Set A, Set B) {
    this.A = A;
    this.B = B;
  }

  @Override
  public Set operate() {
    result = new Set(A);
    Iterator itr = B.iterator();
    while (itr.hasNext()) {
      Instance e = (Instance)itr.next();
      if (!A.contains(e))
        result.add(e);
    }
    return result;
  }
}

class Intersection extends SetOperator {
  Set A, B;

  public Intersection(Set A, Set B) {
    this.A = A;
    this.B = B;
  }

  @Override
  public Set operate() {
    result = new Set();
    Iterator itr = B.iterator();
    while (itr.hasNext()) {
      Instance e = (Instance)itr.next();
      if (A.contains(e))
        result.add(e);
    }
    return result;
  }
}

