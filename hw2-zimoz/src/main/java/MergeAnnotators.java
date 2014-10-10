import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import types.geneTag;
import types.resultTag;

/**
 * This annotator is for merging the results we got from the three different annotators before.
 * We need to design a algorithm to decide which result to output.
 * Counts the numbers of gene name frequency in these three annotators,
 * and chooses the ones that are considered as gene for more than two annotators.
 * Utilizes a hashmap to store the results.
 * @author zimo
 *
 */

public class MergeAnnotators extends JCasAnnotator_ImplBase {

  File outFile;
  FileWriter fileWriter;
  
  BufferedReader br=null;
  BufferedWriter bw;
  String info=null; 
  private HashMap<String, Integer> finalresult;
  
  public MergeAnnotators() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
//    Iterator annotationIter = aJCas.getAnnotationIndex(geneTag.type).iterator();
//    geneTag g= new geneTag(aJCas);
//    String Identify = g.getCasProcessorId();   
//    if(Identify=="1"){     
//    }
    
    Iterator annotationIter = aJCas.getAnnotationIndex(geneTag.type).iterator();
    finalresult=new HashMap<String, Integer>();
    while (annotationIter.hasNext()) {
      geneTag gt= (geneTag) annotationIter.next();
      String info=gt.getID()+"|"+gt.getStart()+" "+gt.getEnd()+"|"+gt.getText();
      if(finalresult.containsKey(info)){
        finalresult.put(info, finalresult.get(info)+1);
      }
      else{
        finalresult.put(info, 1);
      }
    }
    
    for(String key: finalresult.keySet())
    {
      if(finalresult.get(key)>=2)
      {
        resultTag t=new resultTag(aJCas);
          t.setText(key);
          t.addToIndexes();
              
      }
      else {
       continue;
     }
    }
  
    
  }

}
