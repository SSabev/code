package uk.ac.ed.inf.sdp2012.group7.strategy.oldastar;

import java.awt.Point;

public class OldNode implements Comparable<OldNode> {
	
	// properties of this node
	OldAreaMap map;
	boolean visited;
	float distanceFromStart;
	float heuristicDistanceFromGoal;
	OldNode previousNode;
	int x;
	int y;
	boolean isObstacle;
	boolean isStart;
	private boolean isGoal;
	
	public OldNode(int x, int y) {
		this(x,y,false,Float.MAX_VALUE,false,false,false);
	}
	
	OldNode (int x, int y, boolean visited, float distanceFromStart, boolean isObstical, boolean isStart, boolean isGoal) {
		this.x = x;
		this.y = y;
		this.visited = visited;
		this.distanceFromStart = distanceFromStart;
		this.isObstacle = isObstical;
		this.isStart = isStart;
		this.isGoal = isGoal;
	}


	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public float getDistanceFromStart() {
		return distanceFromStart;
	}

	public void setDistanceFromStart(float f) {
		this.distanceFromStart = f;
	}

	public OldNode getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(OldNode previousNode) {
		this.previousNode = previousNode;
	}
	
	public float getHeuristicDistanceFromGoal() {
		return heuristicDistanceFromGoal;
	}

	public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
		this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isObstical() {
		return isObstacle;
	}

	public void setObstical(boolean isObstical) {
		this.isObstacle = isObstical;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isGoal() {
		return isGoal;
	}

	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}

	
	// converts a single node into a point
	public Point nodeToPoint(OldNode node) {
		Point point = new Point(node.getX(),node.getY());
		return point;
	}
	
	public Point nodeToPoint(){
		return nodeToPoint(this);
	}


	public boolean equals(OldNode node) {
		return (node.x == x) && (node.y == y);
	}

	@Override
	public int compareTo(OldNode otherNode) {
		float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
		float otherTotalDistanceFromGoal = otherNode.getHeuristicDistanceFromGoal() + otherNode.getDistanceFromStart();
		
		// if positive, otherNode is chosen as it has a smaller total distance from goal
		return (int) (thisTotalDistanceFromGoal - otherTotalDistanceFromGoal);
	}
}