


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.collection.base_cpm.CasObjectProcessor;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;


public class CasConsumer extends CasConsumer_ImplBase implements CasObjectProcessor {
  File outFile;

  FileWriter fileWriter;

  public CasConsumer() {
  }
  
  public void initialize() throws ResourceInitializationException{
    // extract configuration parameter settings
    String oPath = (String) getUimaContext().getConfigParameterValue("outputdocument");
    
 // Output file should be specified in the descriptor
    if (oPath == null) {
      throw new ResourceInitializationException(
              ResourceInitializationException.CONFIG_SETTING_ABSENT, new Object[] { "outputdocument" });
    }
    
    try {
      fileWriter = new FileWriter("/home/zimo/Desktop/hw2-zimoz.out");
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }
  
  
  public synchronized void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    
 // iterate and print annotations
    Iterator annotationIter = jcas.getAnnotationIndex(geneTag.type).iterator();
    while (annotationIter.hasNext()) {
      //GeneType annotation = jcas.getCas().
      geneTag g= (geneTag) annotationIter.next();

      // get the text that is enclosed within the annotation in the CAS
      String aText = g.getCoveredText();
      try {
        fileWriter.write(((geneTag) g).getID() + '|');
        fileWriter.write(g.getStart() + " " + g.getEnd() + "|");
        fileWriter.write(g.getText() + '\n');
        fileWriter.flush();
      } catch (IOException e) {
        throw new ResourceProcessException(e);
      }
    }
  }

  @Override
  public void destroy() {
    if (fileWriter != null) {
      try {
        fileWriter.close();
      } catch (IOException e) {
        // ignore IOException on destroy
      }
    }
  }

}