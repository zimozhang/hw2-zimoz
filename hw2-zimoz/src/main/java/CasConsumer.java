import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import types.ResultTag;


public class CasConsumer extends CasConsumer_ImplBase implements CasObjectProcessor {
  
  /**
   * This class is to get the CAS from annotation.
   * Last process of name recognition process.
   * The main duty for this casConsumer is to collect information from 2 parts:
   * First, receives sentence ID and the original sentences from collectionReader; 
   * SSecond, finds out the start point and end point of the Gene Name from Annotator.
   * 
   * Also, retrieves the name entities according to the positions provided by the AE engine,
   * Counts the white blanks, deletes these spaces and prints out these information to the output file in required format. 
   * In this case: ID |position| gene name .
   * 
   * Calculates the precision and recall in the end of the class.
   * 
   * @author zimo
   */
  
  File outFile;
  File compareFile;
  FileWriter fileWriter;
  FileWriter compareWriter;
  
  BufferedReader br=null;
  BufferedWriter bw;
  String linenumber=null; 
  int samplenumber;
  int correct;//the right number of geneTag
  int totalrecognitions;
  private HashSet<String> s;
  
  public CasConsumer() {
  }
  
  // extract configuration parameter settings
//  public void initialize() throws ResourceInitializationException{
  public void initialize() throws ResourceInitializationException {
//    super.initialize(aUimaContext);
    String oPath = (String) getUimaContext().getConfigParameterValue("outputdocument");
//    String samplefile = (String) getUimaContext().getConfigParameterValue("SAMPLE_FILE");
//    String samplefile = (String) getUimaContext().getConfigParameterValue("SAMPLE_FILE");
 // Output file should be specified in the descriptor
    if (oPath == null) {
      throw new ResourceInitializationException(
              ResourceInitializationException.CONFIG_SETTING_ABSENT, new Object[] { "outputdocument" });
    }
    
    try {
//      outFile = new File("outputdocument");
      outFile = new File("/home/zimo/git/hw2-zimoz/hw2-zimoz/src/main/resources/hw1-zimoz.out");
      fileWriter = new FileWriter(outFile);
//      fileWriter = new FileWriter(compareFile);
    } catch (IOException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }
    
    
/**
 * Sets a Hashset to record the number of sample in standard output.
 * For comparing the data I got with the data in standard output later.
 */
    s = new HashSet<String>();
    
    try {
      InputStream is = LingPipeAnnotator.class.getClassLoader().getResourceAsStream("sample.out");
      
      br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      while ((linenumber = br.readLine()) != null) {
        s.add(linenumber);
        samplenumber++;
        //System.out.println(s);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

/**
 * Uses a new file to record the data for comparing    
 */
//     compareFile = new File("compare.out");
     try {
//       compareWriter = new FileWriter(compareFile);
      bw = new BufferedWriter(new FileWriter("compare.out"));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }       
    
  }
    
  public synchronized void processCas(CAS aCAS) throws ResourceProcessException {
   
/**
 * Get the sentence ID and the original sentences from collectionReader
 * Get the start and end point of each gene
 */
     JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    
    
 /**
  * Iterates and prints annotations.
  * Counts the correct and total number of recognitions in the meanwhile.
  */
    Iterator annotationIter = jcas.getAnnotationIndex(ResultTag.type).iterator();
    while (annotationIter.hasNext()) {
      ResultTag r= (ResultTag) annotationIter.next();
//      String aText = r.getCoveredText();
      totalrecognitions++;
      String info=r.getText();
//      System.out.println(r.getID());
//    String info=r.getID()+"|"+r.getStart()+" "+r.getEnd()+"|"+r.getText();
      try {
        fileWriter.write(r.getText() + '\n');
//        System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        fileWriter.flush();
         if(s.contains(info)){
           correct++;
      }
        bw.write(info);
        bw.newLine();
        bw.flush();
      } catch (IOException e) {
        throw new ResourceProcessException(e);
      }    
//      }
   
//      try {
//          br.close();
//      } catch (IOException ignoreMe){
//     // TODO Auto-generated catch block
//        ignoreMe.printStackTrace();
      }  

    } 
      
  @Override
  public void destroy() {
 /**
  * Calculates the precision and recall for evaluating the performance of the Annotators  
  */
      try {
        if (bw != null) {
          Double precision=(Double) (correct*1.0/totalrecognitions*1.0);
          Double recall=(Double)(correct*1.0/samplenumber*1.0);
          Double F=(Double) 2.0* precision*recall/(precision+recall);
          System.out.println("precision="+precision);
          System.out.println("recall="+recall);
          System.out.println("F-measure="+F);
          System.out.println("correct="+correct);
          System.out.println("total recognizations="+totalrecognitions);
          bw.close();
          bw = null;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (fileWriter != null) {
      try {
        fileWriter.close();
      } catch (IOException e) {
        // ignore IOException on destroy
      }
    
      }
  } 

}