import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.io.*;

import org.apache.uima.UIMARuntimeException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import types.GeneTag;
import types.SentenceTag;


import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;




/**
 * This is the second Annotator.
 * GeneNameAnnotator refers to ne-en-bio-genetag.HmmChunker for gene mention tagging.
 * Utilizes the linpipe methods and its's training results.
 * @see http://alias-i.com/lingpipe/demos/tutorial/ne/read-me.html
 * 
 * @author zimo
 */
public class LingPipeAnnotator extends JCasAnnotator_ImplBase {
 
  BufferedReader br = null;
  BufferedWriter bw;
  String line = null;
  int correct;// the right number of geneTag
  int totalrecognitions;
  int samplenumber;
  private HashSet<String> hs;
  
//private final static String lingpipeset = "src/main/resources/data/ne-en-bio-genetag.HmmChunker";
  
//  private static String modelFile;
//  modelFile=(String)LingPipeAnnotator(ModelFile);
  public static final String model = "ModelFile";
  File cFile = new File(model);
  //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@"+cFile.getName());
  //mistake here
/**
 * Documents the confidence of each result.
 */
  ConfidenceChunker chunkers;
  
   public void initialize(UimaContext aUimaContext) throws ResourceInitializationException {
     super.initialize(aUimaContext);
     try {
      chunkers = (ConfidenceChunker) AbstractExternalizable.readResourceObject(LingPipeAnnotator.class, (String) aUimaContext.getConfigParameterValue("ModelFile"));
    } catch (ClassNotFoundException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    } catch (IOException e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    } 
      
      
      hs = new HashSet<String>();

      try {
        InputStream is = LingPipeAnnotator.class.getClassLoader().getResourceAsStream("sample.out");
        
        br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        String ln;
        while ((ln = br.readLine()) != null) {
          hs.add(ln);
          samplenumber++;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        
        bw = new BufferedWriter(new FileWriter("compare.out"));
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
/**
 * Because of the requirement of the format, 
 * I use a function to count the number of white blanks in the sentences,
 * and reduce these later in defining the start and end point of each gene.  
 * @param phrase
 * @return
 */
   private int countBlanks(String phrase) {
     int count = 0;
     for (int i = 0; i < phrase.length(); i++) {
       if (phrase.charAt(i) == ' ') {
         count++;
       }
     }
     return count;
   }   

   /**
    * Deals with the text in order to extract geneName from each sentence in this Annotator.
    * Gets the start point from the number of first character of mention.
    * Gets the end point from the number of the last character of the mention.
    * Minus the white spaces characters in each sentence to implement these.
    */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub

    FSIterator<Annotation> it = aJCas.getAnnotationIndex(SentenceTag.type).iterator();
    while (it.hasNext()) {
     
     SentenceTag s = (SentenceTag) it.next();
    
     String sIdentify = s.getID();
     
     String sText = s.getText();
//     System.out.println(sText);
     Chunking chunking = ((Chunker) chunkers).chunk(sText);
     Chunk[] carray = chunking.chunkSet().toArray(new Chunk[0]);
     
     
     for (int i = 0; i < carray.length; i++) {
      
      GeneTag g = new GeneTag(aJCas);
      totalrecognitions++;
      g.setID(sIdentify);
      g.setBegin(carray[i].start());
      g.setEnd(carray[i].end());
     
      String a = sText.substring(carray[i].start(), carray[i].end());
      String b = sText.substring(0,carray[i].start());
      int m = countBlanks(a);
      int n = countBlanks(b);
      
      
     g.setBegin(carray[i].start()-n);
     g.setEnd(carray[i].end()-1-n-m);
     g.setText(sText.substring(carray[i].start(), carray[i].end()));
     g.setCasProcessorId("1");
     
     String info = g.getID() + "|" + g.getStart() + " " + g.getEnd() + "|" + g.getText();

     if (hs.contains(info)) {
       correct++;
     }

     try {

       bw.write(info);
       bw.newLine();
       bw.flush();
     } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
     }
     
     g.addToIndexes();
    }
     }
   
     }
  public void destroy() {
    /**
     * Calculates the precision and recall for evaluating the performance of the Annotators
     */
    if (bw != null) {
      Double precision = (Double) (correct * 1.0 / totalrecognitions * 1.0);
      Double recall=(Double)(correct*1.0/samplenumber*1.0);
      Double F=(Double) 2.0* precision*recall/(precision+recall);
      System.out.println("########## This is LingPipedAnnotator ############");
      System.out.println("precision="+precision);
      System.out.println("recall="+recall);
      System.out.println("F-measure="+F);
      System.out.println();
      try {
        bw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      bw = null;
    }   
  }
}