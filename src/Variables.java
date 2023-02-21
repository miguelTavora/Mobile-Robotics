
public class Variables {
	
	// variable used to define if will use the prof library or
	// our implementation
	private final boolean MY_IMPLEMENTATION = true;

	private String robotName;
	private boolean onOff, debug;
	private int radius, angle, distance;
	private int xf, yf, of;
	private RobotLegoEV3 robot;
	private MyRobotLego myRobot;
	private TrajectoriesGestor gestor;
	private FollowWall follow;


	public Variables() {
		robotName= "EVB";
		onOff = false;
		debug = true;
		radius = 10;
		angle = 90;
		distance = 20;
		xf = 70;
		yf = 40;
		of = 20;
		robot = new RobotLegoEV3();
		myRobot = new MyRobotLego();
		gestor = new TrajectoriesGestor(myRobot);
		follow = new FollowWall(myRobot, gestor);
		
	}
	
	public String getNomeRobot() {
		return robotName;
	}

	public void setNomeRobot(String nomeRobot) {
		this.robotName = nomeRobot;
	}

	public boolean isOnOff() {
		return onOff;
	}

	public void setOnOff(boolean onOff) {
		this.onOff = onOff;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngulo(int angle) {
		this.angle = angle;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public int getXf() {
		return this.xf;
	}
	
	public void setXf(int Xf) {
		this.xf = Xf;
	}
	
	public int getYf() {
		return this.yf;
	}
	
	public void setYf(int Yf) {
		this.yf = Yf;
	}
	
	public int getOf() {
		return this.of;
	}
	
	public void setOf(int Of) {
		this.of = Of;
	}
	
	public RobotLegoEV3 getRobot() {
		return this.robot;
	}
	
	public MyRobotLego getMyRobot() {
		return this.myRobot;
	}
	
	public TrajectoriesGestor getTrajectoriesGestor() {
		return this.gestor;
	}
	
	public FollowWall getFollowWall() {
		return this.follow;
	}
	
	public boolean getMyImplentation() {
		return this.MY_IMPLEMENTATION;
	}

}
