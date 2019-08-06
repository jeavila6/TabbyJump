package edu.utep.cs5381.tabbyjump;

public class Hitbox {
    float top;
    float left;
    float bottom;
    float right;
    float height;

    boolean intersects(Hitbox hitbox) {
        boolean hit = false;

        if (this.right > hitbox.left && this.left < hitbox.right) {
            // Intersecting on x axis

            if (this.top < hitbox.bottom && this.bottom > hitbox.top) {
                // Intersecting on y axis as well

                hit = true;
            }
        }

        return hit;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
