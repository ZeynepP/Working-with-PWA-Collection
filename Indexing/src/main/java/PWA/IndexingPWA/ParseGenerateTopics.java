package PWA.IndexingPWA;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class ParseGenerateTopics {

	/**
	 * It is used to generate TREC style topics files from pwa topics format
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		String path = args[0]; //"/home/pehlivanz/PWA/topics.xml";
		String output = args[1];
		File fXmlFile = new File(path);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document docoriginal = dBuilder.parse(fXmlFile);
		
		docoriginal.getDocumentElement().normalize();
		
		// PREPARE NEW DOCUMENT
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document docNEW = docBuilder.newDocument();
		Element rootElement = docNEW.createElement("Topics");
		docNEW.appendChild(rootElement);
		
		NodeList nList = docoriginal.getElementsByTagName("topic");
		Node nNode;
		Element eElement;
		
		Element newelement;
		String query;
		String datestart;
		String dateend;
		Element tempelement;
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			tempelement = docNEW.createElement("top");
			
			nNode = (Node) nList.item(temp);
	 
		
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				eElement = (Element) nNode;
	 
				newelement = docNEW.createElement("num");
				newelement.appendChild(docNEW.createTextNode("Number:" + eElement.getAttribute("number")));
				tempelement.appendChild(newelement);
				
				query = eElement.getElementsByTagName("query").item(0).getTextContent();
				//System.out.print("\""+query+"\",");
				
				try {
					datestart = eElement.getElementsByTagName("start").item(0).getTextContent();
					dateend = eElement.getElementsByTagName("end").item(0).getTextContent();
					
					query = query.trim() +"@" + convertDateToTimeCronon(datestart) + "_" + convertDateToTimeCronon(dateend);
					System.out.println(query);
				
				// to filter topics without date I did it here
					newelement = docNEW.createElement("title");
					newelement.appendChild(docNEW.createTextNode( query));
					tempelement.appendChild(newelement);
					
					newelement = docNEW.createElement("desc");
					newelement.appendChild(docNEW.createTextNode(eElement.getElementsByTagName("description").item(1).getTextContent()));
					tempelement.appendChild(newelement);
					
					newelement = docNEW.createElement("narr");
					newelement.appendChild(docNEW.createTextNode(eElement.getElementsByTagName("description").item(0).getTextContent()));
					tempelement.appendChild(newelement);
					
					rootElement.appendChild(tempelement);
				}
				catch(Exception ex)
				{
					System.out.println("NO Date");
					
					
				}
				
			}
		}
		
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(docNEW);
		StreamResult result = new StreamResult(new File(output));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
		
	}
	
	
	public static long convertDateToTimeCronon(String date) {
		String longDateFormat = "dd/mm/yyyy";
    	long chronon = 0;
    	try {
    		Date dateTimestamp = new SimpleDateFormat(longDateFormat).parse(date);
			Calendar cal = new GregorianCalendar(Locale.FRANCE);
			cal.setTime(dateTimestamp);
			chronon = cal.getTimeInMillis();

        }  catch (ParseException e) {
        	e.printStackTrace();
        	System.exit(-1);
        }

		return chronon;
    }


}
