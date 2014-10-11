import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import types.GeneTag;
import types.SentenceTag;
import abner.Tagger;
  
/**
 * This is the third annotator I wrote.
 * Utilizes ABNER which is a software tool for molecular biology text analysis.
 * Utilizes Class Tagger which is the interface to the CRF that does named entity tagging.
 * The BIOCREATIVE I used is trained on the BioCreative corpus, just fit my needs.
 * ABNER's core is a statistical machine learning system,
 * uses linear-chain conditional random fields (CRFs) with a variety of orthographic and contextual features.
 */
public class ABNERAnnotator extends JCasAnnotator_ImplBase {

  private File output = null;
  
  BufferedReader br = null;
  BufferedWriter bw;
  String line = null;

  int correct;// the right number of geneTag
  int samplenumber;
  int totalrecognitions;

  private HashSet<String> hs;
  
  Tagger ta = new Tagger(Tagger.BIOCREATIVE);
 
  public void initialize(UimaContext aUimaContext) throws ResourceInitializationException {
    super.initialize(aUimaContext);

    hs = new HashSet<String>();

    try {
      InputStream is = LingPipeAnnotator.class.getClassLoader().getResourceAsStream("sample.out");
      
      br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      
      while ((line = br.readLine()) != null) {
        hs.add(line);
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
  
  public ABNERAnnotator() {
    // TODO Auto-generated constructor stub
    
  }
 /**
  * Utilizes this function to count white blanks in sentence.
  * make sure I find the exactly right position of gene.  
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

  
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
/**
 * Iterators all sentences and finds out the gene name and position.
 */
    FSIterator<Annotation> it = aJCas.getAnnotationIndex(SentenceTag.type).iterator();
    
    
    while (it.hasNext()) {
      
      SentenceTag s = (SentenceTag) it.next();
     
      String sIdentify = s.getID();
/**
 * Take an input string and return a Vector of 2D String arrays      
 */      
      String sText = s.getText();
//      System.out.println(sText);
      String[][] result = ta.getEntities(sText);

      for (int i = 0; i < result[0].length; i++) {
        
        GeneTag g = new GeneTag(aJCas);
        
        g.setID(sIdentify);
        int start=sText.indexOf(result[0][i]);
        if(start==-1){
          break;
        }
/**
 * Considers the number of blanks to calculate the start and end point of each gene.       
 */
        totalrecognitions++;
        int a=countBlanks(sText.substring(0, start));
        int b=countBlanks(result[0][i])+1;
        g.setBegin(start-a);
        g.setEnd(start+result[0][i].length()-a-b);
        g.setText(result[0][i]);
        g.setCasProcessorId("3");
        
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
      System.out.println("########## This is ABNERAnnotator ############");
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