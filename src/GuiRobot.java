
import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

public class GuiRobot implements Runnable{
	
	private JFrame frame;
	
	private boolean connected = false;
	private boolean followWall = false;
	
	// text fiels to write distances
	private JTextField fieldRobotName;
	private JTextField fieldXf;
	private JTextField fieldYf;
	private JTextField fieldOf;
	private JTextField fieldDistance;
	private JTextField fieldRay;
	private JTextField fieldAngle;
	
	// text area to show the commands
	private JTextArea areaConsole;
	
	
	// buttons to execute commands
	private JButton btnFront;
	private JButton btnRight;
	private JButton btnLeft;
	private JButton btnBack;
	private JButton btnStop;
	private JButton btnTrajectory;
	private JButton btnFollowWall;
	
	private JRadioButton onOff;
	private JCheckBox checkBoxDebug;
	
	// variables used
	private Variables variables;

	
	public static void main(String[] args) {
		// runs the GUI as thread to not block the commands
		GuiRobot gr = new GuiRobot();
		Thread t = new Thread(gr);
		t.start();
	}
	
	public GuiRobot() {
		variables = new Variables();
	}

	public void run() {
		initializeGui();
		
	}
	
	private void initializeGui() {
		frame = new JFrame();
		frame.setBounds(100, 100, 580, 585);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// create the elements of the interface
		createButtons();
		createRobotName();
		createDistance();
		createOnOffBtn();
		createRay();
		createAngle();
		createConsole();
		createDebug();
		
		
		createXf();
		createYf();
		createOf();

		frame.setVisible(true);
	}
	
	
	// create all the buttons
	private void createButtons() {
		// left button
		btnLeft = new JButton("Esquerda");
		btnLeft.setBounds(79, 277, 140, 68);
		btnLeft.setBackground(new Color(19, 56, 190));
		btnLeft.setEnabled(false);
		frame.getContentPane().add(btnLeft);

		// right button
		btnRight = new JButton("Direita");
		btnRight.setBounds(361, 277, 140, 68);
		btnRight.setBackground(new Color(230, 219, 172));
		btnRight.setEnabled(false);
		frame.getContentPane().add(btnRight);

		// stop button
		btnStop = new JButton("Parar");
		btnStop.setBounds(220, 277, 140, 68);
		btnStop.setBackground(new Color(227, 36, 43));
		btnStop.setEnabled(false);
		frame.getContentPane().add(btnStop);

		// front button
		btnFront = new JButton("Frente");
		btnFront.setBounds(220, 208, 140, 68);
		btnFront.setBackground(new Color(61, 237, 151));
		btnFront.setEnabled(false);
		frame.getContentPane().add(btnFront);

		// back button
		btnBack = new JButton("Retaguarda");
		btnBack.setBounds(220, 346, 140, 68);
		btnBack.setBackground(new Color(182, 95, 207));
		btnBack.setEnabled(false);
		frame.getContentPane().add(btnBack);
		
		
		btnTrajectory = new JButton("Trajetória");
		btnTrajectory.setBounds(381, 366, 140, 68);
		btnTrajectory.setBackground(new Color(177, 177, 177));
		btnTrajectory.setEnabled(false);
		frame.getContentPane().add(btnTrajectory);
		
		btnFollowWall = new JButton("Seguir parede");
		btnFollowWall.setBounds(381, 198, 140, 68);
		btnFollowWall.setBackground(new Color(255,140,0));
		btnFollowWall.setEnabled(false);
		frame.getContentPane().add(btnFollowWall);
		
		// listeners for the buttons
		listenerBtnLeft();
		listenerBtnRight();
		listenerBtnFront();
		listenerBtnBack();
		listenerBtnStop();
		listenerBtnTrajectories();
		listenerBtnFollowWall();

		// listener when close the connection with robot
		listenerOnClose();
	}
	
	private void createOnOffBtn() {
		// Radio button to turn robot on or off
		onOff = new JRadioButton("On/Off");
		onOff.setFont(new Font("Tahoma", Font.PLAIN, 15));
		onOff.setBounds(410, 25, 109, 23);
		onOff.setSelected(variables.isOnOff());
		frame.getContentPane().add(onOff);
		
		listenerConnectRobot();
	}

