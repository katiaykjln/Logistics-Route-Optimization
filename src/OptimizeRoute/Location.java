package OptimizeRoute;

public class Location {

    final String trackingNumber;
    final int x;
    final int y;
    private int serialNumber;

    public Location(String trackingNumber, int x, int y, int serialNumber) {
        this.trackingNumber = trackingNumber;
        this.x = x;
        this.y = y;
        this.serialNumber = serialNumber;
    }

    public double distanceTo(Location comparedLocation){
        int xDistance = x - comparedLocation.x;
        int yDistance = y - comparedLocation.y;
        return Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    @Override
    public String toString(){
        return "Tracking number is: " + trackingNumber + ", address is: " + x + ", " + y +", package serial number is: " + serialNumber;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
}
