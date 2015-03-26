import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

	private static boolean info;
	private static boolean remove_floatingObjects;
	private static boolean remove_unPowered;
	private static boolean remove_noBeacon;
	private static boolean remove_noOwnerShip;
	private static boolean disableBlocks;
	private static boolean cleanup;
	/**
	 * Space Engineers - Maintenance
	 * 
	 * Parameter: <SavePath> <BackupPath> <Params[]> Params[]: -
	 * 
	 */

	public static List<String> entityRemove = null;
	private static boolean deactivateIdleMovementTurret;

	int asteroids = 0;
	int floatingObjects = 0;
	int grids = 0;
	int deleted = 0;
	int modified = 0;
	int unpowered = 0;

	int light = 0;
	int gravity = 0;
	int timer = 0;
	int sensor = 0;
	int refinery = 0;
	int assembler = 0;
	int projector = 0;

	public static void main(String[] args)
			throws org.apache.commons.cli.ParseException {
		HelpFormatter lvFormater = new HelpFormatter();
		Options options = new Options();
		options.addOption("h", "help", false, "Show Help");

		options.addOption(OptionBuilder.withLongOpt("savePath")
				.withDescription("Path to Savegame").withArgName("PathToFile")
				.isRequired(false).withValueSeparator('=').hasArg(true)
				.create("S"));

		options.addOption(OptionBuilder.withLongOpt("backupPath")
				.withDescription("Path to Backup").isRequired(false)
				.withValueSeparator('=').hasArg(true).create("B"));

		options.addOption(OptionBuilder.withLongOpt("verbose")
				.withDescription("Activate verbose").isRequired(false)
				.withValueSeparator('=').hasArg(false).create("v"));

		options.addOption(OptionBuilder.withLongOpt("cleanup")
				.withDescription("Cleanup active, needed for all cleanups")
				.isRequired(false).withValueSeparator('=').hasArg(false)
				.create("c"));

		options.addOption(OptionBuilder.withLongOpt("cleanupBeacon")
				.withDescription("Cleanup noBeacon").isRequired(false)
				.withValueSeparator('=').hasArg(false).create("cb"));

		options.addOption(OptionBuilder.withLongOpt("cleanupNoPower")
				.withDescription("Cleanup noPowered").isRequired(false)
				.withValueSeparator('=').hasArg(false).create("cp"));

		options.addOption(OptionBuilder.withLongOpt("cleanupFloatingObjects")
				.withDescription("Cleanup noPowered").isRequired(false)
				.withValueSeparator('=').hasArg(false).create("cf"));

		options.addOption(OptionBuilder.withLongOpt("deactivateAllFunctional")
				.withDescription("Deactivating all functional")
				.isRequired(false).withValueSeparator('=').hasArg(false)
				.create("da"));

		options.addOption(OptionBuilder
				.withLongOpt("disableIdleMovementTurrets")
				.withDescription(
						"Deactivating the Idle-Movement on all turrets - for fixing bugs,lags")
				.isRequired(false).withValueSeparator('=').hasArg(false)
				.create("dim"));

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("h")) {
			lvFormater.printHelp("Help ", options);
		}

		String argSavePath = "";
		if (cmd.hasOption("S")) {
			argSavePath = cmd.getOptionValue("S");
		}

		boolean argBackup = false;
		String argBackupPath = "";
		if (cmd.hasOption("B")) {
			argBackup = true;
			if (cmd.hasOption("B")) {
				argBackupPath = cmd.getOptionValue("B");
			}
		}

		boolean argInfo = false;
		if (cmd.hasOption("v")) {
			argInfo = true;
		}

		boolean argDim = false;
		if (cmd.hasOption("dim")) {
			argDim = true;
		}

		boolean argCleanup = false;
		if (cmd.hasOption("c")) {
			argCleanup = true;
		}

		boolean argBeacon = false;
		if (cmd.hasOption("cb")) {
			argBeacon = true;
		}

		boolean argNoPower = false;
		if (cmd.hasOption("cp")) {
			argNoPower = true;
		}

		boolean argRemFloating = false;
		if (cmd.hasOption("cf")) {
			argRemFloating = true;
		}

		boolean argDeAll = false;
		if (cmd.hasOption("da")) {
			argDeAll = true;
		}

		/*
		 * ===================================================
		 * ================== General Settings =================
		 * ===================================================
		 */

		entityRemove = new ArrayList<String>();

		String path = "";
		if (argSavePath.equals("")) {
			path = "D:\\TestWorld\\SANDBOX_0_0_0_.sbs";
		} else {
			path = argSavePath + "\\SANDBOX_0_0_0_.sbs";
		}

		info = argInfo; // Informations doing the Work

		cleanup = argCleanup; // Activating Cleanup-Mode

		disableBlocks = argDeAll;

		/*
		 * ===================================================
		 * ================== Config Backup =================
		 * ===================================================
		 */
		if (argBackup)
			createBackup(argSavePath, argBackupPath);

		/*
		 * ===================================================
		 * ================== Config Cleanup =================
		 * ===================================================
		 */

		remove_floatingObjects = argRemFloating; // Removing Floating
		remove_unPowered = argNoPower; // removing grids without power
		remove_noBeacon = argBeacon; // removing grids without beacon
		remove_noOwnerShip = true;

		/*
		 * ===================================================
		 * ================== Config DisableBlocks =================
		 * ===================================================
		 */
		boolean gravity = false;
		boolean rotor = false;
		boolean sensor = false;
		boolean projector = false;
		boolean timerblock = false;
		deactivateIdleMovementTurret = argDim;

		//
		// ################## Code #####################
		//
		// Do not Touch
		//
		// #############################################

		Main main = new Main();
		if (info | disableBlocks | cleanup)
			main.startMaintanence(path);
	}

	public static boolean createBackup(String source, String destination) {
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd_HH-mm");
		try {
			Zipping zip = new Zipping(source, destination + "\\Backup_"
					+ LocalDateTime.now().format(formatter) + ".zip");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			log("BACKUP ERROR", 0, false);
			return false;
		}

	}

	public void startMaintanence(String path) {
		Document doc = null;

		if (cleanup) {
			doc = analyseXML(readXML(path));
		}

		// Writing (save) changes to XML
		if (cleanup | disableBlocks) {
			saveXML(doc, path);

			System.out.println("### Results ###");
			System.out.println("Grids: " + this.grids);
			System.out.println("###	Changes ###");
			System.out.println("Deleted: " + this.deleted);
			System.out.println("Modified: " + this.modified);
		}
	}

	public static void log(String text, int level, boolean append) {
		boolean debug = true;
		if (info) {
			switch (level) {
			case 0:
				if (append)
					System.out.print("INFO: " + text);
				else
					System.out.println("INFO: " + text);
				break;
			case 1:
				if (debug) {
					if (append)
						System.out.print("  INFO: " + text);
					else
						System.out.println("  INFO: " + text);
				}
				break;
			case 2:
				if (debug) {
					if (append)
						System.out.print("   INFO: " + text);
					else
						System.out.println("   INFO: " + text);
				}
				break;

			default:
				if (debug) {
					if (append)
						System.out.print("    INFO: " + text);
					else
						System.out.println("    INFO: " + text);
				}
				break;
			}

		}
	}

	public void info(List<CubeGrid> list) {
		for (CubeGrid foo : list) {

			switch (foo.getType()) {
			case "MyObjectBuilder_FloatingObject":
				floatingObjects++;
				break;
			case "MyObjectBuilder_VoxelMap":
				asteroids++;
				break;
			case "MyObjectBuilder_CubeGrid":
				// System.out.print("Grid: " + foo.getType() + " ("
				// + foo.getEntityId() + ")");
				// System.out.println("	Blocks: " + foo.getCubeBlocks().size());
				if (!foo.isPowered()) {
					unpowered++;
				}
				grids++;
				break;

			}
		}

		System.out.println("### Infos ###");
		System.out.println("	Info:");
		System.out.println("		Grids: " + this.grids);
		System.out.println("			unpowered: " + this.unpowered);
		System.out.println("		Asteroids: " + this.asteroids);
		System.out.println("		Floating: " + this.floatingObjects);

	}

	private Transformer createXmlTransformer() throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		return transformer;
	}

	private void saveXML(Document doc, String savePath) {
		// write the content into xml file
		doc.normalize();
		try {

			OutputStream os = new FileOutputStream(new File(savePath));
			OutputStreamWriter bufferedWriter = new OutputStreamWriter(os,
					"UTF8");

			Transformer transformer = createXmlTransformer();
			// StreamResult result = new StreamResult(new PrintWriter(
			// new FileOutputStream(savePath, false)));
			StreamResult result = new StreamResult(bufferedWriter);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Document analyseXML(Document doc) {
		try {
			NodeList nList = doc
					.getElementsByTagName("MyObjectBuilder_EntityBase");

			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nNode;

					switch (e.getAttribute("xsi:type")) {
					case "MyObjectBuilder_FloatingObject":
						// do s.th. with FO
						break;

					case "MyObjectBuilder_CubeGrid":
						// do s.th. with CG
						grids++;
						log("Grid-Id: "
								+ e.getElementsByTagName("EntityId").item(0)
										.getTextContent(), 0, false);
						if (checkCubeGridForCleanup(e)) {
							log("deleted", 1, false);
							deleteElement(e);
						}
						break;

					case "MyObjectBuilder_Character":
						// do s.th. with Characters
						break;

					case "MyObjectBuilder_VoxelMap":
						// do s.th. with Asteroids
						break;

					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	public boolean checkCubeGridForCleanup(Element element) {
		log("===== Entering CubeBlocks =====", 1, false);
		boolean delete = true;
		boolean powered = false;
		boolean beaconed = false;

		NodeList nestedNode = element
				.getElementsByTagName("MyObjectBuilder_CubeBlock");
		for (int j = 0; j < nestedNode.getLength(); j++) {
			Node n = nestedNode.item(j);
			Element ee = (Element) n;
			if (ee.getNodeName() != null) {
				log("Type: " + ee.getAttributeNode("xsi:type").getNodeValue(),
						2, true);
				log("SubtypeName: "
						+ ee.getElementsByTagName("SubtypeName").item(0)
								.getTextContent(), 2, false);

				if (cleanup && remove_unPowered && !remove_noBeacon) {
					if (!powered) {
						powered = checkForPower(ee);
						if (powered)
							delete = false;
					}

				} else if (cleanup && remove_noBeacon && !remove_unPowered) {
					if (!beaconed) {
						beaconed = checkForBeacon(ee);
						if (beaconed)
							delete = false;
					}

				} else if (cleanup && remove_noBeacon && remove_unPowered) {
					if (!beaconed | !powered) {
						if (!beaconed) {
							beaconed = checkForBeacon(ee);
							if (beaconed)
								if (powered)
									delete = false;

						}
						if (!powered) {
							powered = checkForPower(ee);
							if (powered)
								if (beaconed)
									delete = false;

						}
					}
				} else if (cleanup && !remove_noBeacon && !remove_unPowered) {
					delete = false;
				}

				if (deactivateIdleMovementTurret) {
					deactivateIdleMovementOnTurrets(ee);
				}

				if (disableBlocks) {
					disableBlocks(ee);
				}
			}

		}
		return delete;
	}

	public boolean checkInventoryFor(Element element, String block, String item) {
		if (element.getAttributeNode("xsi:type").getNodeValue().equals(block)) {
			NodeList inventoryNode = element.getElementsByTagName("Inventory");
			for (int u = 0; u < inventoryNode.getLength(); u++) {
				Node inventoryN = inventoryNode.item(u);
				Element inventoryee = (Element) inventoryN;

				NodeList itemsNode = inventoryee.getElementsByTagName("Items");
				for (int v = 0; v < itemsNode.getLength(); v++) {

					Node itemsn = itemsNode.item(v);
					Element itemsee = (Element) itemsn;
					// System.out.println("items = true");
					if (itemsee.hasChildNodes()) {
						if (itemsee.getElementsByTagName("SubtypeName").item(0)
								.getTextContent().equals(item)) {
							return true;
						}
					}
				}

			}
		}
		return false;
	}

	private boolean checkForPower(Element element) {

		switch (element.getAttributeNode("xsi:type").getNodeValue()) {
		case "MyObjectBuilder_SolarPanel":
		case "MyObjectBuilder_BatteryBlock":
			return true;

		case "MyObjectBuilder_Reactor":
			if (checkInventoryFor(element, element.getAttributeNode("xsi:type")
					.getNodeValue(), "Uranium")) {
				return true;
			}
		}

		return false;
	}

	public boolean deactivateIdleMovementOnTurrets(Element element) {
		switch (element.getAttribute("xsi:type")) {
		case "MyObjectBuilder_LargeGatlingTurret":
		case "MyObjectBuilder_LargeMissileTurret":
		case "MyObjectBuilder_LargeInteriorTurret":
			element.getElementsByTagName("EnableIdleRotation").item(0)
					.setTextContent("false");
			modified++;
			return true;

		}

		return false;
	}

	public boolean disableBlocks(Element element) {

		switch (element.getAttribute("xsi:type")) {
		case "MyObjectBuilder_GravityGenerator":
		case "MyObjectBuilder_TimerBlock":
		case "MyObjectBuilder_SensorBlock":
		case "MyObjectBuilder_ReflectorLight":
		case "MyObjectBuilder_InteriorLight":
		case "MyObjectBuilder_Refinery":
		case "MyObjectBuilder_Assembler":
		case "MyObjectBuilder_Projector":
		case "MyObjectBuilder_Beacon":
		case "MyObjectBuilder_RadioAntenna":
		case "MyObjectBuilder_ShipWelder":
		case "MyObjectBuilder_ShipGrinder":
		case "MyObjectBuilder_MotorAdvancedStator":
		case "MyObjectBuilder_MotorAdvancedRotor":
			element.getElementsByTagName("Enabled").item(0)
					.setTextContent("false");
			modified++;
			return true;
			// Test Disable FTL
			// case "MyObjectBuilder_Gyro":
			// if (element.getElementsByTagName("SubtypeName").item(0)
			// .getTextContent().equals("LargeFTL")
			// || element.getElementsByTagName("SubtypeName").item(0)
			// .getTextContent().equals("SmallFTL")) {
			// System.out.println("######## Found FTL ########");
			// element.getElementsByTagName("Enabled").item(0)
			// .setTextContent("false");
			// element.getElementsByTagName("GyroOverride").item(0)
			// .setTextContent("false");
			// element.getElementsByTagName("ColorMaskHSV").item(0)
			// .getAttributes();
			// }
			// break;
		}

		return false;
	}

	public boolean checkForBeacon(Element element) {

		boolean beaconed = false;

		switch (element.getAttributeNode("xsi:type").getNodeValue()) {
		case "MyObjectBuilder_Beacon":
			if (!beaconed)
				beaconed = true;
			return true;

		}

		return false;
	}

	public void deleteElement(Element e) {
		e.getParentNode().removeChild(e);
		deleted++;
	}

	public void deleteElement(Document doc) {
		NodeList nodes = doc.getElementsByTagName("MyObjectBuilder_EntityBase");

		for (int i = 0; i < nodes.getLength(); i++) {
			Element parent = (Element) nodes.item(i);
			Element entity = (Element) parent.getElementsByTagName("EntityId")
					.item(0);
			if (entityRemove.contains(entity.getTextContent())) {
				parent.getParentNode().removeChild(parent);
				this.deleted++;
			}
		}
	}

	private Document readXML(String path) {

		try {
			File fXmlFile = new File(path);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();
			FileInputStream in = new FileInputStream(fXmlFile);
			Document doc = dBuilder.parse(in, "UTF-8");

			doc.getDocumentElement().normalize();

			return doc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