	// create the text and field to write the name
	private void createRobotName() {
		// label to show text of the robot name
		JLabel labelRobot = new JLabel("Robot:");
		labelRobot.setFont(new Font("Tahoma", Font.PLAIN, 19));
		labelRobot.setBounds(183, 13, 67, 37);
		frame.getContentPane().add(labelRobot);

		// field to set the robot name
		fieldRobotName = new JTextField("" + variables.getNomeRobot());
		fieldRobotName.setBounds(245, 21, 90, 25);
		frame.getContentPane().add(fieldRobotName);
		
		listenerFieldRobotName();
	}
	
	private void createXf() {
		JLabel label = new JLabel("Xf:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 16));
		label.setBounds(60, 92, 80, 23);
		frame.getContentPane().add(label);
		
		fieldXf = new JTextField(""+variables.getXf());
		fieldXf.setBounds(85, 92, 86, 25);
		frame.getContentPane().add(fieldXf);
		
		listenerXf();
	}
	
	private void createYf() {
		JLabel label = new JLabel("Yf:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 16));
		label.setBounds(221, 95, 67, 17);
		frame.getContentPane().add(label);

		fieldYf = new JTextField(""+ variables.getYf());
		fieldYf.setBounds(250, 92, 86, 25);
		frame.getContentPane().add(fieldYf);
		
		listenerYf();
	}
	
	private void createOf() {
		JLabel labelAngle = new JLabel("Of:");
		labelAngle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelAngle.setBounds(438, 92, 58, 20);
		frame.getContentPane().add(labelAngle);

		fieldOf = new JTextField(""+variables.getOf());
		fieldOf.setBounds(465, 92, 86, 25);
		frame.getContentPane().add(fieldOf);
		
		listenerOf();
	}

	// text and field of distance
	private void createDistance() {
		// label to show text of distância
		JLabel labelDistance = new JLabel("Dist\u00E2ncia:");
		labelDistance.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelDistance.setBounds(10, 162, 80, 23);
		frame.getContentPane().add(labelDistance);

		// field to write the distance
		fieldDistance = new JTextField("" + variables.getDistance());
		fieldDistance.setBounds(85, 162, 86, 25);
		frame.getContentPane().add(fieldDistance);
		
		listenerDistance();
	}
	
	private void createRay() {
		// text to show raio
		JLabel labelRay = new JLabel("Raio:");
		labelRay.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelRay.setBounds(211, 165, 67, 17);
		frame.getContentPane().add(labelRay);

		// field to write the ray
		fieldRay = new JTextField("" + variables.getRadius());
		fieldRay.setBounds(250, 162, 86, 25);
		frame.getContentPane().add(fieldRay);
		
		listenerRay();
	}
	
	private void createAngle() {
		// text to write ângulo
		JLabel labelAngle = new JLabel("\u00C2ngulo:");
		labelAngle.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelAngle.setBounds(403, 162, 58, 20);
		frame.getContentPane().add(labelAngle);

		// field to write the angle
		fieldAngle = new JTextField("" + variables.getAngle());
		fieldAngle.setBounds(465, 162, 86, 25);
		frame.getContentPane().add(fieldAngle);
		
		listenerAngle();
	}
	
	private void createConsole() {
		JLabel labelConsole = new JLabel("Consola");
		labelConsole.setFont(new Font("Tahoma", Font.PLAIN, 15));
		labelConsole.setBounds(72, 440, 58, 20);
		frame.getContentPane().add(labelConsole);

		areaConsole = new JTextArea();
		areaConsole.setBounds(72, 460, 458, 65);
		areaConsole.setEditable(false);
		areaConsole.setFont(areaConsole.getFont().deriveFont(14f));
		frame.getContentPane().add(areaConsole);
	}
	
	private void createDebug() {
		checkBoxDebug = new JCheckBox("Debug");
		checkBoxDebug.setBounds(72, 404, 97, 23);
		checkBoxDebug.setSelected(variables.isDebug());
		frame.getContentPane().add(checkBoxDebug);
		
		listenerToDebug();
	}

	private void listenerFieldRobotName() {
		fieldRobotName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				variables.setNomeRobot(fieldRobotName.getText());
				myPrint("Nome:"+ variables.getNomeRobot());
			}
		});
	}
	
	private void listenerBtnLeft() {
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						myPrint("Esquerda: " + String.valueOf(variables.getAngle()) + " "+ String.valueOf(variables.getRadius()));
						// TODO
						if (variables.getMyImplentation()) {
							variables.getMyRobot().curveLeft(variables.getAngle(), variables.getRadius(), true);
							variables.getMyRobot().stop();

						} else {
							variables.getRobot().CurvarEsquerda(variables.getRadius(), variables.getAngle());
							variables.getRobot().Parar(false);
						}
					}
				}.start();
			}
		});
	}
	
	private void listenerBtnRight() {
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						myPrint("Direita: " + String.valueOf(variables.getAngle()) + " "+ String.valueOf(variables.getRadius()));
						// TODO
						if (variables.getMyImplentation()) {
							variables.getMyRobot().curveRight(variables.getAngle(), variables.getRadius(), true);
							variables.getMyRobot().stop();
						} else {
							variables.getRobot().CurvarDireita(variables.getRadius(), variables.getAngle());
							variables.getRobot().Parar(false);
						}
					}
				}.start();
			}
		});
	}
	
	private void listenerBtnFront() {
		btnFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// creates a thread that makes the robot goes forward
				new Thread() {
				    public void run() {
				    	myPrint("Frente: " + variables.getDistance());
						// TODO
						if (variables.getMyImplentation()) {
							variables.getMyRobot().line(variables.getDistance(), false);
							variables.getMyRobot().stop();

						} else {
							variables.getRobot().Reta(variables.getDistance());
							variables.getRobot().Parar(false);
						}
				    }
				}.start();
			}
		});
	}
	
	private void listenerBtnStop() {
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						myPrint("Parar");
						// TODO
						if (variables.getMyImplentation()) {
							if(!followWall)
								variables.getMyRobot().stop();
							else 
								btnStop.setEnabled(false);
						}

						else
							variables.getRobot().Parar(true);
						
						followWall = false;
					}
				}.start();
			}
		});
	}
	
	private void listenerBtnBack() {
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						myPrint("Retaguarda: " + String.valueOf(variables.getDistance()));
						// TODO
						if (variables.getMyImplentation()) {
							variables.getMyRobot().line(variables.getDistance() * (-1), false);
							variables.getMyRobot().stop();
						} else {
							variables.getRobot().Reta(variables.getDistance() * (-1));
							variables.getRobot().Parar(false);
						}
					}
				}.start();
			}

		});
	}
	
	private void listenerConnectRobot() {
		onOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (onOff.isSelected()) {
					myPrint("Conexão com robô");
					//TODO
					if(variables.getMyImplentation()) connected = variables.getMyRobot().openEV3(variables.getNomeRobot());
						
					else connected = variables.getRobot().OpenEV3(variables.getNomeRobot());
					
					// set the interface state
					setButtinsState(connected);
				}
				else {
					myPrint("Disconexão com robô");
					// TODO
					if(variables.getMyImplentation()) variables.getMyRobot().closeEV3();
					
					else variables.getRobot().CloseEV3();
						
					onOff.setSelected(false);
					variables.setOnOff(false);
					connected = false;
					// set the interface state
					setButtinsState(false);
					followWall = false;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}
		});
	}
	
	public void listenerOnClose() {
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	if(connected) {
		    		// disconnect the robot
		    		if(variables.getMyImplentation()) variables.getMyRobot().closeEV3();
		    		
		    		else variables.getRobot().CloseEV3();
		    		followWall = false;
		    	}
		    }
		});
	}
	
	private void listenerBtnTrajectories() {
		btnTrajectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						setButtinsState(false);
						// gets the string with the values of the commands that will be done
						String trajectories = variables.getTrajectoriesGestor()
								.getTrajectory(variables.getXf(), variables.getYf(), variables.getOf());
						
						
						myPrint(trajectories);
						
						// executes the commands
						variables.getTrajectoriesGestor().executeTrajectory(checkBoxDebug.isSelected());
						variables.getMyRobot().stop();
						setButtinsState(true);
					}
				}.start();
			}
		});
	}
	
	private void listenerBtnFollowWall() {
		btnFollowWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						// disable buttons to prevent errors
						btnFollowWall.setEnabled(false);
						btnTrajectory.setEnabled(false);
						followWall = true;
						while(followWall) {
							String result = variables.getFollowWall().calcutate();
							myPrint("Follow Wall: "+result);
							System.out.println("result: "+result);
							// executes the commands
							variables.getTrajectoriesGestor().executeTrajectory(checkBoxDebug.isSelected());
						}
						System.out.println("Terminar");
						variables.getMyRobot().stop();
						
						// enable after the execution
						btnFollowWall.setEnabled(true);
						btnTrajectory.setEnabled(true);
						btnStop.setEnabled(true);
					}
				}.start();
			}
		});
	}
	
	private void listenerXf() {
		fieldXf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// verifies if the sting only contains numbers, otherwise shows a message
				if(fieldXf.getText().matches("[0-9\\-]*")) {
					int xf = Integer.parseInt(fieldXf.getText());
					if(xf <= 400) {
						variables.setXf(xf);
						myPrint("Xf:"+ variables.getXf());
					}
					else JOptionPane.showMessageDialog(frame, "A distância só pode ser no máximo 400");//
				}
				
				else JOptionPane.showMessageDialog(frame, "A distância só pode conter números");//
				
			}
		});
	}
	
	private void listenerYf() {
		fieldYf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// verifies if the sting only contains numbers, otherwise shows a message
				if(fieldYf.getText().matches("[0-9\\-]*")) {
					int yf = Integer.parseInt(fieldYf.getText());
					if(yf <= 400) {
						variables.setYf(yf);
						myPrint("Yf:"+ variables.getYf());
					}
					else JOptionPane.showMessageDialog(frame, "A distância só pode ser no máximo 400");//
				}
				
				else JOptionPane.showMessageDialog(frame, "A distância só pode conter números");//
				
			}
		});
	}
	
	private void listenerOf() {
		fieldOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// verifies if the sting only contains numbers, otherwise shows a message
				if(fieldOf.getText().matches("[0-9\\-]*")) {
					int of = Integer.parseInt(fieldOf.getText());
					if(of <= 400) {
						variables.setOf(of);
						myPrint("Of:"+ variables.getOf());
					}
					else JOptionPane.showMessageDialog(frame, "A distância só pode ser no máximo 400");//
				}
				
				else JOptionPane.showMessageDialog(frame, "A distância só pode conter números");//
				
			}
		});
	}
	
	private void listenerDistance() {
		fieldDistance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// verifies if the sting only contains numbers, otherwise shows a message
				if(fieldDistance.getText().matches("[0-9]*")) {
					int dist = Integer.parseInt(fieldDistance.getText());
					if(dist <= 400) {
						variables.setDistance(dist);
						myPrint("Distancia:"+ variables.getDistance());
					}
					else JOptionPane.showMessageDialog(frame, "A distância só pode ser no máximo 400");//
				}
				
				else JOptionPane.showMessageDialog(frame, "A distância só pode conter números");//
				
			}
		});
	}
	
	private void listenerRay() {
		fieldRay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fieldRay.getText().matches("[0-9]*")) {
					int radius = Integer.parseInt(fieldRay.getText());
					if(radius <= 50) {
						variables.setRadius(radius);
						myPrint("Raio:"+ variables.getRadius());
					}
					else JOptionPane.showMessageDialog(frame, "O raio só pode ser no máximo 50");//
				}
				
				else JOptionPane.showMessageDialog(frame, "O raio só pode conter números");//
			}
		});
	}
	
	private void listenerAngle() {
		fieldAngle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fieldAngle.getText().matches("[0-9]*")) {
					int angle = Integer.parseInt(fieldAngle.getText());
					if(angle <= 720) {
						variables.setAngulo(angle);
						myPrint("Angulo:"+ variables.getAngle());
					}
					else JOptionPane.showMessageDialog(frame, "O angulo só pode ser no máximo 720");//
				}
				else JOptionPane.showMessageDialog(frame, "O angulo só pode conter números");//
			}
		});
	}
	
	private void listenerToDebug() {
		checkBoxDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				variables.setDebug(checkBoxDebug.isSelected());
			}
		});
	}
	
	
	public void myPrint(String str) {
		areaConsole.setText(str);
	}
	
	public void setButtinsState(boolean result) {
		onOff.setSelected(result);
		variables.setOnOff(result);
		btnLeft.setEnabled(result);
		btnRight.setEnabled(result);
		btnFront.setEnabled(result);
		btnStop.setEnabled(result);
		btnBack.setEnabled(result);
		btnTrajectory.setEnabled(result);
		btnFollowWall.setEnabled(result);
	}
	
}
