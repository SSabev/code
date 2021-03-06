package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.ArrayList;
import uk.ac.ed.inf.sdp2012.group7.vision.ThresholdsState;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.MovingObject;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * Does fucking everything.  Needs to be broken up
 * 
 * @author s0951580
 *  
 * 
 * 
 */



public class Thresholding {
	
	/**
	 * @author James Hulme
	 * @author Dale Myers
	 * @author Radoslav Gabrovski
	 * 
	 */

	private WorldState worldState = WorldState.getInstance();

	private ArrayList<Integer> yellowRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotX = new ArrayList<Integer>();
	private ArrayList<Integer> yellowRobotY = new ArrayList<Integer>();
	private ArrayList<Integer> blueRobotY = new ArrayList<Integer>();
	private ArrayList<Point> blueGreenPlate = new ArrayList<Point>();
	private ArrayList<Point> yellowGreenPlate = new ArrayList<Point>();


	private ArrayList<Point> newYellowPixels = new ArrayList<Point>();
	private ArrayList<Point> newBluePixels = new ArrayList<Point>();

	private Point[] blueGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
	private Point[] yellowGreenPlate4Points = new Point[]{new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};


	private Color c;
	/*The north, south, east and west immediate pixel's colors of c*/
	private Color cS;
	private Color cE;
	private Color cEN;
	private Color cEE;
	private Color cSS;
	private Color cSW;

	private int GB;// green - blue
	private int RG; // red - green
	//private int RB; // red - blue
	//private int RGthresh;

	private int[][] redBallThresh= new int[2][3];
	private int[][] yellowRobotThresh= new int[2][3];
	private int[][] blueRobotThresh= new int[2][3];
	private int[][] greenPlatesThresh= new int[2][1];


	private Point redCentroidA = new Point();
	private Point redCentroidB = new Point();
	private Point redCentroidC = new Point();
	private Point redCentroidD = new Point();
	private Point redCentroidE = new Point();
	private Point blueCentroidA = new Point();
	private Point blueCentroidB = new Point();
	private Point blueCentroidC = new Point();
	private Point blueCentroidD = new Point();
	private Point blueCentroidE = new Point();
	private Point yellowCentroidA = new Point();
	private Point yellowCentroidB = new Point();
	private Point yellowCentroidC = new Point();
	private Point yellowCentroidD = new Point();
	private Point yellowCentroidE = new Point();

	private int redCountA;
	private int redCountB;
	private int redCountC;
	private int redCountD;
	private int redCountE;
	private int yellowCountA;
	private int yellowCountB;
	private int yellowCountC;
	private int yellowCountD;
	private int yellowCountE;
	private int blueCountA;
	private int blueCountB;
	private int blueCountC;
	private int blueCountD;
	private int blueCountE;
	//    private int robot; // 0 for Yellow, 1 for Blue(our robot)  We will use the world state

	private ThresholdsState ts;
	private Plate plate = new Plate();

	private double randy = 0;

	private int redX = 0;
	private int redY = 0;

	private int yellowX = 0;
	private int yellowY = 0;

	private int blueX = 0;
	private int blueY = 0;

	private int totalYellowX = 0;
	private int totalYellowY = 0;

	private int numYellowCentroids = 0;

	private int totalBlueX = 0;
	private int totalBlueY = 0;

	private int numBlueCentroids = 0;

	private int totalRedX = 0;
	private int totalRedY = 0;

	private int numRedCentroids = 0;



	/**
	 * The constructor for the class
	 * 
	 * @param ts A thresholdsstate object so we can initialise the thresholds
	 */

