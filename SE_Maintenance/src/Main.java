import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Space Engineers - Maintenance
	 * 
	 * Parameter: <SavePath> <BackupPath> <Params[]> Params[]: -
	 * 
	 */

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

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("h")) {
			lvFormater.printHelp("Help ", options);
		}
		String argSavePath = "";
		if (cmd.hasOption("S")) {
			argSavePath = cmd.getOptionValue("S");
		}

		boolean argInfo = false;
		if (cmd.hasOption("v")) {
			argInfo = true;
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
		String path = "";
		if (argSavePath.equals("")) {
			path = "C:\\ProgramData\\SpaceEngineersDedicated\\Armageddon\\Saves\\Survival\\SANDBOX_0_0_0_.sbs";
		} else {
			path = argSavePath + "\\SANDBOX_0_0_0_.sbs";
		}
		boolean info = argInfo; // Informations doing the Work

		boolean cleanup = argCleanup; // Activating Cleanup-Mode

		boolean disableBlocks = argDeAll;

		/*
		 * ===================================================
		 * ================== Config Cleanup =================
		 * ===================================================
		 */

		boolean remove_floatingObjects = argRemFloating; // Removing Floating
		boolean remove_unPowered = argNoPower; // removing grids without power
		boolean remove_noBeacon = argBeacon; // removing grids without beacon
		boolean remove_noOwnerShip = true;

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

		//
		// ################## Code #####################
		//
		// Do not Touch
		//
		// #############################################

		Main main = new Main();
		if (info | disableBlocks | cleanup)
			main.startMaintanence(path, info, cleanup, disableBlocks,
					remove_unPowered, remove_noBeacon, remove_floatingObjects,
					remove_noOwnerShip);
	}

	public void startMaintanence(String path, boolean info, boolean cleanup,
			boolean disableBlocks, boolean remove_unPowered,
			boolean remove_noBeacon, boolean floatingObjects,
			boolean noOwnerShip) {

		Document doc = readXML(path);
		List<CubeGrid> cg = analyseXML(doc);

		// Infos
		if (info)
			info(cg);

		if (cleanup)
			doc = prepareCleanupWorld(doc, cg, remove_unPowered,
					remove_noBeacon, noOwnerShip, floatingObjects);

		if (disableBlocks)
			doc = prepareDisableBlocks(doc, cg);

		// Writing (save) changes to XML
		if (cleanup | disableBlocks)
			saveXML(doc, path);

		System.out.println("### Results ###");
		System.out.println("	Changes:");
		System.out.println("		Deleted: " + this.deleted);
		System.out.println("		Modified: " + this.modified);

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

	private Document prepareDisableBlocks(Document doc, List<CubeGrid> grid) {

		NodeList nodes = doc.getElementsByTagName("MyObjectBuilder_CubeBlock");

		for (int j = 0; j < nodes.getLength(); j++) {
			Node n = nodes.item(j);
			Element ee = (Element) n;

			if (ee.getNodeName() != null) {

				switch (ee.getAttribute("xsi:type")) {
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
					ee.getElementsByTagName("Enabled").item(0)
							.setTextContent("false");
					modified++;
					break;
				case "MyObjectBuilder_Gyro":
					if (ee.getElementsByTagName("SubtypeName").item(0)
							.getTextContent().equals("LargeFTL")
							|| ee.getElementsByTagName("SubtypeName").item(0)
									.getTextContent().equals("SmallFTL")) {
						System.out.println("######## Found FTL ########");
						ee.getElementsByTagName("Enabled").item(0)
								.setTextContent("false");
						ee.getElementsByTagName("GyroOverride").item(0)
								.setTextContent("false");
						ee.getElementsByTagName("ColorMaskHSV").item(0)
								.getAttributes();
					}
					break;
				}

			}
		}

		return doc;
	}

	private Document prepareCleanupWorld(Document doc, List<CubeGrid> grid,
			boolean noPowered, boolean noBeacon, boolean noOwnerShip,
			boolean floatingObjects) {

		NodeList nodes = doc.getElementsByTagName("MyObjectBuilder_EntityBase");
		List<String> entityRemove = new ArrayList<String>();

		for (CubeGrid s : grid) {
			if (s.getType().equals("MyObjectBuilder_CubeGrid")) {

				if (!entityRemove.contains(s.getEntityId())) {
					boolean delete = false;

					if ((noPowered && noBeacon)) {
						if (!(s.isPowered() && s.isBeacon()))
							delete = true;
					} else {

						if (noPowered && !noBeacon) {
							if (!s.isPowered())
								delete = true;
						}

						if (noBeacon && !noPowered) {
							if (!s.isBeacon())
								delete = true;
						}
					}
					if (delete)
						entityRemove.add(s.getEntityId());
				}
			}

			if (floatingObjects) {
				if (s.getType().equals("MyObjectBuilder_CubeGrid")) {
					entityRemove.add(s.getEntityId());
				}
			}

		}

		for (int i = 0; i < nodes.getLength(); i++) {
			Element parent = (Element) nodes.item(i);
			Element entity = (Element) parent.getElementsByTagName("EntityId")
					.item(0);
			if (entityRemove.contains(entity.getTextContent())) {
				parent.getParentNode().removeChild(parent);
				this.deleted++;
			}
		}

		return doc;
	}

	private Document readXML(String path) {

		try {
			File fXmlFile = new File(path);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder;

			dBuilder = dbFactory.newDocumentBuilder();
			FileInputStream in = new FileInputStream(fXmlFile);
			Document doc = dBuilder.parse(in, "UTF-16");

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

	private List<CubeGrid> analyseXML(Document doc) {

		List<CubeGrid> cubeGrid = new ArrayList<CubeGrid>();

		try {
			NodeList nList = doc
					.getElementsByTagName("MyObjectBuilder_EntityBase");

			for (int i = 0; i < nList.getLength(); i++) {
				CubeGrid cg = new CubeGrid();
				List<CubeBlock> cl = new ArrayList<CubeBlock>();
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) nNode;
					cg.setNode(e);
					cg.setPowered(false);
					cg.setType(e.getAttribute("xsi:type"));
					cg.setEntityId(e.getElementsByTagName("EntityId").item(0)
							.getTextContent());

					NodeList nestedNode = e
							.getElementsByTagName("MyObjectBuilder_CubeBlock");
					for (int j = 0; j < nestedNode.getLength(); j++) {
						Node n = nestedNode.item(j);
						Element ee = (Element) n;
						CubeBlock cb = new CubeBlock();
						if (ee.getNodeName() != null) {
							cb.setType(ee.getAttributeNode("xsi:type")
									.getNodeValue());
						}
						cb.setSubType(ee.getElementsByTagName("SubtypeName")
								.item(0).getTextContent());
						// Inventory if subtype is reactor
						if (cb.getType().equals("MyObjectBuilder_Reactor")
								|| cb.getType().equals(
										"MyObjectBuilder_SolarPanel")
								|| cb.getType().equals(
										"MyObjectBuilder_BatteryBlock")) {
							if (cb.getType().equals(
									"MyObjectBuilder_SolarPanel")
									|| cb.getType().equals(
											"MyObjectBuilder_BatteryBlock")) {
								cg.setPowered(true);
							}

							if (cb.getType().equals("MyObjectBuilder_Reactor")) {
								NodeList inventoryNode = ee
										.getElementsByTagName("Inventory");
								// System.out.println("inventory now");
								for (int u = 0; u < inventoryNode.getLength(); u++) {
									Node inventoryN = inventoryNode.item(u);
									Element inventoryee = (Element) inventoryN;

									NodeList itemsNode = inventoryee
											.getElementsByTagName("Items");
									for (int v = 0; v < itemsNode.getLength(); v++) {

										Node itemsn = itemsNode.item(v);
										Element itemsee = (Element) itemsn;
										// System.out.println("items = true");
										if (itemsee.hasChildNodes()) {
											if (itemsee
													.getElementsByTagName(
															"SubtypeName")
													.item(0).getTextContent()
													.equals("Uranium")) {
												cg.setPowered(true);
											}
										}
									}

								}
							}
						}

						if (cb.getType().equals("MyObjectBuilder_Beacon")) {
							cg.setBeacon(true);
						}

						cl.add(cb);
					}
					cg.setCubeBlocks(cl);
				}
				cubeGrid.add(cg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cubeGrid;
	}

}
