/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.Serializable;
import weka.experiment.Stats;

/**
 *
 * @author Danel
 */
public class Interval implements Serializable {

    public LandMark left;
    public LandMark right;
    private MeasureCharacter mc;      // OJO con la serialización
    private transient Stats st = null;


    public Interval(MeasureCharacter mc, int left, int right) {
      this.mc = mc;
      this.left = new LandMark(this, left);
      this.right = new LandMark(this, right);
    }

    public int getLeft() {
      return left.getValue();
    }

    public int getRight() {
      return right.getValue();
    }

    public void setLeft(int value) {
      if (value != left.getValue()) {
        left.setValue(value);
        st = null;
      }
    }

    public void setRight(int value) {
      if (value != right.getValue()) {
        right.setValue(value);
        st = null;
      }
    }

    public MeasureCharacter getMeasureChar() {
      return mc;
    }
    
    public double getMean() {
      if (st == null) {
        st = new Stats();
        int r = getRight();
        int attr = mc.getAttrIndex();
        for (int i = getLeft(); i < r; i++)
          st.add(mc.getData().instance(i).value(attr));
        st.calculateDerived();
        return st.mean;
      } else
        return st.mean;
}

}
