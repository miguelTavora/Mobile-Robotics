import java.util.concurrent.Semaphore;

public class MyRobotLego {
	
	public static final int VELOCITY = 50;

	// total distance between wheels
	public static final float WHEEL_CENTER_MASS = 9.5f;
	
	private InterpretadorEV3 interpretador;
	
	private final float WHEEL_RADIUS = 2.73f;
	
	// multiple possible states, to the line and curve
	private enum states {START, LEFT, RIGHT, CALCULATE, WAIT, END};
	
	// time of bluetooth to make communication
	private final int COMMUNICATION_TIME = 200;
	
	
	// obtain the right wheel
	private final int RIGHT_WHEEL = InterpretadorEV3.OUT_C;
	private final int LEFT_WHEEL = InterpretadorEV3.OUT_B;
	private final int RIGHT_LEFT_WHEELS = InterpretadorEV3.OUT_BC;
	private final int TOUCH_SENSOR = InterpretadorEV3.S_1;
	private final int US_SENSOR = InterpretadorEV3.S_2;
	
	// to prevent multiple threads access the multiple commands simultaneously 
	private Semaphore semaphore;
	
	public MyRobotLego() {
		interpretador = new InterpretadorEV3();
		semaphore = new Semaphore(1);
	}
	
	// open communication with robot EV3
	public boolean openEV3(String robotName) {
		// because its used multi-threading, only one access at the same time
		try {
			semaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// connects to the robot
		boolean result = this.interpretador.OpenEV3(robotName);
		// releases the other threads
		semaphore.release();
		return result;
	}
	
	// close the communication with robot EV3
	public void closeEV3() {
		// because its used multi-threading 
		try {
			semaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// remove all the commands previously sent
		this.interpretador.ResetAll();
		try {
			Thread.sleep(COMMUNICATION_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.interpretador.CloseEV3();
		// releases the thread to another thread uses
		semaphore.release();
	}
	
	// realizes the straight line
	// positive values go forward, negative values goes backwards
	// arg sensor is when we want to know with touch sensor if robot collided or not
	// return is only useful when sensor is true
	// and returns true when robot collided
	public boolean line(int distance, boolean sensor) {
		System.out.println("detro line");
		// this way a thread will only start after do a command
		try {
			semaphore.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("depois semaforo");
		// current rotations to make the line
		int initialRotations = this.interpretador.RotationCount(RIGHT_WHEEL);
		
		// angle that must be done by the robot
		double ang = 0;

		states currentState = states.START;
		
		// when the distance is positive goes forward
		// if negative goes backwards
		int positiveSignal = (distance > -1) ? 1 : -1;

		// variable used to prediction technique
		int deltaActual = initialRotations;
		
		// variable used to detect collision
		boolean collided =  false;

		while (currentState != states.END) {
			switch (currentState) {
			case START:
				// the value of the distance must be positive
				// so we use the abs to set positive the value
				ang = ((distance / WHEEL_RADIUS) * 180 / Math.PI)+initialRotations;
				System.out.println("ang: "+ang);
				// sends the message to go forward
				this.interpretador.OnFwd(RIGHT_WHEEL, VELOCITY * positiveSignal, LEFT_WHEEL, VELOCITY * positiveSignal);

				currentState = states.CALCULATE;
				break;

			case CALCULATE:
				int rotationCounts = this.interpretador.RotationCount(RIGHT_WHEEL);

				// prediction value
				int deltaD = rotationCounts - deltaActual;
				System.out.println("rot current: "+rotationCounts+deltaD);

				// verifies if collided or completed the rotations
				// the last argument is when it goes back the value must be less than before
				int stateEnd = this.calculateStateEnd(rotationCounts, ang, deltaD, sensor, (positiveSignal < 0));

				// rotation count completes the task
				// if sensor is on and collides goes back
				// otherwise keep running
				collided = (stateEnd == 2) ? true : false;
				currentState = (stateEnd > 0) ? states.END : states.WAIT;
				System.out.println("collided: "+collided);
				System.out.println("state: "+currentState);
				
				// sets the delta to the current rotations, to predict the value and gets the
				// least error possible
				deltaActual = rotationCounts;
				break;

			case WAIT:
				System.out.println("esperar: ");
				// to not overload the robot with messages
				// must have a delay
				try {
					Thread.sleep(COMMUNICATION_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentState = states.CALCULATE;
				break;

			}
		}
		
		// after complete of the command sends a release to other thread starts running
		semaphore.release();
		
		//if no one sends the command to stop it keep on going until receive a command to stop
		return collided;
	}
	
	// angle is in degrees, and radius is in cm
	public boolean curveRight(int angle, int radius, boolean sensor) {
		// this way a thread will only start after do a command
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// makes the right curve
		boolean collided = curve(true, angle, radius, sensor);

		
		// after complete the command gives one permission to another thread is freed
		semaphore.release();
		
		// if no one sends a stop command it keeps going until send another command or
		// stop command
		return collided;
	}
	
	
	public boolean curveLeft(int angle, int radius, boolean sensor) {
		// this way a thread will only start after do a command
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// makes the left curve
		boolean collided = curve(false, angle, radius, sensor);
		
		// after complete the command gives one permission to another thread is freed
		// if no one sends a stop command it keeps going until send another command or stop command
		semaphore.release();
		
		return collided;
	}
	
	// creates a curve with state of curve right and left
	private boolean curve(boolean right, int angle, int radius, boolean sensor) {

		states currentState = (right) ? states.RIGHT : states.LEFT;
		
		int[] currentRotations = this.interpretador.RotationCount(RIGHT_WHEEL, LEFT_WHEEL);
		double meanRotations = Math.round((currentRotations[0] + currentRotations[1]) / 2);
		
		// to detect when it collided
		boolean collision = false;

		double ang = 0;
		// variable used to prediction technique
		double deltaActual = meanRotations;

		while (currentState != states.END) {
			switch (currentState) {
			case RIGHT:
				// calculations used to curve right
				double radiusRight = radius - (WHEEL_CENTER_MASS / 2);
				double radiusLeft = radius + (WHEEL_CENTER_MASS / 2);
				double factor = radiusLeft / radiusRight;

				int rightSpeed = (int) Math.round((VELOCITY * 2) / (1 + factor));
				int leftSpeed = (int) Math.round((factor * rightSpeed));

				double totalDist = angle * radius;
				
				ang = (Math.abs(totalDist) / WHEEL_RADIUS) + meanRotations;

				// the right wheel is C, and left
				this.interpretador.OnFwd(RIGHT_WHEEL, rightSpeed, LEFT_WHEEL, leftSpeed);
				currentState = states.CALCULATE;
				break;

			case LEFT:
				// calculations used to curve left
				radiusRight = radius + (WHEEL_CENTER_MASS / 2);
				radiusLeft = radius - (WHEEL_CENTER_MASS / 2);
				factor = radiusRight / radiusLeft;
				leftSpeed = (int) Math.round((VELOCITY * 2) / (1 + factor));
				rightSpeed = (int) Math.round((factor * leftSpeed));
				
				
				totalDist = angle * radius;
				
				ang = (Math.abs(totalDist) / WHEEL_RADIUS) + meanRotations;

				// the right wheel is C, and left
				this.interpretador.OnFwd(RIGHT_WHEEL, rightSpeed, LEFT_WHEEL, leftSpeed);
				currentState = states.CALCULATE;
				break;

			case CALCULATE:
				int[] rotationCounts = this.interpretador.RotationCount(RIGHT_WHEEL, LEFT_WHEEL);
				double result = Math.round((rotationCounts[0] + rotationCounts[1]) / 2);
				
				// prediction technique
				double prediction = result - deltaActual;
				
				
				int resultState = calculateStateEnd((int)result, ang, (int)prediction, sensor, false);
				
				// verify if the robot already complete the curve or not with prediction
				// or collided
				collision = (resultState == 2) ? true : false;
				currentState = (resultState > 0) ? states.END : states.WAIT;
				
				// used to store the previous value and make movement prediction
				deltaActual = result;
				break;

			case WAIT:
				// to not overload the robot with messages
				// must have a delay
				try {
					Thread.sleep(COMMUNICATION_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentState = states.CALCULATE;
				break;
			}
		}
		
		return collision;
	}
	
	// stops the robot
	// it can also uses the Float command, but it gets a bigger error
	// and the first time it as used gets a really high error
	public synchronized void stop() {
		try {
			semaphore.acquire();
			//this.interpretador.Float(RIGHT_LEFT_WHEELS);
			this.interpretador.Off(RIGHT_LEFT_WHEELS);
			Thread.sleep(COMMUNICATION_TIME);
			this.interpretador.ResetAll();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		semaphore.release();
	}
	
	
	// returns 2 -> if the robot collided
	// returns 1 -> rotation count completed
	// returns 0 -> don't complete the execution
	private int calculateStateEnd(int rotationCounts, double ang, int deltaD, boolean sensor, boolean reverse) {
		
		if(sensor) {
			int touched = this.interpretador.SensorTouch(TOUCH_SENSOR);
			if(touched == 1) return 2;
			
		}
		
		// when goes back the values must be less
		if(reverse) return (rotationCounts + deltaD < ang ) ? 1 : 0;
		
		// if the state is end to stops
		// or the rotation count completes the task
		return (rotationCounts + deltaD > ang ) ? 1 : 0;
	}
	
	public int getSensorUs() {
		return this.interpretador.SensorUS(US_SENSOR);
	}
}
