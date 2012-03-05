package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import uk.ac.ed.inf.sdp2012.group7.strategy.Strategy;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AStarRun;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.AreaMap;
import uk.ac.ed.inf.sdp2012.group7.strategy.astar.Node;
import uk.ac.ed.inf.sdp2012.group7.vision.VisionTools;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;
//import uk.ac.ed.inf.sdp2012.group7.strategy.Arc;
//import uk.ac.ed.inf.sdp2012.group7.strategy.ControlInterface;


public class PlanMonitor {
	
	private Plan currentPlan;
	private WorldState worldState = WorldState.getInstance();
	private double nodeInPixels;

	
	public PlanMonitor(Plan plan){
		currentPlan = plan;
		nodeInPixels = currentPlan.getAllStaticObjects().getNodeInPixels();
	}
	
	public PlanMonitor(){
		
	}

	public void setPlan(Plan plan){
		currentPlan = plan;
		nodeInPixels = currentPlan.getAllStaticObjects().getNodeInPixels();
	}
	
	public void savePlan(){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("plan.txt",true));
			out.write(generateASCIIPlan().toString());
		} catch (IOException ex) { 
			Strategy.logger.error("Could not write to file: " + ex.getMessage());
		} finally {
			if(out != null){
				try{
					out.close();
				} catch (Exception ex){
					Strategy.logger.error("Error in closing buffered writer: " + ex.getMessage());
				}
			}
		}
	}
	
	public void outputPlan(){
		long start = System.currentTimeMillis();
		String[][] plan = generateASCIIPlan();
		System.out.println(plan.toString());
		generateImage(plan);
		long timed = System.currentTimeMillis() - start;
		Strategy.logger.info("Time to generate plan render: " + timed + "ms");
	}
	
	public String[][] generateASCIIPlan(){
		AreaMap map = currentPlan.getAStarRun().getAreaMap();
		ArrayList<Node> waypoints = null;
		try{
			waypoints = currentPlan.getAStarRun().getPath().getWayPoints();
		} catch (Exception ex){
			Strategy.logger.error("Waypoins in generateASCIIPlan is null");
			return new String[0][0];
		}
		if(map.getNodes().length <= 0) return new String[0][0];
		String[][] ascii = new String[map.getMapHeight()][map.getMapWidth()];
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[y].length;x++){
				ascii[y][x] = " ";
			}
		}
		for(int x = 0; x < ascii[0].length; x++){
			ascii[0][x] = ".";
		}
		for(int x = 0; x < ascii[0].length; x++){
			ascii[ascii.length-1][x] = ".";
		}
		for(int y = 0; y < ascii.length; y++){
			ascii[y][0] = ".";
		}
		for(int y = 0; y < ascii.length; y++){
			ascii[y][ascii[y].length - 1] = ".";
		}
				
		for(int y = 0; y < ascii.length; y++){
			for(int x = 0; x < ascii[y].length; x++){
				for(Node n : waypoints){
					if(n.nodeToPoint().equals(new Point(x,y))){
						ascii[y][x] = "#";
						continue;
					}
				}
				Node n = map.getNode(x,y);
				//if(n.isGoal()) ascii[y][x] = "T";
				if(n.isObstical()) ascii[y][x] = "X";
				if(n.isObstical()) ascii[y][x] = "X";
				if(currentPlan.getAllStaticObjects().getCentreOfTheirGoal().equals(new Point(x,y))) ascii[y][x] = "C";
				if(n.isStart()) ascii[y][x] = "S";
				if(n.isVisited()) ascii[y][x] = " ";
				if(currentPlan.getAStarRun().getPath().getWayPoint(currentPlan.getAStarRun().getPath().getLength()-1).equals(new Point(x,y))) ascii[y][x] = "N";
				if(currentPlan.getAllMovingObjects().getBallPosition().equals(new Point(x,y))) ascii[y][x] = "B";
			}
		}
		return ascii;
	}
	
	public void saveImage(String[][] text){
		try {
		    BufferedImage bi = generateImage(text);
		    File outputfile = new File("planoutput.png");
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException ex) {
			Strategy.logger.error("Error saving image: " + ex.getMessage());
		}
	}
	
	public BufferedImage generateImage(String[][] text){
		int width = worldState.getPitch().getWidthInPixels();
		int height = worldState.getPitch().getHeightInPixels();
		BufferedImage im = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		im = generateOverlay(text,im);
		//im = markData(im);
		worldState.setOverlay(im);
		return im;
	}
	
	/*public BufferedImage markData(BufferedImage im){
		Graphics2D graphics = im.createGraphics();
		ControlInterface ci = new ControlInterface(5);
		Arc arc = ControlInterface.chooseArc(currentPlan);
		double radius = arc.getRadius();
		double angle = currentPlan.getOurRobotAngle();
		if(arc.isLeft()) {
			angle = angle + (Math.PI * 2) - (Math.PI/2);
		} else {
			angle = angle + Math.PI/2;
		}
		angle = angle % (Math.PI*2);
		double retAngle = (Math.PI + angle) % (Math.PI * 2);
		int x = (int)(currentPlan.getNodeInPixels()*currentPlan.getOurRobotPosition().x + (currentPlan.getNodeInPixels()*radius*Math.cos(angle)));
		int y = (int)(currentPlan.getNodeInPixels()*currentPlan.getOurRobotPosition().y + (currentPlan.getNodeInPixels()*radius*Math.sin(angle)));
		
		Strategy.logger.info("Arc info: " + String.format("%d,%d,%d,%d,%d,%d",x, y, 
				(int)radius, (int)radius, 
				(int)Math.toDegrees(VisionTools.convertAngleBack(retAngle)), 
				(int)Math.toDegrees(VisionTools.convertAngleBack(Math.PI))));
		
		graphics.drawArc(	x, y, 
							(int)radius, (int)radius, 
							Math.abs((int)Math.toDegrees(VisionTools.convertAngleBack(retAngle))), 
							Math.abs((int)Math.toDegrees(VisionTools.convertAngleBack(Math.PI))));
		
		graphics.drawOval(	(int)(	currentPlan.getNodeInPixels() * 
									currentPlan.getOurRobotPositionVisual().x)-15, 
							(int)(	currentPlan.getNodeInPixels() *
									currentPlan.getOurRobotPositionVisual().y)-15,
							30, 30);
		return im;
	}*/
		
    public BufferedImage generateOverlay(String[][] ascii, BufferedImage image){
    	Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        graphics.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        for(int y = 0; y < ascii.length; y++){
        	for(int x = 0; x < ascii[y].length; x++){
        		if(ascii[y][x] != " "){
        			int xp = (int)(x*nodeInPixels + (nodeInPixels / 4) + 0.5);
        			int yp = (int)(y*nodeInPixels + (nodeInPixels / 4) + 0.5);
        			int width = (int)((nodeInPixels/2)+0.5);
        			int height = width;
        			graphics.fillRect(xp, yp, width, height);
        		}
        	}
        }
        //graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        return image;
    }
	
	
}
