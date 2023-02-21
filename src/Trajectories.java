
public class Trajectories {
	
	private double yc1Tj2;
	private double yc2Tj2;
	
	private double yc1Tj3;
	private double yc2Tj3;
	
	private double alphaTj1;
	
	
	public int[] trajectory1(int Xf, int Yf, int Of) {
		//System.out.println("Tragetória 1");
		// must convert to radians, because the calculations is based on radians
		double radOf = Math.toRadians(Of);
		
		
		// quadratic formula with a, b and c
		double a = 2 + 2 * Math.cos(radOf);
		double b = 2*Yf*(1- Math.cos(radOf))+2*Xf*Math.sin(radOf);
		double c = -(Math.pow(Xf, 2) + Math.pow(Yf, 2));
		
		// calculates the radius
		double radius = distanceBetweenPoints(a, b, c);
		
		
		// calculates the alpha
		double alpha = Math.toDegrees(Math.asin((Xf - radius * Math.sin(radOf)) / (2 * radius)));
		this.alphaTj1 = alpha;
		//System.out.println("a: "+a);
		//System.out.println("b: "+b);
		//System.out.println("c: "+c);
		
		//System.out.println("x: "+radius);
		//System.out.println("alpha: "+alpha);
		
		double halfWheelCenterMass = MyRobotLego.WHEEL_CENTER_MASS/2;
		double factor = ((radius + halfWheelCenterMass) / (radius - halfWheelCenterMass));
		//System.out.println("factor: "+ factor);
		
		// without the cast is teorical, with cast is pratical
		double velLeftWheelT = 2*MyRobotLego.VELOCITY / (factor+1);
		int velLeftWheelP = (int) velLeftWheelT;
		double velRightWheelT =  2*MyRobotLego.VELOCITY - velLeftWheelP;
		int velRightWheelP = (int) velRightWheelT;
		
		// must convert the factor to the pratical one
		factor = (double)velRightWheelP / (double)velLeftWheelP;
		
		//System.out.println("velLeftWheel: "+ velLeftWheelP);
		//System.out.println("velRightWheel: "+ velRightWheelT);
		//System.out.println("velRightWheel: "+ velRightWheelP);
		//System.out.println("factor: "+ factor);
		
		double radiusP = (halfWheelCenterMass) * ((factor + 1) / (factor - 1));
		//System.out.println(radiusP);
		
		double xc2 = Xf - radiusP * Math.sin(radOf);
		double yc2 = Yf + radiusP * Math.cos(radOf);
		//System.out.println("xc2: " + xc2);
		//System.out.println("yc2: " + yc2);
		
		int xc1 = 0;
		
		double d12 = Math.sqrt((Math.pow(xc1 - xc2, 2) + Math.pow(radiusP - yc2, 2)));
		//System.out.println("d12: " + d12);
		
		double alpha1 =  Math.toDegrees(Math.acos(xc2/d12));
		//System.out.println("alpha1: "+ alpha1);
		double alpha2 = Of - alpha1;
		//System.out.println("alpha2: "+ alpha2);
		
		int[] results = { (int)alpha1, (int)radiusP, (int)d12,  (int)alpha2, (int)radiusP};
		//System.out.println("results: "+Arrays.toString(results));
		return results;
		
	}
	
	public int[] trajectory2(int Xf, int Yf, int Of) {
		//System.out.println("Tragetória 2");
		double radOf = Math.toRadians(Of);
		
		
		double a  = 2 - (2 * Math.cos(radOf));
		double b = (2 * Yf * (1 + Math.cos(radOf)) ) - (2 * Xf * Math.sin(radOf));
		double c = -( Math.pow(Xf, 2) + Math.pow(Yf, 2));
		
		
		double r = distanceBetweenPoints(a, b ,c);
		//System.out.println("R: "+r);
		
		double alpha = Math.asin((Xf + r * Math.sin(radOf)) / (2 * r));
		alpha = Math.toDegrees(alpha);
		//System.out.println("alpha: "+alpha);
		
		double factor = (r + (MyRobotLego.WHEEL_CENTER_MASS / 2)) / (r - (MyRobotLego.WHEEL_CENTER_MASS / 2));
		//System.out.println("factor: "+factor);
		double vel2 = 2/ (factor + 1) * MyRobotLego.VELOCITY;
		//System.out.println("vel2: "+vel2);
		int tVel2 = (int) vel2;
		//System.out.println("vel2P: "+tVel2);
		int vel1 = 2 * MyRobotLego.VELOCITY - tVel2;
		//System.out.println("vel1: "+vel1);
		// makes the factor pratical
		factor = (double)vel1 / (double)tVel2;
		//System.out.println("factorP: "+factor);
		double radiusP = (MyRobotLego.WHEEL_CENTER_MASS / 2) * ((factor + 1) / (factor - 1));
		
		//System.out.println("radius P: "+radiusP);
		
		
		double[] c1 = {0, radiusP};
		double[] c2 = {radiusP * Math.sin(radOf) + Xf, Yf - (radiusP * Math.cos(radOf))};
		this.yc1Tj2 = c1[1];
		this.yc2Tj2 = c2[1];
		//System.out.println("c2: " + Arrays.toString(c2));
		
		
		double d12 = Math.pow(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2), 0.5);
		//System.out.println("d12: "+d12);
		
		
		double delta = Math.acos(radiusP/ (d12 /2 ));
		//double delta = Math.toDegrees(Math.acos(radiusP/ (d12 /2 )));
		//System.out.println("delta: "+delta);
		
