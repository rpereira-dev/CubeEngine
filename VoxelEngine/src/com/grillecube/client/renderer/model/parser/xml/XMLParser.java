package com.grillecube.client.renderer.model.parser.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a node in an XML file. This contains the name of the node, a map
 * of the attributes and their values, any text data between the start and end
 * tag, and a list of all its children nodes.
 * 
 * @author Karl
 *
 */
public class XMLParser {

	private static final Pattern DATA = Pattern.compile(">(.+?)<");
	private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
	private static final Pattern ATTR_NAME = Pattern.compile("(.+?)=");
	private static final Pattern ATTR_VAL = Pattern.compile("\"(.+?)\"");
	private static final Pattern CLOSED = Pattern.compile("(</|/>)");

	/**
	 * Reads an XML file and stores all the data in {@link XMLNode} objects,
	 * allowing for easy access to the data contained in the XML file.
	 * 
	 * @param file
	 *            - the XML file
	 * @return The root node of the XML structure.
	 */
	public static XMLNode loadXmlFile(String filepath) {
		File file = new File(filepath);
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Can't find the XML file: " + file.getPath());
			System.exit(0);
			return null;
		}
		try {
			br.readLine();
			XMLNode node = loadNode(br);
			br.close();
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error with XML file format for: " + file.getPath());
			System.exit(0);
			return null;
		}
	}

	private static XMLNode loadNode(BufferedReader reader) throws Exception {
		String line = reader.readLine().trim();
		if (line.startsWith("</")) {
			return null;
		}
		String[] startTagParts = getStartTag(line).split(" ");
		XMLNode node = new XMLNode(startTagParts[0].replace("/", ""));
		addAttributes(startTagParts, node);
		addData(line, node);
		if (CLOSED.matcher(line).find()) {
			return node;
		}
		XMLNode child = null;
		while ((child = loadNode(reader)) != null) {
			node.addChild(child);
		}
		return node;
	}

	private static void addData(String line, XMLNode node) {
		Matcher matcher = DATA.matcher(line);
		if (matcher.find()) {
			node.setData(matcher.group(1));
		}
	}

	private static void addAttributes(String[] titleParts, XMLNode node) {
		for (int i = 1; i < titleParts.length; i++) {
			if (titleParts[i].contains("=")) {
				addAttribute(titleParts[i], node);
			}
		}
	}

	private static void addAttribute(String attributeLine, XMLNode node) {
		Matcher nameMatch = ATTR_NAME.matcher(attributeLine);
		nameMatch.find();
		Matcher valMatch = ATTR_VAL.matcher(attributeLine);
		valMatch.find();
		node.addAttribute(nameMatch.group(1), valMatch.group(1));
	}

	private static String getStartTag(String line) {
		Matcher match = START_TAG.matcher(line);
		match.find();
		return match.group(1);
	}

}