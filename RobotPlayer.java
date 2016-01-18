package jeffPlayer;


import battlecode.common.*;

import java.util.*;
public class RobotPlayer
{
	static RobotController rc;
	static ArrayList<MapLocation> pastLocations = new ArrayList<MapLocation>();
	static MapLocation target = new MapLocation(435,159);
	static Random rn = new Random();
	
	public static void run(RobotController rcIn) throws GameActionException{
		rc=rcIn;
		

		while (true){
			try{
				if (rc.isCoreReady())
				moveHere(target);
				Clock.yield();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
		}
	}

	private static void moveHere(MapLocation target) throws GameActionException 
	{
		MapLocation current = rc.getLocation();
		int cost = 0;
		Direction[] DIRECTIONS = new Direction[]{Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST,Direction.NORTH};
		HashMap<Direction, Integer> costOfMoves = new HashMap<Direction,Integer>();
		MinSort minSort = new MinSort(costOfMoves);
		TreeMap <Direction,Integer> movesMinSort = new TreeMap<Direction,Integer>(minSort);
		
		
		for(Direction c: DIRECTIONS)  //Calculating costs of all moves and putting into HashMap
		{
			MapLocation candidateLocation = rc.getLocation().add(c);
			cost = current.add(c).distanceSquaredTo(target);
			if (rc.senseRubble(candidateLocation) > 0)
			{
//				System.out.println("Rubble is " + rc.senseRubble(candidateLocation));
				cost+= (int) rc.senseRubble(candidateLocation);
			}
			costOfMoves.put(c,cost);
			cost = 0;
		}
		
		movesMinSort.putAll(costOfMoves);  //Sorting all costs from least to greatest 
//		System.out.println("New Set");   //Debug code for calculations
//		for (Map.Entry<Direction, Integer> entry2 : movesMinSort.entrySet()) {
//		    Direction key = entry2.getKey();
//		    Integer value = entry2.getValue();
//		    System.out.println(key + " " + value);
//		}
		
		
		for (Map.Entry<Direction,Integer> entry : movesMinSort.entrySet())  //Will find least move and go that direction
		{
			System.out.println(entry.getValue());
			MapLocation moveCandidate = rc.getLocation().add(entry.getKey());
			
			if (rc.canMove(entry.getKey()) && !pastLocations.contains(moveCandidate))
			{
				rc.move(entry.getKey());
				if (moveCandidate.equals(target))  //If reached target - clear pastLocations.
				{
					pastLocations.clear();
					return;
				}
				pastLocations.add(moveCandidate);
				if (pastLocations.size() > 25)
				{
					pastLocations.remove(0); //If greater then 25, remove from list so can go back to - value can be changed as made more accurate
				}
				return;
			}
		}
		
		return;
		
	}
	
	
	@SuppressWarnings("rawtypes")
	static class MinSort implements Comparator{

		HashMap base;

		public MinSort(HashMap base){
			this.base = base;
		}
		public int compare(Object a, Object b)
		{
			if ( ( (int) base.get(a)) >=  (int) (base.get(b)) ){
				return 1;
			}else{
				return -1;
			}
		}
	}
	
	
}