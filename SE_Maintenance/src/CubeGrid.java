import java.util.List;

import org.w3c.dom.Node;

public class CubeGrid {

	private String type;
	private String entityId;
	private String persistentFlags;
	private String gridSizeEnum;
	private List<CubeBlock> cubeBlocks;
	private boolean powered;
	private boolean beacon;
	private Node node;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getPersistentFlags() {
		return persistentFlags;
	}

	public boolean isBeacon() {
		return beacon;
	}

	public void setBeacon(boolean beacon) {
		this.beacon = beacon;
	}

	public void setPersistentFlags(String persistentFlags) {
		this.persistentFlags = persistentFlags;
	}

	public String getGridSizeEnum() {
		return gridSizeEnum;
	}

	public void setGridSizeEnum(String gridSizeEnum) {
		this.gridSizeEnum = gridSizeEnum;
	}

	public List<CubeBlock> getCubeBlocks() {
		return cubeBlocks;
	}

	public void setCubeBlocks(List<CubeBlock> cubeBlocks) {
		this.cubeBlocks = cubeBlocks;
	}

	public CubeGrid() {

	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public CubeGrid(String type, String entityId, String persistentFlags,
			String gridSizeEnum, List<CubeBlock> cubeBlocks, boolean powered,
			boolean beacon, Node node) {
		super();
		this.type = type;
		this.entityId = entityId;
		this.persistentFlags = persistentFlags;
		this.gridSizeEnum = gridSizeEnum;
		this.cubeBlocks = cubeBlocks;
		this.powered = powered;
		this.beacon = beacon;
		this.node = node;
	}

}
