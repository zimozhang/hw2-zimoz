import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import types.geneTag;
import types.sentenceTag;


public class StanfordAnnotator extends JCasAnnotator_ImplBase {

  /**
   * StanfordAnnotator is the analysis engine of the system. It is the second step of main name
   * recognition process. Accepts the context of the sentence. Returns the start and end point of
   * each gene name entities.
   * 
   * Call the stanford maven dependencies and uses its's recognition methods.
   * 
   * @author zimo
   *
   * @param phrase
   * @return
   */

  /**
   * Counts the white blanks for calculating the position of each gene.
   * 
   * @param phrase
   * @return
   */

  BufferedReader br = null;

  BufferedWriter bw;

  String line = null;

  int correct;// the right number of geneTag

  int totalrecognitions;

  private HashSet<String> s;

// String sample=(String) getConfigParameterValue("SAMPLE_FILE");
  
  public void initialize(UimaContext aUimaContext) throws ResourceInitializationException {
    super.initialize(aUimaContext);

    s = new HashSet<String>();

    try {
      InputStream is = LingPipeAnnotator.class.getClassLoader().getResourceAsStream("sample.out");
      
      br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      
      while ((line = br.readLine()) != null) {
        s.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      bw = new BufferedWriter(new FileWriter("comparefile"));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

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
   * Iterates the sentences getting from CollectionReader. Finds out the ID, gene name and the start
   * and end point of each gene.
   */
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIterator iter = aJCas.getAnnotationIndex().iterator();

    // iterate
    while (iter.isValid()) {

      sentenceTag a = (sentenceTag) iter.get();
      String docText = a.getText();
      PosTagNamedEntityRecognizer recognizer = null;
      try {
        recognizer = new PosTagNamedEntityRecognizer();
      } catch (ResourceInitializationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      String id = a.getID();

      Map<Integer, Integer> mymap = recognizer.getGeneSpans(docText);
      int begin = 0;
      int end = 0;

      for (Map.Entry<Integer, Integer> pairs : mymap.entrySet()) {
        geneTag b = new geneTag(aJCas);
        b.setID(id);
        b.setBegin(pairs.getKey());
        b.setEnd(pairs.getValue());
        // int k = (int) pairs.getKey();
        // int v = (int) pairs.getValue();
        // System.out.println(k);
        // b.setBegin(k);
        // b.setEnd(v);

        String text = docText.substring(begin, end);
        String c = docText.substring(pairs.getKey(), pairs.getValue());
        String d = docText.substring(0, pairs.getKey());

        int m = countBlanks(c);
        int n = countBlanks(d);
        totalrecognitions++;
        b.setBegin(pairs.getKey() - n);
        b.setEnd(pairs.getValue() - 1 - n - m);
        b.setText(docText.substring(pairs.getKey(), pairs.getValue()));
        b.setCasProcessorId("2");

        String info = b.getID() + "|" + b.getStart() + " " + b.getEnd() + "|" + b.getText();

        if (s.contains(info)) {
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
        b.addToIndexes();

      }

      iter.moveToNext();
    }

  }

  public void destroy() {
    /**
     * Calculates the precision and recall for evaluating the performance of the Annotators
     */
    if (bw != null) {
      Double precision = (Double) (correct * 1.0 / totalrecognitions * 1.0);
      System.out.println("StanfordAnnotator precision="+precision);
      try {
        bw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      bw = null;
    }
  }
  public StanfordAnnotator() {

  }

}
