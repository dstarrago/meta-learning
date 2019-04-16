/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;


import java.io.Serializable;

/**
 *
 * @author Danel
 */
public class LandMark implements Serializable {

    private int value;
    private Interval interval;       

    public LandMark(Interval interval, int value) {
      this.interval = interval;
      setValue(value);
    }

    public Interval getInterval() {
      return interval;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      if (value > interval.getMeasureChar().getData().numInstances())
        this.value = interval.getMeasureChar().getData().numInstances();
      else
        if (value < 0)
          this.value = 0;
        else
          this.value = value;

    }

}
