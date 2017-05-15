
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.BorderLayout;
import javax.swing.JToggleButton;
import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Robot;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Click {
	private JFrame frame;  // Fenetre graphique
	private static Robot robot;  // Robot permettant de deplacer la souris
	private long chronoDepartCalibration;  // Chrono depart de la calibration
	private long chronoArriveeCalibration;  // Chrono arrive de la calibration
	private long chronoDepartLeft;  // Chrono depart click gauche
	private long chronoArriveeLeft; // Chrono arrive click gauche
	private long chronoDepartLeftD;  // Chrono depart click double gauche
	private long chronoArriveeLeftD; // Chrono arrive click double gauche
	private long chronoDepartCenter;  // Chrono depart click molette, centre
	private long chronoArriveeCenter; // Chrono arrive click molette, centre
	private long chronoDepartCenterD;  // Chrono depart double click molette, centre
	private long chronoArriveeCenterD; // Chrono arrive double click molette, centre
	private long chronoDepartRight;  // Chrono depart click droit
	private long chronoArriveeRight; // Chrono arrive click droit
	private long chronoDepartDrag;  // Chrono depart drag
	private long chronoArriveeDrag; // Chrono arrive drag
	private static SerialTest main;  // Serial Arduino
	private JPanel doubleC;
	private static Graphics g;
	private JTextField cpt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			//Initialisation du robot
			robot=new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Lancement du serial
					main = new SerialTest();
					main.initialize();
					Thread t=new Thread() {
						public void run() {
							//the following line will keep this app alive for 1000 seconds,
							//waiting for events to occur and responding to them (printing incoming messages to console).
							try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
						}
					};
					// Creation de la fenetre graphique
					Click window = new Click();
					window.frame.setVisible(true);
					// Auto focus constant fenetre de gestion des clicks
					window.frame.setAlwaysOnTop(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Click() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(238, 238, 238));
		frame.setBounds(100, 100, 300, 125);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel simpleC = new JPanel();
		frame.getContentPane().add(simpleC, BorderLayout.NORTH);
		simpleC.setLayout(new BoxLayout(simpleC, BoxLayout.X_AXIS));

		/* Bouton Click Gauche */
		JToggleButton left = new JToggleButton("  LEFT  ");
		g = left.getGraphics();
		left.setBackground(UIManager.getColor("Button.background"));
		left.paint(g);
		simpleC.add(left);
		left.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// Si la souris est depuis plus de 2 secondes sur le bouton click gauche, on génère le click gauche
				long time = java.lang.System.currentTimeMillis();
				if(time >= chronoArriveeLeft){
					g = left.getGraphics();
					left.setBackground(Color.GREEN);
					left.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On genere le click gauche
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					left.setBackground(UIManager.getColor("Button.background"));
					left.paint(g);
				}
			}
		});
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartLeft=java.lang.System.currentTimeMillis();
				chronoArriveeLeft=chronoDepartLeft+2000;
			}
		});

		/* Bouton Click Molette */
		JToggleButton center = new JToggleButton("CENTER");
		g = center.getGraphics();
		center.setBackground(UIManager.getColor("Button.background"));
		center.paint(g);
		center.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// Si la souris est depuis plus de 2 secondes sur le bouton click centre, on génère le click molette, centre
				if(java.lang.System.currentTimeMillis()>=chronoArriveeCenter){
					g = center.getGraphics();
					center.setBackground(Color.GREEN);
					center.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On genere le click centre, molette
					robot.mousePress(InputEvent.BUTTON2_MASK);
					robot.mouseRelease(InputEvent.BUTTON2_MASK);
					g = center.getGraphics();
					center.setBackground(UIManager.getColor("Button.background"));
					center.paint(g);
				}
			}
		});
		center.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartCenter=java.lang.System.currentTimeMillis();
				chronoArriveeCenter=chronoDepartCenter+2000;
			}
		});
		simpleC.add(center);

		/* Bouton Click Droit */
		JToggleButton right = new JToggleButton("  RIGHT...LEFT");
		g = right.getGraphics();
		right.setBackground(UIManager.getColor("Button.background"));
		right.paint(g);
		simpleC.add(right);
		right.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				//Le click droit étant pour un menu déroulant, on génère d'abord un click droit, puis un clck gauche de selection, a 3 secondes d'intervalle
				if(java.lang.System.currentTimeMillis()>=chronoArriveeRight){
					g = right.getGraphics();
					right.setBackground(Color.GREEN);
					right.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On génère le click droit après trois secondes
					robot.mousePress(InputEvent.BUTTON3_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_MASK);
					//On attend 5 secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("5");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("4");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On génère le click gauche de selection après trois secondes
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					g = right.getGraphics();
					right.setBackground(UIManager.getColor("Button.background"));
					right.paint(g);
				}
			}
		});
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartRight=java.lang.System.currentTimeMillis();
				chronoArriveeRight=chronoDepartRight+2000;
			}
		});

		doubleC = new JPanel();
		frame.getContentPane().add(doubleC, BorderLayout.WEST);
		doubleC.setLayout(new BoxLayout(doubleC, BoxLayout.X_AXIS));

		JToggleButton dLeft = new JToggleButton("LEFT 2");
		g = dLeft.getGraphics();
		dLeft.setBackground(UIManager.getColor("Button.background"));
		dLeft.paint(g);
		dLeft.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// Si la souris est depuis plus de 2 secondes sur le bouton click gauche, on génère le click gauche
				long time = java.lang.System.currentTimeMillis();
				if(time >= chronoArriveeLeftD){
					g = dLeft.getGraphics();
					dLeft.setBackground(Color.GREEN);
					dLeft.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On genere le double click gauche
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					g = dLeft.getGraphics();
					dLeft.setBackground(UIManager.getColor("Button.background"));
					dLeft.paint(g);
				}
			}
		});
		dLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartLeftD=java.lang.System.currentTimeMillis();
				chronoArriveeLeftD=chronoDepartLeftD+2000;
			}
		});		
		doubleC.add(dLeft);

		JToggleButton Left_Left = new JToggleButton("LEFT...LEFT");
		g = Left_Left.getGraphics();
		Left_Left.setBackground(UIManager.getColor("Button.background"));
		Left_Left.paint(g);
		Left_Left.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// Si la souris est depuis plus de 2 secondes sur le bouton click centre, on génère le click molette, centre
				if(java.lang.System.currentTimeMillis()>=chronoArriveeCenterD){					
					g = Left_Left.getGraphics();
					Left_Left.setBackground(Color.GREEN);
					Left_Left.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On genere le click gauche
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On regenere le click gauche
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					g = Left_Left.getGraphics();
					Left_Left.setBackground(UIManager.getColor("Button.background"));
					Left_Left.paint(g);
				}
			}
		});
		Left_Left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartCenterD=java.lang.System.currentTimeMillis();
				chronoArriveeCenterD=chronoDepartCenterD+2000;
			}
		});
		doubleC.add(Left_Left);
		
		JToggleButton tglbtnDrag = new JToggleButton("DRAG");
		tglbtnDrag.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				// Si la souris est depuis plus de 2 secondes sur le bouton click gauche, on génère le click gauche
				long time = java.lang.System.currentTimeMillis();
				if(time >= chronoArriveeDrag){
					g = tglbtnDrag.getGraphics();
					tglbtnDrag.setBackground(Color.GREEN);
					tglbtnDrag.paint(g);
					//On attend trois secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					//On genere le double click gauche
					robot.mousePress(InputEvent.BUTTON1_MASK);
					//On attend 5 secondes
					try {
						g = cpt.getGraphics();
						cpt.setText("5");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("4");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("3");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("2");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("1");
						cpt.paint(g);
						Thread.sleep(1000);
						g = cpt.getGraphics();
						cpt.setText("0");
						cpt.paint(g);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					g = tglbtnDrag.getGraphics();
					tglbtnDrag.setBackground(UIManager.getColor("Button.background"));
					tglbtnDrag.paint(g);
				}
			}
		});
		tglbtnDrag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartDrag=java.lang.System.currentTimeMillis();
				chronoArriveeDrag=chronoDepartDrag+2000;
			}
		});
		doubleC.add(tglbtnDrag);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		cpt = new JTextField();
		panel.add(cpt);
		cpt.setColumns(10);
		JToggleButton calibrate = new JToggleButton("CALIBRATE");
		panel.add(calibrate);
		calibrate.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				//On remet la souris au centre de l'ecran si la souris est sur le bouton calibration depuis plus de deux secondes
				if(java.lang.System.currentTimeMillis()>=chronoArriveeCalibration){
					//On met la souris au centre
					robot.mouseMove(main.width/2, main.height/2);
				}
			}
		});

		calibrate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// On calcule les deux secondes quand la souris arrive dans le bouton
				chronoDepartCalibration=java.lang.System.currentTimeMillis();
				chronoArriveeCalibration=chronoDepartCalibration+2000;
			}
		});
	}
}
