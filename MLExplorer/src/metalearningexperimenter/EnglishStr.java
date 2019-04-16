/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

/**
 *
 * @author Danel
 */
public class EnglishStr implements MessageStr {

  public String sTrainingAccuracy() {
    return "Training Accuracy";
  }

  public String sTestAccuracy() {
    return "Test Accuracy";
  }

  public String sAxisYLabel() {
    return "Accuracy";
  }

  public String sAxisXLabel(){
    return "Data set";
  }

  public String sAxisXLabelDetail(){
    return "overall test Accuracy";
  }

  public String sQualClassif(){
    return "Quality of classification";
  }

  public String sAccClassif(){
    return "Accuracy of classification";
  }

  public String sGenAccMemb(){
    return "Generalized accuracy of approximation weighed by rough membership";
  }

  public String sGenPrecMemb(){
    return "Generalized precision of approximation weighed by rough membership";
  }

  public String sGenAccInv(){
    return "Generalized accuracy of approximation weighed by rough involvement";
  }

  public String sGenPrecInv(){
    return "Generalized precision of approximation weighed by rough involvement";
  }

  public String sGenAccAgr(){
    return "Generalized accuracy of approximation weighed by rough agreement";
  }

  public String sGenPrecAgr(){
    return "Generalized precision of approximation weighed by rough agreement";
  }

}