	public Thresholding(ThresholdsState ts) {  // Sets the constants for thresholding for each pitch 
		redBallThresh[0][0] = 130;
		redBallThresh[0][1] = 90;
		redBallThresh[0][2] = 90;
		redBallThresh[1][0] = 170;
		redBallThresh[1][1] = 170;
		redBallThresh[1][2] = 170;
		yellowRobotThresh[0][0] = 140;
		yellowRobotThresh[0][1] = 140;
		yellowRobotThresh[0][2] = 170;
		yellowRobotThresh[1][0] = 150;
		yellowRobotThresh[1][1] = 190;
		yellowRobotThresh[1][2] = 140;
		blueRobotThresh[0][0] = 120;
		blueRobotThresh[0][1] = 170;
		blueRobotThresh[0][2] = 90;
		blueRobotThresh[1][0] = 160;
		blueRobotThresh[1][1] = 230;
		blueRobotThresh[1][2] = 215;


		greenPlatesThresh[0][0] = 120;
		greenPlatesThresh[1][0] = 205;


		this.ts = ts;

	}
	
	/**
	 * Our do everything method
	 * 
	 * Goes through every pixel, working out which object it should belong to,
	 * calculates positions etc
	 * 
	 * @param img The frame of video
	 * @param left Left boundary of pitch
	 * @param right Right boundary of pitch
	 * @param top Top boundary of pitch
	 * @param bottom Bottom boundary of pitch
	 * @return the frame we just took, probably to display
	 */
	public BufferedImage getThresh(BufferedImage img, int left, int right, int top, int bottom) { // Method to get thresholded image 
		//Vision.logger.debug("Starting thresholding");

		//stops it fucking up the locations before we've given it the thresholds
		if (worldState.isClickingDone()){

			newBluePixels = new ArrayList<Point>();
			newYellowPixels = new ArrayList<Point>();
			ArrayList<Point> bluePixels = new ArrayList<Point>();
			ArrayList<Point> yellowPixels = new ArrayList<Point>();
			/*pitch = worldState.getRoom();
			width = right-left;
			height = top-bottom;*/

			/*
           Initialising to one to stop java dividing by 0 when it shouldn't
			 */
			redCountA = 0;
			redCountB = 0;
			redCountC = 0;
			redCountD = 0;
			redCountE = 0;
			redCentroidA.setLocation(0,0);
			redCentroidB.setLocation(0,0);
			redCentroidC.setLocation(0,0);
			redCentroidD.setLocation(0,0);
			redCentroidE.setLocation(0,0);

			blueCountA = 0;
			blueCountB = 0;
			blueCountC = 0;
			blueCountD = 0;
			blueCountE = 0;
			blueCentroidA.setLocation(0,0);
			blueCentroidB.setLocation(0,0);
			blueCentroidC.setLocation(0,0);
			blueCentroidD.setLocation(0,0);
			blueCentroidE.setLocation(0,0);

			yellowCountA = 0;
			yellowCountB = 0;
			yellowCountC = 0;
			yellowCountD = 0;
			yellowCountE = 0;
			yellowCentroidA.setLocation(0,0);
			yellowCentroidB.setLocation(0,0);
			yellowCentroidC.setLocation(0,0);
			yellowCentroidD.setLocation(0,0);
			yellowCentroidE.setLocation(0,0);

			//Vision.logger.debug("Iterating image");
			for (int i = left; i < right; i++) {
				for (int j = top; j < bottom; j++) {
					//Vision.logger.debug("Oh dear (i,j) = " + Integer.toString(i) + "," + Integer.toString(j) + ")");
					c = new Color(img.getRGB(i,j));

					GB = Math.abs((c.getBlue() - c.getGreen()));
					RG = Math.abs((c.getRed() - c.getGreen()));
					//RB = Math.abs((c.getRed() - c.getBlue()));

					if(isRed(c, GB)){ //  was inside  RB > 50 && RG > 50
						img.setRGB(i, j, Color.red.getRGB()); //Red Ball
						randy = Math.random();
						if (randy > 0 && randy <= 0.2){						    
							redCountA++;
							redCentroidA.setLocation(redCentroidA.getX() + i, redCentroidA.getY() + j);
						}else if (randy > 0.2 && randy <= 0.4){
							redCountB++;
							redCentroidB.setLocation(redCentroidB.getX() + i, redCentroidB.getY() + j);
						}else if (randy > 0.4 && randy <= 0.6){
							redCountC++;
							redCentroidC.setLocation(redCentroidC.getX() + i, redCentroidC.getY() + j);
						}else if (randy > 0.6 && randy <= 0.8){
							redCountD++;
							redCentroidD.setLocation(redCentroidD.getX() + i, redCentroidD.getY() + j);
						}else if (randy > 0.8 && randy <= 1){
							redCountE++;
							redCentroidE.setLocation(redCentroidE.getX() + i, redCentroidE.getY() + j);
						}

					}
					else if (isYellow(c)) {
						setCs(i,j,right,left,top,bottom, img);
						if (isYellow(cS) && isYellow(cE) && isYellow(cEE) && isYellow(cEN) && isYellow(cSS) && isYellow(cSW)  ){
							img.setRGB(i, j, Color.yellow.getRGB()); // Yellow robot
							yellowRobotX.add(i);
							yellowRobotY.add(j);
							randy = Math.random();
							if (randy > 0 && randy <= 0.2){						    
								yellowCountA++;
								yellowCentroidA.setLocation(yellowCentroidA.getX() + i, yellowCentroidA.getY() + j);
							}else if (randy > 0.2 && randy <= 0.4){
								yellowCountB++;
								yellowCentroidB.setLocation(yellowCentroidB.getX() + i, yellowCentroidB.getY() + j);
							}else if (randy > 0.4 && randy <= 0.6){
								yellowCountC++;
								yellowCentroidC.setLocation(yellowCentroidC.getX() + i, yellowCentroidC.getY() + j);
							}else if (randy > 0.6 && randy <= 0.8){
								yellowCountD++;
								yellowCentroidD.setLocation(yellowCentroidD.getX() + i, yellowCentroidD.getY() + j);
							}else if (randy > 0.8 && randy <= 1){
								yellowCountE++;
								yellowCentroidE.setLocation(yellowCentroidE.getX() + i, yellowCentroidE.getY() + j);
							}
							yellowPixels.add(new Point(i,j));
						}
					}
					else if (isBlue(c)){
						setCs(i,j,right,left,top,bottom, img);
						if (isBlue(cS) && isBlue(cE) && isBlue(cEE) && isBlue(cEN) && isBlue(cSS) && isBlue(cSW)  ){
							img.setRGB(i, j, Color.blue.getRGB()); // Blue robot 
							blueRobotX.add(i);
							blueRobotY.add(j);
							randy = Math.random();
							if (randy > 0 && randy <= 0.2){						    
								blueCountA++;
								blueCentroidA.setLocation(blueCentroidA.getX() + i, blueCentroidA.getY() + j);
							}else if (randy > 0.2 && randy <= 0.4){
								blueCountB++;
								blueCentroidB.setLocation(blueCentroidB.getX() + i, blueCentroidB.getY() + j);
							}else if (randy > 0.4 && randy <= 0.6){
								blueCountC++;
								blueCentroidC.setLocation(blueCentroidC.getX() + i, blueCentroidC.getY() + j);
							}else if (randy > 0.6 && randy <= 0.8){
								blueCountD++;
								blueCentroidD.setLocation(blueCentroidD.getX() + i, blueCentroidD.getY() + j);
							}else if (randy > 0.8 && randy <= 1){
								blueCountE++;
								blueCentroidE.setLocation(blueCentroidE.getX() + i, blueCentroidE.getY() + j);
							}
							bluePixels.add(new Point(i,j));
						}
						//make blue thresholds for the different pitches in that [pitch][x] style
					}
					else if (isGreen(c,GB,RG))  {
						img.setRGB(i,j, Color.green.getRGB()); // GreenPlates 
						if (Point.distance(	worldState.getBlueRobot().getPosition().getCentre().x,
								worldState.getBlueRobot().getPosition().getCentre().y,
								i,j) < 34) {
							blueGreenPlate.add(new Point(i,j));
						} 
						if (Point.distance(	worldState.getYellowRobot().getPosition().getCentre().x,
								worldState.getYellowRobot().getPosition().getCentre().y,
								i,j) < 34){
							yellowGreenPlate.add(new Point(i,j));
						}

					}

				}
			}

			if (redCountA == 0) redCountA++;
			if (redCountB == 0) redCountB++;
			if (redCountC == 0) redCountC++;
			if (redCountD == 0) redCountD++;
			if (redCountE == 0) redCountE++;
			if (blueCountA == 0) blueCountA++;
			if (blueCountB == 0) blueCountB++;
			if (blueCountC == 0) blueCountC++;
			if (blueCountD == 0) blueCountD++;
			if (blueCountE == 0) blueCountE++;
			if (yellowCountA == 0) yellowCountA++;
			if (yellowCountB == 0) yellowCountB++;
			if (yellowCountC == 0) yellowCountC++;
			if (yellowCountD == 0) yellowCountD++;
			if (yellowCountE == 0) yellowCountE++;


			//TODO: Run these points through the parralax fix
			totalRedX = 0;
			totalRedY = 0;
			numRedCentroids = 0;


			redCentroidA.setLocation(redCentroidA.getX()/redCountA, redCentroidA.getY()/redCountA);
			redCentroidB.setLocation(redCentroidB.getX()/redCountB, redCentroidB.getY()/redCountB);
			redCentroidC.setLocation(redCentroidC.getX()/redCountC, redCentroidC.getY()/redCountC);
			redCentroidD.setLocation(redCentroidD.getX()/redCountD, redCentroidD.getY()/redCountD);
			redCentroidE.setLocation(redCentroidE.getX()/redCountE, redCentroidE.getY()/redCountE);

			totalYellowX = 0;
			totalYellowY = 0;
			numYellowCentroids = 0;


			yellowCentroidA.setLocation(yellowCentroidA.getX()/yellowCountA, yellowCentroidA.getY()/yellowCountA);
			yellowCentroidB.setLocation(yellowCentroidB.getX()/yellowCountB, yellowCentroidB.getY()/yellowCountB);
			yellowCentroidC.setLocation(yellowCentroidC.getX()/yellowCountC, yellowCentroidC.getY()/yellowCountC);
			yellowCentroidD.setLocation(yellowCentroidD.getX()/yellowCountD, yellowCentroidD.getY()/yellowCountD);
			yellowCentroidE.setLocation(yellowCentroidE.getX()/yellowCountE, yellowCentroidE.getY()/yellowCountE);

			totalBlueX = 0;
			totalBlueY = 0;
			numBlueCentroids = 0;


			blueCentroidA.setLocation(blueCentroidA.getX()/blueCountA, blueCentroidA.getY()/blueCountA);
			blueCentroidB.setLocation(blueCentroidB.getX()/blueCountB, blueCentroidB.getY()/blueCountB);
			blueCentroidC.setLocation(blueCentroidC.getX()/blueCountC, blueCentroidC.getY()/blueCountC);
			blueCentroidD.setLocation(blueCentroidD.getX()/blueCountD, blueCentroidD.getY()/blueCountD);
			blueCentroidE.setLocation(blueCentroidE.getX()/blueCountE, blueCentroidE.getY()/blueCountE);

			c = new Color(img.getRGB((int)redCentroidA.getX(), (int)redCentroidA.getY()));
			if (isRed(c, GB)) {
				totalRedX += redCentroidA.getX();
				totalRedY += redCentroidA.getY();
				numRedCentroids++;
			}
			c = new Color(img.getRGB((int)redCentroidB.getX(), (int)redCentroidB.getY()));
			if (isRed(c, GB)) {
				totalRedX += redCentroidB.getX();
				totalRedY += redCentroidB.getY();
				numRedCentroids++;
			}
			c = new Color(img.getRGB((int)redCentroidC.getX(), (int)redCentroidC.getY()));
			if (isRed(c, GB)) {
				totalRedX += redCentroidC.getX();
				totalRedY += redCentroidC.getY();
				numRedCentroids++;
			}
			c = new Color(img.getRGB((int)redCentroidD.getX(), (int)redCentroidD.getY()));
			if (isRed(c, GB)) {
				totalRedX += redCentroidD.getX();
				totalRedY += redCentroidD.getY();
				numRedCentroids++;
			}
			c = new Color(img.getRGB((int)redCentroidE.getX(), (int)redCentroidE.getY()));
			if (isRed(c, GB)) {
				totalRedX += redCentroidE.getX();
				totalRedY += redCentroidE.getY();
				numRedCentroids++;
			}

			if (numRedCentroids == 0){
				numRedCentroids++;
			}

			redX = (int)(totalRedX/numRedCentroids);
			redY = (int)(totalRedY/numRedCentroids);

			c = new Color(img.getRGB((int)yellowCentroidA.getX(), (int)yellowCentroidA.getY()));
			if (isYellow(c)) {
				totalYellowX += yellowCentroidA.getX();
				totalYellowY += yellowCentroidA.getY();
				numYellowCentroids++;
			}
			c = new Color(img.getRGB((int)yellowCentroidB.getX(), (int)yellowCentroidB.getY()));
			if (isYellow(c)) {
				totalYellowX += yellowCentroidB.getX();
				totalYellowY += yellowCentroidB.getY();
				numYellowCentroids++;
			}
			c = new Color(img.getRGB((int)yellowCentroidC.getX(), (int)yellowCentroidC.getY()));
			if (isYellow(c)) {
				totalYellowX += yellowCentroidC.getX();
				totalYellowY += yellowCentroidC.getY();
				numYellowCentroids++;
			}
			c = new Color(img.getRGB((int)yellowCentroidD.getX(), (int)yellowCentroidD.getY()));
			if (isYellow(c)) {
				totalYellowX += yellowCentroidD.getX();
				totalYellowY += yellowCentroidD.getY();
				numYellowCentroids++;
			}
			c = new Color(img.getRGB((int)yellowCentroidE.getX(), (int)yellowCentroidE.getY()));
			if (isYellow(c)) {
				totalYellowX += yellowCentroidE.getX();
				totalYellowY += yellowCentroidE.getY();
				numYellowCentroids++;
			}

			if (numYellowCentroids == 0){
				numYellowCentroids++;
			}

			yellowX = (int)(totalYellowX/numYellowCentroids);
			yellowY = (int)(totalYellowY/numYellowCentroids);

			c = new Color(img.getRGB((int)blueCentroidA.getX(), (int)blueCentroidA.getY()));
			if (isBlue(c)) {
				totalBlueX += blueCentroidA.getX();
				totalBlueY += blueCentroidA.getY();
				numBlueCentroids++;
			}
			c = new Color(img.getRGB((int)blueCentroidB.getX(), (int)blueCentroidB.getY()));
			if (isBlue(c)) {
				totalBlueX += blueCentroidB.getX();
				totalBlueY += blueCentroidB.getY();
				numBlueCentroids++;
			}
			c = new Color(img.getRGB((int)blueCentroidC.getX(), (int)blueCentroidC.getY()));
			if (isBlue(c)) {
				totalBlueX += blueCentroidC.getX();
				totalBlueY += blueCentroidC.getY();
				numBlueCentroids++;
			}
			c = new Color(img.getRGB((int)blueCentroidD.getX(), (int)blueCentroidD.getY()));
			if (isBlue(c)) {
				totalBlueX += blueCentroidD.getX();
				totalBlueY += blueCentroidD.getY();
				numBlueCentroids++;
			}
			c = new Color(img.getRGB((int)blueCentroidE.getX(), (int)blueCentroidE.getY()));
			if (isBlue(c)) {
				totalBlueX += blueCentroidE.getX();
				totalBlueY += blueCentroidE.getY();
				numBlueCentroids++;
			}

			if (numBlueCentroids == 0){
				numBlueCentroids++;
			}

			blueX = (int)(totalBlueX/numBlueCentroids);
			blueY = (int)(totalBlueY/numBlueCentroids);

			blueGreenPlate4Points = plate.getCorners(blueGreenPlate);
			yellowGreenPlate4Points = plate.getCorners(yellowGreenPlate);

			worldState.getBlueRobot().getPosition().setCorners(blueGreenPlate4Points);
			worldState.getYellowRobot().getPosition().setCorners(yellowGreenPlate4Points);

			Point fixBall = new Point(redX,redY);
			if ((redX != 0) && (redY != 0)) {
				if (worldState.getBarrelFix()){
					worldState.setBallPosition(fixBall);
				}else{     
					worldState.setBallPosition(DistortionFix.barrelCorrected(fixBall));
				}
			}

			Point fixBlue = new Point(blueX,blueY);
			if ((blueX != 0) && (blueY != 0)) {
				if (worldState.getBarrelFix()){
					worldState.setBlueRobotPosition(fixParallax(fixBlue,worldState.getBlueRobot()));
				}else{
					worldState.setBlueRobotPosition(fixParallax(DistortionFix.barrelCorrected(fixBlue),worldState.getBlueRobot()));
				}
			}

			Point fixYell = new Point(yellowX,yellowY);
			if ((yellowX != 0) && (yellowY != 0)) {
				if (worldState.getBarrelFix()){
					worldState.setYellowRobotPosition(fixParallax(fixYell,worldState.getYellowRobot()));
				}else{
					worldState.setYellowRobotPosition(fixParallax(DistortionFix.barrelCorrected(fixYell),worldState.getYellowRobot()));
				}
			}

			for(Point p : bluePixels){

				if( plate.isInRectangle(p,blueGreenPlate4Points)  ){
					if (worldState.getBarrelFix()){
						newBluePixels.add(fixParallax(p,worldState.getBlueRobot()));
					}else{
						newBluePixels.add(fixParallax(DistortionFix.barrelCorrected(p),worldState.getBlueRobot()));
					}
				}
			}
			for(Point p : yellowPixels){

				if( plate.isInRectangle(p,yellowGreenPlate4Points) ){
					if (worldState.getBarrelFix()){
						newYellowPixels.add(fixParallax(p,worldState.getYellowRobot()));
					}else{
						newYellowPixels.add(fixParallax(DistortionFix.barrelCorrected(p),worldState.getYellowRobot()));
					}
				}
			}

			worldState.setBluePixels(newBluePixels);
			worldState.setYellowPixels(newYellowPixels);

			//The above is supposed to filter the pixels and pick up only the T pixels, but the orientation then is always with the (0,0) point 


			blueGreenPlate.clear();
			yellowGreenPlate.clear();

		}

		return img;

	}

