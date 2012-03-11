/**
 * 
 */
package uk.ac.ed.inf.sdp2012.group7.strategy.planning;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import uk.ac.ed.inf.sdp2012.group7.strategy.astar.*;
import uk.ac.ed.inf.sdp2012.group7.vision.worldstate.WorldState;

/**
 * @author s0955088
 *
 */
public class Plan {

	private TargetDecision targetDecision;
	private AStarRun aStarNav;
	private AllStaticObjects allStaticObjects;
	private ArrayList<Node> path, robotObstacles, ballObstacles;
	public static final Logger logger = Logger.getLogger(Plan.class);

	//World state info
	private AllMovingObjects allMovingObjects;
	private WorldState worldState = WorldState.getInstance();
	
	//targets and navs
	private Node target;
	private Node navPoint;


	/**
	 * 
	 */
	//Constructor
	public Plan(AllStaticObjects allStaticObjects, AllMovingObjects allMovingObjects) {
		
		logger.debug("Plan being generated");
		
		this.allStaticObjects = allStaticObjects;
		this.allMovingObjects = allMovingObjects;
		allMovingObjects.update(allStaticObjects);
		
		//Set up obstacles created by opposition
		//redundant?
		//this.opposition = new OppositionPrediction(allMovingObjects, this.allStaticObjects);
		
		//Add the opposition obstacles to the overall obstacles
		//redundant?
		//this.obstacles = opposition.getDefaultObstacles();
		
		//Setup target for A*
		this.targetDecision = new TargetDecision(this.allMovingObjects, this.allStaticObjects);
		
		this.target = this.targetDecision.getTarget();
		this.navPoint = this.targetDecision.getNavPoint();
		
		logger.debug("Target Decision Position: " + targetDecision.getTarget().toString());
		logger.debug("NavPoint Decision Position: " + targetDecision.getNavPoint().toString());
		logger.debug("Ball Position: " + this.allMovingObjects.getBallPosition());
		logger.debug("Robot Position: " + this.allMovingObjects.getOurPosition().toString());
		logger.debug("Their Robot Position: " + this.allMovingObjects.getTheirPosition().toString());
		
		//
		this.ballObstacles = this.allMovingObjects.getBallObstacles();
		this.robotObstacles = this.allMovingObjects.getRobotObstacles();
		
		//a* for Current position to navPpoint
		aStarNav = new AStarRun(this.allStaticObjects.getHeight(),
								this.allStaticObjects.getWidth(),
								allMovingObjects.getOurPosition(),
								navPoint,
								this.ballObstacles,
								this.robotObstacles
							);
		
		
		//Requires method to convert from path to ArrayList<Point>
		//Now grab path through A* method
		this.path = aStarNav.getPath();
		
		//Now add target to the end:
		this.path.add(this.target);
		
		logger.debug("Path length: " + this.path.size());
		
		

	}	
	
	public ArrayList<Node> getPath(){
		return this.path;
	}
	
	//action = 0; nothing 
	//action = 1; kick
	public int getAction(){
		return targetDecision.getAction();
	}
	
	public int getPlanType(){
		return targetDecision.getPlanType();
	}
	
	//Plan Monitor
	public double getOurRobotAngle(){
		return allMovingObjects.getOurAngle();
	}
	
	public Point getBallPosition(){
		return allMovingObjects.getBallPosition();
	}
	
	//Plan Monitor
	public Point getOurRobotPosition() {
		return allMovingObjects.getOurPosition();
	}
	
	//For Control Interface
	public Point getOurRobotPositionVisual() {
		return worldState.getOurRobot().getPosition().getCentre();
	}
	
	//For testing
	public AStarRun getAStar(){
		return this.aStarNav;
	}
	
	//For Control Interface
	public double getNodeInPixels(){
		return this.allStaticObjects.getNodeInPixels();
	}
	
	//For Control Interface
	public int getHeightInNodes(){
		return this.allStaticObjects.getHeight();
	}
	
	//Return angle required...
	public double getAngleWanted(){
		return this.targetDecision.getAngleWanted();
	}
	
	public Point getTarget(){
		return this.target;
	}
	
	public Point getNavPoint(){
		return this.navPoint;
	}
	
	public Point getCentreOfTheirGoal(){
		return this.allStaticObjects.getCentreOfTheirGoal();
	}
	
	public Point getCentreOfOurGoal(){
		return this.allStaticObjects.getCentreOfOurGoal();
	}
	
	public AllStaticObjects getAllStaticObjects(){
		return this.allStaticObjects;
	}
	
	public double getDistanceInCM() {
		return this.targetDecision.getTargetCM();
	}
	
	public int getMapHeight(){
		return this.allStaticObjects.getHeight();
	}
	
	public int getMapWidth(){
		return this.allStaticObjects.getWidth();
	}

	public ArrayList<Node> getRobotObstacles(){
		return this.robotObstacles;
	}
	
	public ArrayList<Node> getBallObstacles(){
		return this.ballObstacles;
	}
	
}
