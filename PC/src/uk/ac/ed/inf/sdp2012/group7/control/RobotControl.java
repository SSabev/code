package uk.ac.ed.inf.sdp2012.group7.control;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.MainRunner;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * This class holds the geometric location of our robot but is also responsible
 * for communicating with it.
 */
public class RobotControl implements ConstantsReuse {

	private CommunicationInterface comms;
	private NXTComm nxtComm;
	private NXTInfo info = new NXTInfo(NXTCommFactory.BLUETOOTH, ROBOT_NAME,ROBOT_MAC);
	public static final Logger logger = Logger.getLogger(RobotControl.class);
	
	private BlockingQueue<byte[]> commandList = new LinkedBlockingQueue<byte[]>();
	private byte[] previousCommand = new byte[4];
	
	private boolean isConnected = false;
	private boolean keepConnected = true;

	private boolean simulator = false;
	private boolean bumped = false;
	
	private final byte[] nothing;
	
	public RobotControl() {
		nothing = new byte[4];
		nothing[0] = 0;
		nothing[1] = (byte) OpCodes.DO_NOTHING.ordinal();
		nothing[2] = 0;
		nothing[3] = 0;
	}

	/**
	 * This method initialises the connection and starts the thread which sends
	 * data to the robot
	 * 
	 * @return True if the connection has been established, false otherwise
	 */
	public boolean startCommunications() {

		// start up the connection
		try {
			connectToRobot();
		} catch (IOException ex) {
			System.err.println("Robot Connection Failed: ");
			System.err.println(ex.toString());
			return false;
		}

		// start up the thread which sends commands
		new Thread(new Runnable() {
			public void run() {

				// send data when necessary
				while (keepConnected) {
					if (!commandList.isEmpty()) {
						sendToRobot(commandList.remove());
					}
						
				}
				// disconnect when we're done
				disconnectFromRobot();
				
			}
		}).start();

		return true;

	}

	/**
	 * Stops the connection with the Robot
	 */
	public void stopCommunications() {
		keepConnected = false;
	}
	
	/**
	 * Connects to the NXT. The Robot beeps when the connection has been established.
	 */
	private void connectToRobot() throws IOException {
		simulator = MainRunner.simulator;
		if (simulator)
			comms = new SimulatorCommunication();
		else 
			comms = new BluetoothCommunication(nxtComm, info);

		comms.openConnection();
		commandList.add(nothing);
		setConnected(true);
		beep();
	}

	/**
	 * Disconnect from the NXT
	 */
	private void disconnectFromRobot() {
		try {
			comms.closeConnection();
			if (!simulator)			
				nxtComm.close();
			setConnected(false);
		} catch (IOException ex) {
			System.err.println("Error Disconnecting from NXT");
			System.err.println(ex.toString());
		}
	}