	/**
	 * Correct for parallax
	 * 
	 * Take in a point and what object it belongs to and unparallax it
	 * 
	 * @param p The point to correct
	 * @param m The object it belongs to, i.e. robot centroid, necessary so we can get height
	 * @return The corrected point in type Point
	 */

	public Point fixParallax(Point p, MovingObject m){
		float x = 	(worldState.getPitch().getPitchWidth()/2.0f)*(m.getHeight()) - 
		(VisionTools.pixelsToCM(p.x) * m.getHeight()) + 
		(worldState.getPitch().getCameraHeight() * VisionTools.pixelsToCM(p.x));
		x = (float) (x / worldState.getPitch().getCameraHeight());

		float y = 	(worldState.getPitch().getPitchHeight()/2.0f)*(m.getHeight()) - 
		(VisionTools.pixelsToCM(p.y) * m.getHeight()) + 
		(worldState.getPitch().getCameraHeight() * VisionTools.pixelsToCM(p.y));
		y = (float) (y / worldState.getPitch().getCameraHeight());

		y = VisionTools.cmToPixels(y);
		x = VisionTools.cmToPixels(x);

		return new Point((int)x,(int)y);
	}

	/**
	 * Does the connected component stuff
	 * 
	 * Use a bastardised 8 connected component method, is supposed to have clustering
	 * tacked on to the end, but I just use it for reducing noise.  Currently it is this
	 * method that makes the yellow T zigzaggy.  It checks a points surrounding pixels to
	 * work out if it is noise or not. 
	 * 
	 * @param x - x coordinate of point
	 * @param y - y coordinate of point
	 * @param right To check if we go out of bounds of pitch
	 * @param left To check if we go out of bounds of pitch
	 * @param top To check if we go out of bounds of pitch
	 * @param bottom To check if we go out of bounds of pitch
	 * @param img To get the colour of the neighbouring pixels
	 */

