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

	/** scanners for reading in the files **/
	private static Scanner scannerDataFile = null;
	private static Scanner scannerCommentsFile = null;


	/** Specified files used for this assignment **/
	/* Output file path */
	private static final String OUTPUT_FILENAME = "survey.xml";

	/** input filename path**/	
	private static final String DATA_FILENAME = "data/surveydata.csv";
	private static final String commentsFileName = "data/surveycomments.csv";

	/** Line length constansts **/

	private static final int STANDARD = 11;
	private static final int QUESTION_WITH_OPTIONAL = 13;

	/** csv file constant **/
	private static final int QUESTION = 0;
	private static final int QUESTION_COMMENT = 1;
	private static final int YES = 2;
	private static final int YES_COMMENT = 3;
	private static final int NO = 4;
	private static final int NO_COMMENT = 6;
	private static final int SIT = 6;
	private static final int SIT_COMMENT = 7;
	private static final int STAND = 8;
	private static final int STAND_COMMENT = 9;
	private static final int BLANK = 10;
	private static final int BLANK_COMMENT = 11;
	private static final int OPTIONAL = 12;


	/*----------------------------- General Methods ----------------------------*/

	/**
  Main program to call DOM creator.

	 */
	public static void main(String[] args)  throws FileNotFoundException {


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

			// create surveydata as the root element and add it to the document
			Element rootElement = document.createElement("surveydata");
			document.appendChild(rootElement);    

			// append the data from suveydata.csv
			appendSurveyDataToDocument(rootElement);

			//append the data from surveycomments.csv
			appendCommentsToDocument(rootElement);


			// This allows saving the DOM as a file with indentation
			Source source = new DOMSource(document);
			Result result = new StreamResult(new File(OUTPUT_FILENAME));
			Transformer transf = TransformerFactory.newInstance().newTransformer();

			transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transf.setOutputProperty(OutputKeys.INDENT, "yes");
			transf.transform(source, result);
		}
		catch (Exception exception) {
			System.err.println("Could not create document " + exception);
		}

	}


	private static void appendCommentsToDocument(Element rootElement) {
		Element comments = document.createElement("comments");
		rootElement.appendChild(comments);

		int commentLine = 0;

		while (scannerCommentsFile.hasNextLine()){
			String commentsData = scannerCommentsFile.nextLine();
			String delims = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
			String[] commentTokens = commentsData.split(delims);

			for (String c : commentTokens)
				System.out.println(c.replace("\"", ""));

			if (commentLine!=0){
				Element comment = document.createElement("comment");

				Element commentID = document.createElement("commentID");
				commentID.appendChild(document.createTextNode(commentTokens[0]));
				comment.appendChild(commentID);

				Element commentDetails = document.createElement("comment");


				commentDetails.appendChild(document.createTextNode((commentTokens[1]).replace("\"", "")));
				comment.appendChild(commentDetails);

				comments.appendChild(comment);
			}
			commentLine++;

		}

		if (scannerCommentsFile != null) scannerCommentsFile.close();

	}


	private static void appendSurveyDataToDocument(Element rootElement) {


		//create questions child of library
		Element questions = document.createElement("questions");
		rootElement.appendChild(questions);

		// counter for iterating lines of the file
		int line = 0;

		// while there are more lines in the file read
		// in the date to be transformed to xml
		while (scannerDataFile.hasNextLine()){

			String information = scannerDataFile.nextLine();	// extract the line of data
			String delims = ",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";								// csv files are delimeted by commas
			String[] questionTokens = information.split(delims);
			System.out.println("Line " + line + " : " + questionTokens.length);



			if (line > 0){	// counting from 1 discards the header row

				for (String q : questionTokens)
					System.out.print(q + ", ");
				appendQuestionToQuestions(questions, questionTokens);

			}
			line++; // increment line count before processing next line    
		}
		if (scannerDataFile != null) scannerDataFile.close();

	}


	private static void appendQuestionToQuestions(Element questions, String[] questionTokens) {
		// create question element
		Element question = document.createElement("question");

		// create phrase and append
		Element phrase = document.createElement("phrase");
		phrase.appendChild(document.createTextNode(questionTokens[QUESTION].replace("\"", "")));
		question.appendChild(phrase);

		if (!(questionTokens[QUESTION_COMMENT].equals(""))){
			Element commentID = document.createElement("commentID");
			commentID.appendChild(document.createTextNode(questionTokens[QUESTION_COMMENT]));
			question.appendChild(commentID);}

		// create answers child of question and append to question
		Element answers = document.createElement("answers");

		appendAnswers(answers, questionTokens);

		question.appendChild(answers);

		questions.appendChild(question);

	}


	private static void appendAnswers(Element answers, String[] questionTokens) {

		if (!(questionTokens[YES].equals(""))) {
			Element yes = document.createElement("yes");
			yes.appendChild(document.createTextNode(questionTokens[YES]));
			answers.appendChild(yes);
		}

		if (!(questionTokens[YES_COMMENT].equals(""))) {
			Element yescomment = document.createElement("yescomment");
			yescomment.appendChild(document.createTextNode(questionTokens[YES_COMMENT]));
			answers.appendChild(yescomment);
		}

		if (!(questionTokens[NO].equals(""))) {
			Element no = document.createElement("no");
			no.appendChild(document.createTextNode(questionTokens[NO]));
			answers.appendChild(no);
		}

		if (!(questionTokens[NO_COMMENT].equals(""))) {
			Element nocomment = document.createElement("nocomment");
			nocomment.appendChild(document.createTextNode(questionTokens[NO_COMMENT]));
			answers.appendChild(nocomment);
		}

		if (!(questionTokens[SIT].equals(""))) {
			Element sit = document.createElement("sit");
			sit.appendChild(document.createTextNode(questionTokens[SIT]));
			answers.appendChild(sit);
		}

		if (!(questionTokens[SIT_COMMENT].equals(""))) {
			Element sitcomment = document.createElement("sitcomment");
			sitcomment.appendChild(document.createTextNode(questionTokens[SIT_COMMENT]));
			answers.appendChild(sitcomment);
		}

		if (!(questionTokens[STAND].equals(""))) {
			Element stand = document.createElement("stand");
			stand.appendChild(document.createTextNode(questionTokens[STAND]));
			answers.appendChild(stand);
		}

		if (!(questionTokens[STAND_COMMENT].equals(""))) {
			Element standcomment = document.createElement("standcomment");
			standcomment.appendChild(document.createTextNode(questionTokens[STAND_COMMENT]));
			answers.appendChild(standcomment);
		}

		if (!(questionTokens[BLANK].equals(""))) {
			Element blank = document.createElement("blank");
			blank.appendChild(document.createTextNode(questionTokens[BLANK]));
			answers.appendChild(blank);
		}

		if ((questionTokens.length > STANDARD) && (questionTokens.length == QUESTION_WITH_OPTIONAL)) {
			if (!(questionTokens[BLANK_COMMENT].equals(""))) {
				Element blankcomment = document.createElement("blankcomment");
				blankcomment.appendChild(document.createTextNode(questionTokens[BLANK_COMMENT]));
				answers.appendChild(blankcomment);
			}
			
			if (!(questionTokens[OPTIONAL].equals(""))){
				Element optional = document.createElement("optional");
				optional.appendChild(document.createTextNode(questionTokens[OPTIONAL]));
				answers.appendChild(optional);
			}

		}
	}
}

