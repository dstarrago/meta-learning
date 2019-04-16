/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.util.Iterator;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author danels
 */
public class Set {

class SetIterator<Instance> implements Iterator<Instance> {

  Set s;
  int index;

  public SetIterator(Set s) {
    this.s = s;
    index = 0;
  }

  public boolean hasNext() {
    return index < s.cardinal();
  }

  public Instance next() {
    return (Instance)s.elements.elementAt(index++);
  }

  public void remove() {
    s.elements.removeElementAt(index);
  }

}
  private FastVector elements;

  public Set(Instances data) {
    elements = new FastVector();
    for (int i = 0; i < data.numInstances(); i++)
      elements.addElement(data.instance(i));
  }

  public Set(Set s) {
    elements = (FastVector)s.elements.copy();
  }

  public Set() {
    elements = new FastVector();
  }

  public boolean contains(Instance e) {
    return elements.indexOf(e) != -1;
  }

  public int cardinal() {
    return elements.size();
  }

  public Iterator iterator() {
    return new SetIterator(this);
  }

  public void add(Instance e) {
    elements.addElement(e);
  }

  public void remove(Instance e) {
    elements.removeElementAt(elements.indexOf(e));  // there is no check for existence
  }
  
  public Instances toDataset() {
    if (elements == null || elements.size() == 0) return null;
    Instances r = new Instances(((Instance)elements.elementAt(0)).dataset(), elements.size());
    for (int i = 0; i < elements.size(); i++)
      r.add((Instance)elements.elementAt(i));
    return r;
  }
 
}