	/**
	 * Add a command to be sent to the robot
	 */
	private void addCommand(byte kick,byte code,int parameter) {
		byte[] command = new byte[4];
		command[0] = kick;
		command[1] = code;
		command[2] = (byte) ((parameter >> 8) & 0xFF);
		command[3] = (byte) (parameter & 0xFF);
		if(!compare(command,previousCommand)){
			while (commandList.size() > 2) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					logger.trace(e);
				}
			}
			System.arraycopy(command, 0, previousCommand, 0, 4);
			commandList.add(command);
		}
	}

	private boolean compare(byte[] command, byte[] previous){
		if(command.length != previous.length){
			return false;
		} else {
			for(int i = 0; i < command.length; i++){
				if(command[i] != previous[i]){
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Sends a command to the robot
	 */
	private void sendToRobot(byte[] command) {
		if((WorldState.getInstance().canMove) || (OpCodes.values()[command[1]] == OpCodes.CHANGE_SPEED) || (OpCodes.values()[command[1]] == OpCodes.STOP)){
			if(!bumped){
	
				logger.info("Send "+OpCodes.values()[command[1]]);
				OpCodes response = comms.sendToRobot(command);
				logger.info("Sent "+OpCodes.values()[command[1]]);
				logResponse(response);
				if(response == OpCodes.BUMP_ON) bumped = true;
	
			} else {
				stop();
				while(getResponse() != OpCodes.BUMP_OFF.ordinal()){}
				bumped = false;
				logger.debug("Completed bump procedure");
				
				//We don't need anything in the loop as getResponse is blocking anyway
			}
		} else {
			stop();
		}
	}


	/**
	 * Receive an integer from the robot
	 */
	private int getResponse() {
		//the below method is blocking
		return comms.recieveFromRobot();
	}
	
	/**
	 * Changes the speed the robot is travelling at
	 * @param speed
	 */
	public void changeSpeed(int speed) {
		addCommand((byte) 0,(byte) OpCodes.CHANGE_SPEED.ordinal(),speed);
	}
	
	public void changeRotateSpeed(int speed) {
		addCommand((byte) 0,(byte) OpCodes.CHANGE_ROTATE_SPEED.ordinal(),speed);
	}
	

	/**
	 * Commands the robot to move forward
	 */
	public void moveForward() {
		addCommand((byte) 0,(byte) OpCodes.FORWARDS.ordinal(),0);
	}
	
	
	public void moveForward(int speed) {
		addCommand((byte) 0,(byte) OpCodes.FORWARDS.ordinal(),speed);
	}
	
	/**
	 * Commands the robot to move forwards a certain distance
	 * @param distance Measured in cm
	 */
	public void moveForwardDistance(int distance) {
		addCommand((byte) 0,(byte) OpCodes.FORWARDS_WITH_DISTANCE.ordinal(),distance);
	}
	

	/**
	 * Commands the robot to move backward
	 */
	public void moveBackward() {
		addCommand((byte) 0,(byte) OpCodes.BACKWARDS.ordinal(),0);
	}
	
	/**
	 * Commands the robot to move backward at a speed
	 */
	public void moveBackward(int speed) {
		addCommand((byte) 0,(byte) OpCodes.BACKWARDS.ordinal(),speed);
	}

	/**
	 * Commands the robot to move backward
	 */
	public void moveBackwardDistance(int distance) {
		addCommand((byte) 0,(byte) OpCodes.BACKWARDS_WITH_DISTANCE.ordinal(),distance);
	}

	/**
	 * Commands the robot to stop where it is
	 */
	public void stop() {
		commandList.clear();
		addCommand((byte) 0,(byte) OpCodes.STOP.ordinal(),0);
	}
	
	/**
	 * Commands the robot to kick
	 */
	public void kick() {
		addCommand((byte) 1,(byte) OpCodes.CONTINUE.ordinal(),0);
	}

	
	/**
	 * Rotates the robot by the given number of radians
	 * @param radians 
	 */
	public void rotateBy(double radians, boolean block, boolean left) {
		int degrees = (int)Math.toDegrees(radians);
		if (block) {
			if (left) {
				addCommand((byte) 0, (byte) OpCodes.ROTATE_BLOCK_LEFT.ordinal(), degrees);
			} else {
				addCommand((byte) 0, (byte) OpCodes.ROTATE_BLOCK_RIGHT.ordinal(), degrees);
			}
						
		} else {
			if (left) {
				addCommand((byte) 0, (byte) OpCodes.ROTATE_LEFT.ordinal(), degrees);
			} else {
				addCommand((byte) 0, (byte) OpCodes.ROTATE_RIGHT.ordinal(), degrees);
			}
		}
	}
	
	public void rotateBy(double radians, boolean block){
		if(radians < 0){
			rotateBy(-radians,block,false);
		} else {
			rotateBy(radians,block,true);
		}
	}
	
	public void rotateBy(double radians){
		if(radians < 0){
			rotateBy(-radians,false,false);
		} else {
			rotateBy(radians,false,true);
		}
	}

	/**
	 * Commands the robot to move forwards on an arc with a certain radius from the centre of the robot
	 * @param radius 
	 * @param arcLeft If true the robot will go left, otherwise will go right
	 */
	public void circleWithRadius(int radius, boolean arcLeft) {

		if (radius < 200) {
			if (arcLeft) {
				addCommand((byte) 0,(byte) OpCodes.ARC_LEFT.ordinal(),radius);
				
			} else {
				addCommand((byte) 0,(byte) OpCodes.ARC_RIGHT.ordinal(),radius);
				
			}
		} else {
			addCommand((byte) 0,(byte) OpCodes.FORWARDS.ordinal(),0);
		}

	}
	
	
	public void arcWithDistance(int distance, int radius, boolean arcLeft){
		int c = ((radius & 0xFF) << 8) | (distance & 0xFF);
		if (radius < 127) {
			if (arcLeft) {
				addCommand((byte) 0,(byte) OpCodes.ARC_LEFT_DISTANCE.ordinal(),c);
				
			} else {
				addCommand((byte) 0,(byte) OpCodes.ARC_RIGHT_DISTANCE.ordinal(),c);
				
			}
		} else {
			addCommand((byte) 0,(byte) OpCodes.FORWARDS.ordinal(),0);
		}
	}

	/**
	 * Commands the robot to make a noise
	 */
	public void beep() {
		addCommand((byte) 0,(byte) OpCodes.BEEP.ordinal(),0);
	}
	
	/**
	 * Commands the robot to move forward indefinitely and start odometry from its initial position
	 * (if the bluetooth connection is lost, it'll try to move back to the initial position)
	 */
	public void startMatch() {
		addCommand((byte) 0,(byte) OpCodes.START_MATCH.ordinal(),0);
	}
	
	/**
	 * Commands the robot to stop movement and recording odometry
	 */
	public void stopMatch() {
		addCommand((byte) 0,(byte) OpCodes.STOP_MATCH.ordinal(),0);
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isConnected() {
		return isConnected;
	}
	
	public void logResponse(OpCodes response){
		logger.info("Robot Response: " + response);	}



}
