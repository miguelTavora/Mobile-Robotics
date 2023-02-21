
public class FollowWall {

	private final int MIN_ANGLE = 5;
	private final int DISTANCE_LINE = 20;
	private final int IDEAL_DISTANCE = 50;
	private MyRobotLego myRobot;
	private TrajectoriesGestor gestor;

	public FollowWall(MyRobotLego myRobot, TrajectoriesGestor gestor) {
		this.myRobot = myRobot;
		this.gestor = gestor;
	}

	// return the values chosen on trajectory
	public String calcutate() {
		
		double[] dt1dt2Alpha = this.getDistancesAndAlpha();
		if(dt1dt2Alpha == null) {
			this.gestor.collidedGoBack();
			return "collided!";
		}
		
		// 1. Method
		// straighten the robot then go to the line 
		//straightenRobot((int)dt1dt2Alpha[0], (int)dt1dt2Alpha[1], dt1dt2Alpha[2]);
		//String result = alternativeLine();
		
		// 2. Method
		// straighten the robot then get the equation of the ideal line so that we get a point
		//straightenRobot((int)dt1dt2Alpha[0], (int)dt1dt2Alpha[1], dt1dt2Alpha[2]);
		//int[] x3y3 = calcIdealLine((int) dt1dt2Alpha[0], (int) dt1dt2Alpha[1],  Math.toRadians(dt1dt2Alpha[2]));
		//String result = gestor.getTrajectory(x3y3[0], x3y3[1], 0);
		
		// 3. Method
		// just get the line equation then get the point
		int[] x3y3 = calcIdealLine((int) dt1dt2Alpha[0], (int) dt1dt2Alpha[1],  Math.toRadians(dt1dt2Alpha[2]));
		String result = gestor.getTrajectory(x3y3[0], x3y3[1], (int)dt1dt2Alpha[2]);
		
		return result;
		
	}
	
	
	public int[] calcIdealLine (int dist0, int dist1, double alphaRad) {
		// resultados mal
		double dp1 = dist1 * Math.cos(alphaRad);
		System.out.println("dp1: "+dp1);
		
		double dort = Math.abs(dp1 - IDEAL_DISTANCE);
		System.out.println("dort: "+dort);
		
		double h = dort / Math.cos(alphaRad);
		System.out.println("h: "+h);
		
		double x2 = dort * Math.sin(alphaRad);
		double y2 = dort * Math.cos(alphaRad);
		double x1 = 0;
		double y1 = h;
		System.out.println("x2: "+x2);
		System.out.println("y2: "+y2);
		System.out.println("x1: "+x1);
		System.out.println("y1: "+y1);
		
		// this formula is x = mx + b;
		double m = (y1 - y2) / (x2 - x1);

		// if the robot is between the wall and the ideal line b = h 
		// if the robot is beyond the ideal line b = -h
		double b;
		
		if(dp1 > IDEAL_DISTANCE) {
			b = -h;
		}
		else {
			b = h;
		}
		
		
		System.out.println("m: "+m);	
		System.out.println("b: "+b);
		
		//double x3 = (100);
		double x3 = (x2+40);
		double y3 = (m*x3 + b);
		System.out.println("x3: "+x3);
		System.out.println("y3: "+y3);	
		
		int[] result = {(int)x3, (int)y3};
		
		return result;	
		
	}
	
	/*private String alternativeLine() {
		
		int dist = this.myRobot.getSensorUs();
		int distToLine = dist - IDEAL_DISTANCE;
		String result = gestor.getTrajectory(50,distToLine,0);
		
		return result;
	}*/
	
	public void straightenRobot(int dist0, int dist1, double alpha) {
		int curve = (dist1 > dist0) ? 1 : 0;
		
		// it only curves when the radius obtained is bigger than 5 otherwise gets a big error
		if(alpha > MIN_ANGLE) {
			
			// when the goes away from the wall curves to the other-side
			switch(curve) {
			case 0:
				// the radius of the curve is 7 because this the one with lowest error
				myRobot.curveLeft((int) alpha, 10, true);
				myRobot.stop();
				//System.out.println("alpha: "+alpha); 
				break;
			case 1:
				myRobot.curveRight((int) alpha, 10, true);
				myRobot.stop();
				break;
			}
		}
	}
	
	private double[] getDistancesAndAlpha() {
		int dist0 = this.myRobot.getSensorUs();
		System.out.println("dist0: "+dist0);
		
		// if collided must stop
		boolean collided = myRobot.line(DISTANCE_LINE, true);
		
		if(collided) return null;
	
		int dist1 = this.myRobot.getSensorUs();
		System.out.println("dist1: "+dist1);
		
		
		
		double xs = Math.abs(dist0 - dist1);
		double xs2 = xs/ DISTANCE_LINE;
		
		double a =  Math.atan(xs2);
		//System.out.println("aRad: "+a);
		
		// round the number adding 0.5
		double aDegrees = Math.toDegrees(a)+0.5;
		System.out.println("aDegrees: "+aDegrees);
		
		double[] result = {(double) dist0, (double) dist1, aDegrees};
		
		return result;
	}

}