		double dStraight = d12 * Math.sin(delta);
		//System.out.println("dStraight: "+ dStraight);
		
		double alphaP = Math.toDegrees(Math.asin(c2[0]/d12) - delta);
		//System.out.println("alphaP: "+ alphaP);
		
		double angleLastCurve = alphaP - Of;
		
		// results used to move the robot
		int[] results = { (int)alphaP, (int)radiusP, (int)dStraight,  (int)angleLastCurve, (int)radiusP};
		
		return results;
		
		
	}
	
	public int[] trajectory3(int Xf, int Yf, int Of) {
		//System.out.println("Tragetória 3");
		double radOf = Math.toRadians(Math.abs(Of));
		
		double a = 2 - 2 * Math.cos(radOf);
		double b = 2 * Yf * (1 + Math.cos(radOf)) + 2 * Xf * Math.sin(radOf);
		double c = - (Math.pow(Xf, 2) + Math.pow(Yf, 2));
		
		double r = distanceBetweenPoints(a, b, c);
		r = (double) Math.round(r * 100) / 100;
		
		double delta = Math.asin((Xf - r * Math.sin(radOf)) / (r * 2)) ;
		
		// (double) Math.round(alpha * 100) / 100 rounds 2 decimals places
		double alpha = 180 - Math.toDegrees(delta);
		alpha = (double) Math.round(alpha * 100) / 100;

		
		double factor = ((r + (MyRobotLego.WHEEL_CENTER_MASS/2)) / (r - (MyRobotLego.WHEEL_CENTER_MASS/2)));
		
		
		double vel2 = (2 / (factor + 1)) * MyRobotLego.VELOCITY;
		
		int vel2P = (int) vel2;
		int vel1P = 2 * MyRobotLego.VELOCITY - vel2P;

		
		double factorP = (double)vel1P / (double)vel2P;
		
		double rPratical = (MyRobotLego.WHEEL_CENTER_MASS/2) * ((factorP + 1) /(factorP - 1));
		
		double[] c1 = {0, rPratical};
		double[] c2 = {Xf - (Math.sin(radOf) * rPratical), Yf - (Math.cos(radOf) * rPratical)};
		this.yc1Tj3 = c1[1];
		this.yc2Tj3 = c2[1];
		
		double d12 = Math.pow(Math.pow(c1[0] - c2[0], 2) + Math.pow(c1[1] - c2[1], 2), 0.5);

		
		double deltaP = Math.acos(rPratical / (d12/ 2));

		double beta = Math.asin(c2[0] / d12);

		double dStraight = d12 * Math.sin(deltaP);
		
		
		double alphaP = 180 - (Math.toDegrees(beta) + Math.toDegrees(deltaP));
		
		
		int[] result = {(int)Math.round(alphaP), (int)Math.round(rPratical), (int)Math.round(dStraight),
				(int)Math.round((alphaP- Of)), (int)Math.round(rPratical)};

		return result;
	}
	
	
	// only applies the solver formula when it's different of 0
	private double distanceBetweenPoints(double a, double b, double c) {
		// when the a is really low value
		if (a < 0.0009 && a > -0.0009) {
			double radius = (c * (-1)) / b;
			return radius;
		}
		else {
			// makes the calc
			double sqrt = Math.sqrt((Math.pow(b, 2) - (4 * a * c)));
			
			// does the +- 
			double minusB = (b*(-1)) - sqrt;
			double plusB = (b*(-1)) + sqrt;
			
			// must check which one is positive
			double radius = (minusB > 0) ? minusB / (2* a)  : plusB / (2 * a);
			return radius;
		}
	}
	
	public double getYc1Tj2() {
		return this.yc1Tj2;
	}
	
	public double getYc2Tj2() {
		return this.yc2Tj2;
	}
	
	public double getYc1Tj3() {
		return this.yc1Tj3;
	}
	
	public double getYc2Tj3() {
		return this.yc2Tj3;
	}
	
	public double getAlphaTj1() {
		return this.alphaTj1;
	}
}
