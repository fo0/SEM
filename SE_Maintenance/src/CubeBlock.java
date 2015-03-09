public class CubeBlock {

	private String type;
	private String subType;
	private Position min;
	private Position colorMaskHSV;
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

	public Position getMin() {
		return min;
	}

	public void setMin(Position min) {
		this.min = min;
	}

	public Position getColorMaskHSV() {
		return colorMaskHSV;
	}

	public void setColorMaskHSV(Position colorMaskHSV) {
		this.colorMaskHSV = colorMaskHSV;
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

	public CubeBlock(String type, String subType, Position min,
			Position colorMaskHSV, String shareMode, String deformationRatio) {
		super();
		this.type = type;
		this.subType = subType;
		this.min = min;
		this.colorMaskHSV = colorMaskHSV;
		this.shareMode = shareMode;
		this.deformationRatio = deformationRatio;
	}

}
