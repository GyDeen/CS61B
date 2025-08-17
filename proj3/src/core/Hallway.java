package core;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Hallway {

   private class PivotPoint {
      private Point pivotPoint;
      private enum direction {LEFT, RIGHT, UP, DOWN};
   }

   private int id;
   private int fromRoomId;
   private int toRoomId;
   private Point startLocation;
   private Point endLocation;
   private ArrayList pivots;
   private Direction initDirection;

   public Hallway(int fromMainId, int toMainId,
                  Point startDoor, List pivots, Point endDoor, int width) {
      this.fromRoomId = fromMainId;
      this.toRoomId = toMainId;
      startLocation = startDoor;
      this.pivots = new ArrayList<>((Collection) pivots);
      endLocation = endDoor;
   }


   /* getter for hallway */
   public int fromMainId()   { return fromRoomId; }
   public int toMainId()     { return toRoomId; }
   public Point startDoor()  { return startLocation; }
   public ArrayList pivots(){ return pivots; }
   public Point endDoor()    { return endLocation; }


   /** Generate a hallway based on given room
    * @param fromRoom the room that the hallway start
    * @param endRoom the destination of the hallway
    * @param random the random seed */
   public static void generate(MainRoom fromRoom, MainRoom endRoom, Random random) {
      Point fromPos = fromRoom.getLocation(), toPos = endRoom.getLocation();
      Direction initDir = switch (random.nextInt(4)) {
         case 0 -> Direction.LEFT;
         case 1 -> Direction.RIGHT;
         case 2 -> Direction.UP;
         default -> Direction.DOWN;
      };
   }

   /* Return a random direction. If there is initial direction, avoid the initial direction. Also, based on input pair
    determine the direction that it could return */
   private Direction randomDirection(Direction initDirection, Random random, int pair) {
      if (initDirection == null) return Direction.values()[random.nextInt(Direction.values().length)];
      if (initDirection == Direction.LEFT ||  initDirection == Direction.RIGHT) {
         switch (pair) {
            case 0: return Direction.UP;
            case 1: return Direction.DOWN;
         }
      } else if (initDirection == Direction.UP || initDirection == Direction.DOWN) {
         switch (pair) {
            case 0: return Direction.LEFT;
            case 1: return Direction.RIGHT;
         }
      }

      return Direction.values()[random.nextInt(Direction.values().length)];
   }


}
