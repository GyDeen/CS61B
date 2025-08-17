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
   private ArrayList<PivotPoint> pivots;
   private Direction initDirection;

   public Hallway(int fromMainId, int toMainId,
                  Point startDoor, List pivots, Point endDoor, int width) {
      this.fromRoomId = fromMainId;
      this.toRoomId = toMainId;
      startLocation = startDoor;
      this.pivots = new ArrayList<>();
      endLocation = endDoor;
   }


   /* getter for hallway */
   public int fromMainId()   { return fromRoomId; }
   public int toMainId()     { return toRoomId; }
   public Point startDoor()  { return startLocation; }
   public Point endDoor()    { return endLocation; }


   /** Generate a hallway based on given room
    * @param fromRoom the room that the hallway start
    * @param endRoom the destination of the hallway
    * @param random the random seed
    * @param fromIdx the index of the from room
    * @param toIdx the index of the to room*/
   public static void generate(MainRoom fromRoom, MainRoom endRoom, Random random, int fromIdx, int toIdx) {
      Point fromPos = fromRoom.getLocation(), toPos = endRoom.getLocation();

      boolean onFromRight = fromPos.x < toPos.x;
      boolean onFromTop = fromPos.y < toPos.y;

      // Determine the end position range based on the relative position. Avoid detour
      // E.g. If the destination room is on the Right and Top of the starting room, it will hit on either the bottom or
      // the left of the destination room
      Point endPos;
      if (onFromRight && onFromTop) { // On Top-Right of the start room
         boolean onLeft = random.nextBoolean();
         if (onLeft) endPos = new Point(endRoom.getLeft(), random.nextInt(endRoom.getBottom() + 1, endRoom.getTop()));
         else endPos = new Point(random.nextInt(endRoom.getLeft() + 1, endRoom.getRight()), endRoom.getBottom());

      } else if (!onFromRight && !onFromTop) { // On Bottom-Left of the start room
         boolean onRight = random.nextBoolean();
         if (onRight) endPos = new Point(endRoom.getRight(), random.nextInt(endRoom.getBottom() + 1, endRoom.getTop()));
         else endPos = new Point(random.nextInt(endRoom.getLeft() + 1, endRoom.getRight()), endRoom.getTop());

      } else if (!onFromRight && onFromTop) { // On Top-Left of the start room
         boolean onRight = random.nextBoolean();
         if (onRight) endPos = new Point(endRoom.getRight(), random.nextInt(endRoom.getBottom() + 1, endRoom.getTop()));
         else endPos = new Point(random.nextInt(endRoom.getLeft() + 1, endRoom.getRight()), endRoom.getBottom());

      } else { // On Bottom-Right of the start room
         boolean onLeft = random.nextBoolean();
         if (onLeft) endPos = new Point(endRoom.getLeft(), random.nextInt(endRoom.getBottom() + 1, endRoom.getTop()));
         else endPos = new Point(random.nextInt(endRoom.getLeft() + 1, endRoom.getRight()), endRoom.getTop());

      }

      Direction initDir = randomDirection(null, random, 3);
      Point startLoc = switch (initDir) {
          case LEFT -> new Point(fromRoom.getLeft(), fromPos.y);
          case RIGHT -> new Point(fromRoom.getRight(), fromPos.y);
          case UP -> new Point(fromPos.x, fromRoom.getTop());
          case DOWN -> new Point(fromPos.x, fromRoom.getBottom());
      };


   }

   /* Return a random direction. If there is initial direction, avoid the initial direction. Also, based on input pair
    determine the direction that it could return */
   private static Direction randomDirection(Direction initDirection, Random random, int pair) {
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
