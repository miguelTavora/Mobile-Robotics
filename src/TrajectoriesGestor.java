
public class TrajectoriesGestor {
	
	private final int NO_TRAJECTORY = 0;
	private final int TRAJECTORY_1 = 1;
	private final int TRAJECTORY_2 = 2;
	private final int TRAJECTORY_3 = 3;
	
	private Trajectories trajectories;
	private MyRobotLego myRobot;
	
	// variables used to execute the trajectory
	private int[] commandValues;
	private boolean leftSide;
	private int trajectory;
	
	
	public TrajectoriesGestor(MyRobotLego myRobot) {
		this.myRobot = myRobot;
		trajectories = new Trajectories();
	}
	
	public void executeTrajectory(boolean waitCoomand) {
		switch(this.trajectory) {
			case TRAJECTORY_1:
				executeCommands1(commandValues, leftSide, waitCoomand);
				break;
			case TRAJECTORY_2:
				executeCommands2(commandValues, leftSide, waitCoomand);
				break;
			case TRAJECTORY_3:
				executeCommands3(commandValues, leftSide, waitCoomand);
				break;
		}

		// this way when collide don't do a previous calculated trajectory
		this.trajectory = NO_TRAJECTORY;
	}
	
	// returns as string the trajectory chosen
	public String getTrajectory(int Xf, int Yf, int Of) {
		
		String result = "";
		double dxif = calculateDxif(Xf, Of);
		
		// variable to mirror the trajectory
		boolean leftSide = (Yf > 0) ? true : false;
		Yf = Math.abs(Yf);
		this.leftSide = leftSide;
		
		if(dxif <= Xf) {
			int[] results = trajectories.trajectory1(Xf, Yf, Of);
			
			result = "Trajetória 1 -> Xf: "+Xf+", Yf: "+Yf+", Of: "+Of+"\n";
			String curveType = (leftSide) ? "curvarEsquerda(" : "curvarDireita(";
			
			result += curveType+results[0]+", "+results[1]+"); " +
						"reta("+results[2]+"); "+curveType+results[3]+", "+results[4]+")";
			
			this.commandValues = results;
			this.trajectory = TRAJECTORY_1;
		}
		
		else {
			
			int[] resultsTj2 = trajectories.trajectory2(Xf, Yf, Of);
			int[] resultsTj3 = trajectories.trajectory3(Xf, Yf, Of);
			
			double yc1Tj2 = trajectories.getYc1Tj2();
			double yc2Tj2 = trajectories.getYc2Tj2();

			double yc1Tj3 = trajectories.getYc1Tj3();
			double yc2Tj3 = trajectories.getYc2Tj3();
			
			
			
			if(yc2Tj2 < yc1Tj2) {
				
				/*System.out.println("ResultsTj2: " + Arrays.toString(resultsTj2));
				System.out.println("yc1Tj2: "+yc1Tj2);
				System.out.println("yc2Tj2: "+yc2Tj2);*/
				
				String curveType1 = (leftSide) ? "curvarEsquerda(" : "curvarDireita(";
				String curveType2 = (leftSide) ?  "curvarDireita(" : "curvarEsquerda(";
				
				result = "Trajetória 2 -> Xf: "+Xf+", Yf: "+Yf+", Of: "+Of+"\n";
				result += curveType1+resultsTj2[0]+", "+resultsTj2[1]+"); " +
						"reta("+resultsTj2[2]+"); "+curveType2+resultsTj2[3]+", "+resultsTj2[4]+")";
				
				this.commandValues = resultsTj2;
				this.trajectory = TRAJECTORY_2;
			}
			
			else if(yc2Tj3 >= yc1Tj3) {
				
				/*System.out.println("resultsTj3: " + Arrays.toString(resultsTj3));
				System.out.println("yc1Tj3: "+yc1Tj3);
				System.out.println("yc2Tj3: "+yc2Tj3);*/
				
				String curveType1 = (leftSide) ? "curvarEsquerda(" : "curvarDireita(";
				String curveType2 = (leftSide) ?  "curvarDireita(" : "curvarEsquerda(";
				
				result = "Trajetória 3 -> Xf: "+Xf+", Yf: "+Yf+", Of: "+Of+"\n";
				result += curveType1+resultsTj3[0]+", "+resultsTj3[1]+"); " +
						"reta("+resultsTj3[2]+"); "+curveType2+resultsTj3[3]+", "+resultsTj3[4]+")";
				
				this.commandValues = resultsTj3;
				this.trajectory = TRAJECTORY_3;
			}
			
		}
		System.out.println(result);
		return result;
		
	}
	
