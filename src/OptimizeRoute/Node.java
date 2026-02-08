package OptimizeRoute;

public class Node {

    private Location currentLocation;
    private Node previous;
    private Node next;

    public Node(Location currentLocation) {
        this.currentLocation = currentLocation;
        this.previous = null;
        this.next = null;
    }

    public Node getNext() {
        return next;
    }
    public Node getPrevious() {
        return previous;
    }
    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }
}
