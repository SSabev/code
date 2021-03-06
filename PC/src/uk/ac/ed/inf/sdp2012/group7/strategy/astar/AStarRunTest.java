package uk.ac.ed.inf.sdp2012.group7.strategy.astar;


import java.awt.Point;
import java.util.ArrayList;


public class AStarRunTest {
	


		public static void main(String[] args) {
			
			
			
			// set start and end points of the path
			Node start = new Node(new Point(30,16), 0);
			Node end = new Node(new Point(15,15), 0);
			
			//cost tests
			int a = 2;
			int b = 5;
			
			// set obstacles
			Node opposition = new Node(new Point(26,17), b);
			opposition.setOpposition(true);
			Node ball = new Node(new Point(15,15), a);
			ball.setBall(true);
			
			//Use Lists
			ArrayList<Node> balls = new ArrayList<Node>();
			ArrayList<Node> oppositions = new ArrayList<Node>();
			
			//boundary maker
			int boundary = 4;
			
			//Fill Arrays
			
			for(int i = ball.x - boundary; i < ball.x + boundary; i++){
				for(int j = ball.y - boundary; j < ball.y + boundary; j++){
					Node tempBall = new Node(new Point(i,j), a);
					tempBall.setBall(true);
					//System.out.println(tempBall.getX() + " " + tempBall.getY());
					balls.add(tempBall);
				}
			}
			
			for(int i = opposition.x - 2*boundary; i < opposition.x + boundary; i++){
				for(int j = opposition.y - 2*boundary; j < opposition.y + boundary; j++){
					Node tempOpp = new Node(new Point(i,j), b);
					tempOpp.setOpposition(true);
					//System.out.println(tempOpp.getX() + " " + tempOpp.getY());
					oppositions.add(tempOpp);
				}
			}
			
			// run the algorithm
			new AStarRun(29, 58, start, end, balls, oppositions);
		}
}
