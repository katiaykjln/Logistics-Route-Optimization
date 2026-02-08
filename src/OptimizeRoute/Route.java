package OptimizeRoute;

import java.util.ArrayList;

public class Route {

    private Node head;
    private Node tail;
    private int size;

    public Route() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public int getSize() {
        return size;
    }
    public Node getHead() {
        return head;
    }
    public Node getTail() {
        return tail;
    }

    public void addLocation(Location newLocation){
        Node newNode = new Node(newLocation);

        if(head == null){
            head = newNode;
            tail = newNode;
        }else {
            tail.setNext(newNode);
            newNode.setPrevious(tail);
            tail = newNode;
        }

        size++;
    }

    public void printRoute(){
        Node eachNoode = head;
        while(eachNoode != null){
            System.out.println(eachNoode.getCurrentLocation().toString());
            eachNoode = eachNoode.getNext();
        }
        System.out.println("This route have total " + getSize() + " delivery locations.");
    }

    public double totalDistance(){
        double totalDistance = 0;
        Node eachNode = head;
        while(eachNode != null && eachNode.getNext() != null){
            totalDistance += eachNode.getCurrentLocation().distanceTo(eachNode.getNext().getCurrentLocation());
            eachNode = eachNode.getNext();
        }
        return totalDistance;
    }

    private Location findNearestLocation(Location currentLocation, ArrayList<Location> locationList){
        Location nearestLocation = null;
        double minDistance = Double.MAX_VALUE;

        for(Location location : locationList){
            double distance = currentLocation.distanceTo(location);
            if(distance < minDistance){
                minDistance = distance;
                nearestLocation = location;
            }
        }
        return nearestLocation;
    }

    public void optimizeRoute(){
        if(head == null){
            return;
        }
        ArrayList<Location> tempList = new ArrayList<>();
        Node current = head;
        while(current != null){
            tempList.add(current.getCurrentLocation());
            current = current.getNext();
        }
        head = null;
        tail = null;
        size = 0;

        Location firstStop = null;
        double distanceFromWarehouse = Double.MAX_VALUE;
        Location warehouseLocation = new Location("Warehouse",0,0,0);
        for(Location location : tempList){
            double distance = warehouseLocation.distanceTo(location);
            if(distance < distanceFromWarehouse){
                distanceFromWarehouse = distance;
                firstStop = location;
            }
        }

        if(firstStop != null){
            addLocation(firstStop);
            tempList.remove(firstStop);
        }

        Location lastAdded = firstStop;
        while(!tempList.isEmpty()){
            Location nearest = findNearestLocation(lastAdded, tempList);
            addLocation(nearest);
            tempList.remove(nearest);
            lastAdded = nearest;
        }
    }

    public Node findTrackingNumber(String trackingNumber){
        Node currentNode = head;
        while(currentNode != null){
            if(currentNode.getCurrentLocation().getTrackingNumber().equals(trackingNumber)){
                return currentNode;
            }
            currentNode = currentNode.getNext();
        }
        return null;
    }

    public boolean deleteLocation(String trackingNumber){
        Node targetNode = findTrackingNumber(trackingNumber);
        if(targetNode == null){
            return false;
        }

        Node nextNode = targetNode.getNext();
        Node previousNode = targetNode.getPrevious();

        if(targetNode == head){
            head = nextNode;
            if(head != null){
                head.setPrevious(null);
            }
        }
        else {
            previousNode.setNext(nextNode);
        }

        if(targetNode == tail){
            tail = previousNode;
        }
        else {
            nextNode.setPrevious(previousNode);
        }

        targetNode.setNext(null);
        targetNode.setPrevious(null);
        size--;
        return true;
    }
}
