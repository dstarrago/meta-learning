/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package metalearningexperimenter;

import java.io.*;
import weka.core.Instances;
import java.util.Random;
import java.util.Arrays;


  /**
 *
 * @author Danel
 */
public class Prueba {

  public static String DataPath = "C:/Documents and Settings/Danel/My Documents/Docs/SOFTCOMPUTING/Tesis/LAB/DATA/Feb09/ecoli-0-6_vs_1-3";

  /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
      File baseDatasetFile = new File(DataPath, "ecoli-0-6_vs_1-3.dat");
      Instances baseDS = DataCharacterizer.getInstances(baseDatasetFile);
      Measures measures = new Measures();
      double[] characterPerformancesVector = measures.extractMetaAttributes(baseDS);
      System.out.println(Arrays.toString(characterPerformancesVector));
      for (int i = 0; i < 5; i++) {
        baseDS.randomize(new Random());
        characterPerformancesVector = measures.extractMetaAttributes(baseDS);
        System.out.println(Arrays.toString(characterPerformancesVector));
      }
    }
}
