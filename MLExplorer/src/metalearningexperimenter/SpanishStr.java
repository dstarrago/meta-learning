/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

/**
 *
 * @author Danel
 */
public class SpanishStr implements MessageStr {

  public String sTrainingAccuracy() {
    return "Exactitud en entrenamiento";
  }

  public String sTestAccuracy() {
    return "Exactitud en prueba";
  }

  public String sAxisYLabel() {
    return "Exactitud (%)";
  }

  public String sAxisXLabel(){
    return "Conjuntos de Datos";
  }

  public String sAxisXLabelDetail(){
    return "exactitud media en prueba";
  }

  public String sQualClassif(){
    return "Calidad de la clasificación";
  }

  public String sAccClassif(){
    return "Exactitud de la clasificación";
  }

  public String sGenAccMemb(){
    return "Exactitud generalizada de la aproximación ponderada por membresía aproximada";
  }

  public String sGenPrecMemb(){
    return "Precisión generalizada de la aproximación ponderada por membresía aproximada";
  }

  public String sGenAccInv(){
    return "Exactitud generalizada de la aproximación ponderada por compromiso aproximado";
  }

  public String sGenPrecInv(){
    return "Precisión generalizada de la aproximación ponderada por compromiso aproximado";
  }

  public String sGenAccAgr(){
    return "Exactitud generalizada de la aproximación ponderada por concordancia aproximada";
  }

  public String sGenPrecAgr(){
    return "Precisión generalizada de la aproximación ponderada por concordancia aproximada";
  }

}
