package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public abstract class GameObject {
    private Point position;
    private String imagePath;
    private int imageWidth;
    private int imageHeight;
    private boolean active = true;
    private Room belongsTo;

    public GameObject(int x, int y, String imagePath, int imageWidth, int imageHeight) {
        position = new Point(x, y);
        this.imagePath = imagePath;
    }

    public GameObject(Point position, String imagePath, int imageWidth, int imageHeight) {
        this.position = new Point(position);
        this.imagePath = imagePath;
    }

    public Point getPosition() {
        return position;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setPosition(int x, int y) {
        this.position = new Point(x, y);
    }

    public int getImageWidth() {
        return imageWidth;
    }


    public int getImageHeight() {
        return imageHeight;
    }


    public void drawImage() {
        StdDraw.picture(position.x, position.y, imagePath, imageWidth, imageHeight);
    }


    public void update() {

    }

    public void setRoom(Room belongsTo) {
        this.belongsTo = belongsTo;
    }

    public Room getRoom() {
        return belongsTo;
    }

    public void destroy() {
        active = false;
    }
}
