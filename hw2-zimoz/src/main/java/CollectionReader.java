   

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import types.sentenceTag;

public class CollectionReader extends CollectionReader_ImplBase {
  
  public static final String INPUTDIR = "inputdocument";
  
  private int mCurrentIndex;
  
  @Override
 
/**
 * CollectionReader is the first step of the name recognition process.
 * The main duty for this class is to read the original text data from input document.
 * Reads the raw data line by line utilizing the class read(to reduce the I/O time).
 * This class have two processes:
 * First, splits the original line from whole data and sends the ID the the line to the CasConsumer;
 * Second, sends only the line context to Annotator.
 * 
 * @author zimo
 */
 
  public void initialize() throws ResourceInitializationException {
    
    
    
    ArrayList<String> sentences = new ArrayList<String>();
    
    File directory = new File(((String) getConfigParameterValue(INPUTDIR)).trim());
    
    try{
      sentences = readFile(directory);
    }catch (Exception e){
      e.printStackTrace();
    }
    
    mCurrentIndex = 0;
    
  }
  
  private ArrayList<String> records = new ArrayList<String>();
 /**
  * Reads the contents of the input file
  * stores them in ArrayList. 
  * @param directory
  * @return records
  * @throws Exception
  */
  private ArrayList<String> readFile(File directory) throws Exception
  {
    String line = null;
    
    InputStream is = LingPipeAnnotator.class.getClassLoader().getResourceAsStream("hw2.in");
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
    
     while ((line = reader.readLine()) != null) 
     {
         records.add(line);
     }
     
     reader. close();

     return records;
  }
  
  
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    // TODO Auto-generated method stub
/**
 * Reads each sentence from records and prints them out.
 * Splits the sentences and puts the parts separately in sentenceTag type variable.
 */
  JCas jcas;
  try {
    jcas = aCAS.getJCas();
  } catch (CASException e) {
    throw new CollectionException(e);
  }
  
  
  String file = records.get(mCurrentIndex++);
//  System.out.println(file);
  sentenceTag st = new sentenceTag(jcas);
  st.setID(file.substring(0, 14));
  st.setText(file.substring(15, file.length()));
  st.addToIndexes();
  
 
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    // TODO Auto-generated method stub
    return mCurrentIndex < records.size();
  }

  @Override
  public Progress[] getProgress() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

}