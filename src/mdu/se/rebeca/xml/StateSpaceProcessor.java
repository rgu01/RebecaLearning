package mdu.se.rebeca.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import mdu.se.rebeca.cps.Controller;
import mdu.se.rebeca.statespace.*;

public class StateSpaceProcessor<T extends State<T>> {
	public StateSpace<StateWrapper> parse(String path) {
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
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unexpected exception: " + e.getMessage());
		}

		return statespace;
	}

	public void writePolicy(Controller policy) {
		try {
			HashMap<State<T>, List<Transition<T>>> winning = policy.getPolicy();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("transitionsystem");

			for (State<T> state : winning.keySet()) {
				Element stateElement = doc.createElement("state");
				stateElement.setAttribute("id", state.getId());
				String pros = "";
				for (String pro : state.getAtomicPropositions()) {
					pros += pro + ",";
				}
				stateElement.setAttribute("atomicpropositions", pros);
				rootElement.appendChild(stateElement);
				for (Transition<T> action : winning.get(state)) {
					if (action != null) {
						Element actionElement = doc.createElement("transition");
						actionElement.setAttribute("source", action.getSource().getId());
						actionElement.setAttribute("destination", action.getDestination().getId());
						actionElement.setAttribute("executionTime", action.getWeight() + "");
						actionElement.setAttribute("shift", action.getShift() + "");

						Element msgElement = null;
						if (action.getAction().equals("time")) {
							msgElement = doc.createElement("time");
							msgElement.setAttribute("value", action.getWeight() + "");
						} else {
							msgElement = doc.createElement("messageserver");
							msgElement.setAttribute("sender", action.getSender());
							msgElement.setAttribute("owner", action.getOwner());
							msgElement.setAttribute("title", action.getAction());
						}

						actionElement.appendChild(msgElement);
						rootElement.appendChild(actionElement);
					}
				}
			}

			doc.appendChild(rootElement);

			//FileOutputStream outputXML = new FileOutputStream("statespace/policy.xml");
			//writeXml(doc, outputXML);
			FileOutputStream outputStateSpace = new FileOutputStream("statespace/policy.statespace");
			writeXml(doc, outputStateSpace);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void writeXml(Document doc, OutputStream output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);

		transformer.transform(source, result);
	}

}