public class CubeBlock {

	private String type;
	private String subType;
	private String shareMode;
	private String deformationRatio;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getShareMode() {
		return shareMode;
	}

	public void setShareMode(String shareMode) {
		this.shareMode = shareMode;
	}

	public String getDeformationRatio() {
		return deformationRatio;
	}

	public void setDeformationRatio(String deformationRatio) {
		this.deformationRatio = deformationRatio;
	}

	public CubeBlock() {

	}

	public CubeBlock(String type, String subType, String shareMode,
			String deformationRatio) {
		super();
		this.type = type;
		this.subType = subType;
		this.shareMode = shareMode;
		this.deformationRatio = deformationRatio;
	}

}
