
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import gnu.io.*;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class SerialTest implements SerialPortEventListener {
	
	private Robot robot;
	static short width;
	static short height;
	
	SerialPort serialPort;
	/** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyACM1", // Raspberry Pi
			"/dev/ttyACM2", // Raspberry Pi
			"/dev/ttyACM3", // Raspberry Pi
			"/dev/ttyACM4", // Raspberry Pi
			"/dev/ttyACM5", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM5", // Windows
	};
	/**
	 * A BufferedReader which will be fed by a InputStreamReader 
	 * converting the bytes into characters 
	 * making the displayed results codepage independent
	 */
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		width=(short)gd.getDisplayMode().getWidth();
		height=(short)gd.getDisplayMode().getHeight();
		
		try {
			robot=new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// the next line is for Raspberry Pi and 
		// gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
		System.setProperty("rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
			
			// Sending screen size to the Arduino
			byte upper = (byte) (width >> 8); //Get the upper 8 bits
			byte lower = (byte) (width & 0xFF); //Get the lower 8bits
			output.write(upper);
			output.write(lower);
			
			upper = (byte) (height >> 8); //Get the upper 8 bits
			lower = (byte) (height & 0xFF); //Get the lower 8bits
			output.write(upper);
			output.write(lower);
			
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				//Recuperation de la ligne du serial
				String inputLine=input.readLine();
				System.out.println(inputLine);
				//Recuperation du dernier caractere
				char ind=inputLine.charAt(inputLine.length()-1);
				//Si c'est 2, on recalibre, signal envoyé par l'Arduino lorsque l'axe Z a une forte acceleration
				if(ind=='2'){
					//Deplacement de la souris au centre
					robot.mouseMove(width/2, height/2);
				}
				//Si c'est 3, on genere la touche echap pour perdre le focus, + recalibration
				else if(ind=='3'){
					//Deplacement de la souris au centre
					robot.mouseMove(width/2, height/2);
					robot.keyPress(27);
					robot.keyRelease(27);
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
			}
		};
		t.start();
		System.out.println("Started");
	}
}
