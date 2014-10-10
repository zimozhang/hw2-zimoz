

/* First created by JCasGen Thu Oct 09 23:11:57 EDT 2014 */
package types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Oct 09 23:11:57 EDT 2014
 * XML source: /home/zimo/git/hw2-zimoz/hw2-zimoz/src/main/resources/typeSystemDescriptor.xml
 * @generated */
public class resultTag extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(resultTag.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected resultTag() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public resultTag(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public resultTag(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public resultTag(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: ID

  /** getter for ID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (resultTag_Type.featOkTst && ((resultTag_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "types.resultTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((resultTag_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (resultTag_Type.featOkTst && ((resultTag_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "types.resultTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((resultTag_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: Text

  /** getter for Text - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (resultTag_Type.featOkTst && ((resultTag_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "types.resultTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((resultTag_Type)jcasType).casFeatCode_Text);}
    
  /** setter for Text - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (resultTag_Type.featOkTst && ((resultTag_Type)jcasType).casFeat_Text == null)
      jcasType.jcas.throwFeatMissing("Text", "types.resultTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((resultTag_Type)jcasType).casFeatCode_Text, v);}    
  }

    