public class PositionAndOrientation {

	private Position position;
	private Position forward;
	private Position up;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getForward() {
		return forward;
	}

	public void setForward(Position forward) {
		this.forward = forward;
	}

	public Position getUp() {
		return up;
	}

	public void setUp(Position up) {
		this.up = up;
	}

	public PositionAndOrientation(Position position, Position forward,
			Position up) {
		super();
		this.position = position;
		this.forward = forward;
		this.up = up;
	}

}
