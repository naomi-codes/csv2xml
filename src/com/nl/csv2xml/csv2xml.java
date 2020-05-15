package com.nl.csv2xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
DOM handler to read CSV information from survey.csv and surveycomments.csv
 to create an XML document, and to print it.

@author   Naomi Lambert
@version  24/03/2017
*/
public class csv2xml{

/** Document builder */
private static DocumentBuilder builder = null;

/** XML document */
private static Document document = null;


/** Specified files used for this assignment **/
/* Output filename */
private static final String OUTPUT_FILENAME = "survey.xml";

/** input filenames **/	
private static final String DATA_FILENAME = "surveydata.csv";
private static final String commentsFileName = "surveycomments.csv";

/*----------------------------- General Methods ----------------------------*/

/**
  Main program to call DOM creator.

*/
public static void main(String[] args)  throws FileNotFoundException {
	
	/** scanners for reading in the files **/
	Scanner scannerDataFile = null;
	Scanner scannerCommentsFile = null;
	
	/**  **/
	try {
		scannerDataFile = new Scanner(new File(DATA_FILENAME));

		scannerCommentsFile = new Scanner(new File(commentsFileName));
	} catch (FileNotFoundException e) {
		System.out.println("IOException: " + e.getMessage() + " not found");
	}
	
	/** if the scanner has been successfully 
	 * initialised extract the data 
	 * as tokens into @param rowData[]
	 */
	
	try {
	    // create a document builder
	    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	    builder = builderFactory.newDocumentBuilder();
	    
	    // new document
	    document = builder.newDocument();

	    // create library as the root element and add it to the document
	    Element rootElement = document.createElement("surveydata");
	    document.appendChild(rootElement);    
	    
	    //create questions child of library
	    Element questions = document.createElement("questions");
	    rootElement.appendChild(questions);
	    
	    	// counter for iterating lines of the file
	    	int line = 0;
	    	
	    	// while there are more lines in the file read
	    	// in the date to be transformed to xml
	        while (scannerDataFile.hasNextLine()){
	            String information = scannerDataFile.nextLine();
	            String delims = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
	            String[] rowTokens = information.split(delims);
	            
	            
	             	/** Output tokens from CSV file to console **/
	            		if (line == 0){
	            				System.out.println("Discarded header row:");
	            				for (String info: rowTokens)
	            				System.out.println(info);
	            		} else {
	            				System.out.println("Line length: " + rowTokens.length);
	            				for (String info: rowTokens)
	            				System.out.println(info);
	            		
	            				Element question = document.createElement("question");
	            				
	            				Element phrase = document.createElement("phrase");
	            				phrase.appendChild(document.createTextNode(rowTokens[0]));
	            				question.appendChild(phrase);
	            	
	            				if (!(rowTokens[1].equals(""))){
	            				Element commentID = document.createElement("commentID");
	            				commentID.appendChild(document.createTextNode(rowTokens[1]));
	            				question.appendChild(commentID);}

	            				Element responses = document.createElement("responses");
	            		
	            				
	            				if (rowTokens.length == 11){
	            				Element yesno = document.createElement("yesno");
	            							Element yes = document.createElement("yes");
	            							yes.appendChild(document.createTextNode(rowTokens[2]));
	            							yesno.appendChild(yes);
	            		
	            							Element no = document.createElement("no");
	            							no.appendChild(document.createTextNode(rowTokens[4]));
	            							yesno.appendChild(no);
	            							responses.appendChild(yesno);
	            							
	            							if (line == 8){
	            								Element commentID1 = document.createElement("commentIDno");
	            	            				commentID1.appendChild(document.createTextNode(rowTokens[5]));
	            	            				yesno.appendChild(commentID1);
	            							}
	            				} else {
	            						Element sitstand = document.createElement("sitstand");
	            							Element sit = document.createElement("sit");
	            							sit.appendChild(document.createTextNode(rowTokens[6]));
	            							sitstand.appendChild(sit);
	            			
	            							Element stand = document.createElement("stand");
	            							stand.appendChild(document.createTextNode(rowTokens[8]));
	            							sitstand.appendChild(stand);
	            							responses.appendChild(sitstand);
	            						}	
	            				
	            				Element blank = document.createElement("blank");
	            				blank.appendChild(document.createTextNode(rowTokens[10]));
	            				responses.appendChild(blank);
	            				
	            				if (rowTokens.length == 13){
	            					Element optional = document.createElement("optional");
	            					optional.appendChild(document.createTextNode(rowTokens[12]));
	            					responses.appendChild(optional);
	            				}
	            				
	            		
	            	
	            				question.appendChild(responses);
	            				
	            				questions.appendChild(question);
	            		}
	            line++; // increment line count before processing next line    
	        	}
		    if (scannerDataFile != null) scannerDataFile.close();
	        Element comments = document.createElement("comments");
			rootElement.appendChild(comments);
			
			int commentLine = 0;
			
				while (scannerCommentsFile.hasNextLine()){
					String information = scannerCommentsFile.nextLine();
					String delims = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
					String[] rowTokens = information.split(delims);
					
						
					for (String info: rowTokens)
						System.out.println(info);
					if (commentLine!=0){
					Element aComment = document.createElement("aComment");
						Element commentID = document.createElement("commentNoteID");
						commentID.appendChild(document.createTextNode(rowTokens[0]));
						aComment.appendChild(commentID);
						Element commentDetails = document.createElement("commentDetails");
						commentDetails.appendChild(document.createTextNode(rowTokens[1]));
						aComment.appendChild(commentDetails);
					
					comments.appendChild(aComment);}
					commentLine++;
					
				}
	   
	    if (scannerCommentsFile != null) scannerCommentsFile.close();
	    
	    // This allows saving the DOM as a file with indentation
	    File file = new File(OUTPUT_FILENAME);
	    Source source = new DOMSource(document);
	    Result result = new StreamResult(file);
	    Transformer transf = TransformerFactory.newInstance().newTransformer();

	    transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transf.setOutputProperty(OutputKeys.INDENT, "yes");
	    transf.transform(source, result);
	  }
	  catch (Exception exception) {
	    System.err.println("could not create document " + exception);
	  }

	}
}

