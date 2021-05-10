package com.example.BattleRoyal.bean;

/**
 * @author Owner
 */
public class PlayerLocation {

    User spwan;

    private Angle angle;

    private Position position;

    public User getSpwan() {
        return spwan;
    }

    public void setSpwan(User spwan) {
        this.spwan = spwan;
    }

    public Angle getAngle() {
        return angle;
    }

    public void setAngle(Angle angle) {
        this.angle = angle;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public static class Angle {
        private double x, y, z;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }

    public static class Position {
        private double x, y, z;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }
}