	public void setCs(int x, int y, int right, int left, int top, int bottom, BufferedImage img){
		if (x + 1 < right){
			cE = new Color(img.getRGB(x+1,y));
		}else {
			cE = c;
		}
		if (y + 1 < bottom){
			cS = new Color(img.getRGB(x,y+1));
		}else {
			cS = c;
		}
		if ((x + 1 < right) && (y - 1 > top)){
			cEN = new Color(img.getRGB(x+1,y-1));
		}else {
			cEN = c;
		}
		if ((x + 2 < right)){
			cEE = new Color(img.getRGB(x+2,y));
		}else {
			cEE = c;
		}
		if ((y + 2 < bottom)){
			cSS = new Color(img.getRGB(x,y+2));
		}else {
			cSS = c;
		}
		if ((x - 1 > left) && (y + 1 < bottom)){
			cSW = new Color(img.getRGB(x-1,y+1));
		}else {
			cSW = c;
		}

	}
	
	/**
	 * Check if a pixel is blue robot blue
	 * 
	 * @param c Colour to check
	 * @return True if blue
	 */

	public boolean isBlue(Color c){
		return ( (c.getRed() <= ts.getBlue_r()) && (c.getBlue() > ts.getBlue_b())   && (c.getGreen() <= ts.getBlue_g()));
	}
	