	private double calculateDxif(int Xf, int Of) {
		double radOf = Math.toRadians(Math.abs(Of));
		
		// sen x = C opposite / hipotenuse
		double hipotenuse = Xf / Math.sin(radOf);
		
		// cos x = C addjacent / hipotenuse
		double dxif = Math.cos(radOf) * hipotenuse;

		return dxif;
	}
	
	private void executeCommands1(int[] results, boolean leftSide, boolean waitCommand) {
		boolean collided = false;
		
		if(leftSide) 
			collided = curveLeftCollision(results[0], results[1], waitCommand);
		
		// we add 10 to the angle to adjust because is does less than the wanted angle
		else 
			collided = curveRightCollision(results[0], results[1], waitCommand);
		
		
		if(collided) return;
		
		// goes with straight line, if collided must end
		collided = straightLineCollision(results[2], waitCommand);
		if(collided) return;
		
		
		if(leftSide) 
			curveLeftCollision(results[3], results[4], waitCommand);
		
		else
			curveRightCollision(results[3], results[4], waitCommand);

	}
	
	
	private void executeCommands2(int[] results, boolean leftSide, boolean waitCommand) {
		boolean collided = false;
		
		// when is not mirrored trajectory
		if (leftSide) 
			collided = curveLeftCollision(results[0], results[1], waitCommand);
		
		// when is mirrored trajectory
		else
			collided = curveRightCollision(results[0], results[1], waitCommand);
		
		
		if(collided) return;
		
		// goes with straight line, if collided must end
		collided = straightLineCollision(results[2], waitCommand);
		if(collided) return;
		
		
		if (leftSide) 
			curveRightCollision(results[3], results[4], waitCommand);
		
		else 
			curveLeftCollision(results[3], results[4], waitCommand);
		
	}
	
	private void executeCommands3(int[] results, boolean leftSide, boolean waitCommand) {
		boolean collided = false;
		
		// when is not mirrored trajectory
		if (leftSide) 
			collided = curveLeftCollision(results[0], results[1], waitCommand); 
		
		// when is mirrored trajectory
		else 
			collided = curveRightCollision(results[0], results[1], waitCommand);
		
		
		if(collided) return;
		
		// goes with straight line, if collided must end
		collided = straightLineCollision(results[2], waitCommand);
		if(collided) return;
		
		// when is not mirrored trajectory
		if(leftSide) 
			curveRightCollision(results[3], results[4], waitCommand);
		
		// when is mirrored trajectory
		else 
			curveLeftCollision(results[3], results[4], waitCommand);

	}
	
	public void collidedGoBack() {
		// when collides goes back 70cm and curves 90º
		this.myRobot.line(-70, false);
		this.myRobot.stop();
		
		this.myRobot.curveLeft(90, 20, false);
		this.myRobot.stop();
	}
	
	private void waitForCommand(boolean waitCommand) {
		if (waitCommand) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// method to detect collision on forward and go back 70 cms and curve 90º
	private boolean straightLineCollision(int distance, boolean waitCommand) {
		boolean collided = this.myRobot.line(distance, true);
		if (collided) {
			collidedGoBack();
			return true;
		}

		if (waitCommand) {
			this.myRobot.stop();
			waitForCommand(waitCommand);
		}
		return false;
	}
	
	private boolean curveLeftCollision(int angle, int radius, boolean waitCommand) {
		boolean collided = this.myRobot.curveLeft(angle, radius, true);
		if (collided) {
			collidedGoBack();
			return true;
		}
		if (waitCommand) {
			this.myRobot.stop();
			waitForCommand(waitCommand);
		}
		return false;
	}
	
	private boolean curveRightCollision(int angle, int radius, boolean waitCommand) {
		boolean collided = this.myRobot.curveRight(angle, radius, true);
		if (collided) {
			collidedGoBack();
			return true;
		}
		if (waitCommand) {
			this.myRobot.stop();
			waitForCommand(waitCommand);
		}
		return false;
	}

}
