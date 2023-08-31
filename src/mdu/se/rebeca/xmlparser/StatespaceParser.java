package mdu.se.rebeca.xmlparser;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import mdu.se.rebeca.statespace.StateSpace;
import mdu.se.rebeca.statespace.StateSpaceLoader;
import mdu.se.rebeca.statespace.StateWrapper;

public class StatespaceParser {
	public static StateSpace<StateWrapper> parse(String path) {
		StateSpace<StateWrapper> statespace = null;
		try {
			File stateSpaceFile = new File(path);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			StateSpaceLoader<StateWrapper> handler = new StateSpaceLoader<StateWrapper>() {
				public StateWrapper createState() {
					return new StateWrapper();
				}
			};
			saxParser.parse(new FileInputStream(stateSpaceFile), handler);
			statespace = handler.getStatespace();
			} 
		catch (Exception e) {
				e.printStackTrace();
				System.out.println("Unexpected exception: " + e.getMessage());
		}
		
		return statespace;
	}
}