	/**
	 * Check if a pixel is ball red
	 * 
	 * @param c - Colour to check
	 * @param GB - Difference between green and blue channel
	 * @return True if red
	 */
	public boolean isRed(Color c, int GB){
		return ( (c.getRed() > ts.getBall_r()) &&  (c.getBlue() <= ts.getBall_b()) &&  (c.getGreen() <= ts.getBall_g()) && GB < 60 );
	}
	
	/**
	 * Check if part of green plate
	 * 
	 * @param c Colour to check
	 * @param GB Difference in green and blue channel
	 * @param RG Difference in red and green channel
	 * @return True if green
	 */
	public boolean isGreen(Color c, int GB, int RG){

		return ( GB > ts.getGreen_GB() && RG > ts.getGreen_RG() && c.getGreen() > ts.getGreen_g());


	}
	
	/**
	 * Check if pixel is yellow robot
	 * 
	 * @param c Colour to check
	 * @return True if part of yellow T
	 */
	public boolean isYellow(Color c){
		return ((c.getRed() >= ts.getYellow_r_low()) && (c.getRed() <= ts.getYellow_r_high()) && (c.getGreen() >= ts.getYellow_g_low()) && (c.getGreen() <= ts.getYellow_g_high()) && (c.getBlue() >= ts.getYellow_b_low()) && (c.getBlue() <= ts.getYellow_b_high()));
	}

	/**
	 * Get the points that make of the green plate of the blue T
	 * @return the points
	 */
	public Point[] getBlueGreenPlate4Points(){
		return blueGreenPlate4Points;
	}
	
	/**
	 * Get the points that make of the green plate of the yellow T
	 * @return the points
	 */
	public Point[] getYellowGreenPlate4Points(){
		return yellowGreenPlate4Points;
	}

}
