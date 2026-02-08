package OptimizeRoute;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class OptimizeRouteDemo {

    static Route deliveryRoute = new Route();
    static Scanner sc = new Scanner(System.in);


    public static void main(String[] args) {

        System.out.println("Welcome to the Delivery Route Optimize System!");

        while (true) {
            printMenu();
            int choice = sc.nextInt();
            if (choice < 1 || choice > 8) {
                System.out.println("Invalid choice, Please try again!");
                continue;
            }
            switch (choice) {
                case 1:orderGenerator();
                break;

                case 2:
                    if (routeCheck()) {
                    }else {
                        deliveryRoute.printRoute();
                        System.out.printf("Total mileage is %.2f miles.\n", deliveryRoute.totalDistance() / 100);
                    }
                    break;

                case 3:
                    if (routeCheck()) {
                    }else {
                        optimizeRoute();
                    }
                break;

                case 4:
                    if(routeCheck()){
                    }else {
                        exportToCSV();
                    }
                break;

                case 5:
                    if(routeCheck()){
                    }else {
                        addMoreLocation();
                    }
                break;

                case 6:
                    if(routeCheck()){
                    }else {
                        searchAndDeleteLocation();
                    }
                break;

                case 7:System.exit(0);
            }

        }

    }

    public static void printMenu(){
        System.out.println("press [1] to generate delivery order.");
        System.out.println("press [2] to view current routes and summary of order.");
        System.out.println("press [3] to launch smart route optimization algorithm.");
        System.out.println("press [4] to export data to Excel.");
        System.out.println("press [5] to add more delivery stop.");
        System.out.println("press [6] to search & delete package.");
        System.out.println("press [7] to exit system.");
        System.out.println("Please enter your option:");
    }

    public static void orderGenerator(){
        Set<String> usedTrackingNumbers = new HashSet<String>();
        deliveryRoute = new Route();
        Random r = new Random();

        int totalStop = r.nextInt(181) + 70;

        for (int i = 0; i < totalStop; i++) {

            int serialNumber = i + 1;
            String trackingNumber = "";
            boolean isRepeat = false;
            while (!isRepeat) {
                String prefix = "10010";
                int randomPart = r.nextInt(1000000);
                String initialTrackingNumber = prefix + String.format("%06d", randomPart);

                if(!usedTrackingNumbers.contains(initialTrackingNumber)){
                    trackingNumber = initialTrackingNumber;
                    usedTrackingNumbers.add(initialTrackingNumber);
                    isRepeat = true;
                }
            }

            int x = r.nextInt(1000);
            int y = r.nextInt(1000);
            Location newStop = new Location(trackingNumber, x, y, serialNumber);
            deliveryRoute.addLocation(newStop);
        }
        System.out.println("Your order has been generated! Press 2 to view full delivery list.");
    }

    public static void optimizeRoute(){
        System.out.println("Order under optimization...");
        double initialTotalDistance = deliveryRoute.totalDistance();
        System.out.printf("The total mileage before optimize is %.2f miles.\n", initialTotalDistance / 100);
        deliveryRoute.optimizeRoute();
        System.out.printf("The total mileage after optimize is %.2f miles.\n", deliveryRoute.totalDistance() / 100);
        System.out.printf("Your route has been saved %.2f miles!\n", (initialTotalDistance - deliveryRoute.totalDistance()) / 100);
    }

    public static void exportToCSV(){
        String fileName = "delivery_route.csv";
        try(FileWriter writer = new FileWriter(fileName)){

            writer.write("Order,Tracking Number,X,Y,SerialNumber\n");

            Node current = deliveryRoute.getHead();
            int index =1;

            while(current!=null){
                Location locating = current.getCurrentLocation();
                String line = String.format("%d,%s,%d,%d,%d\n", index,locating.getTrackingNumber(),
                        locating.getX(),locating.getY(),locating.getSerialNumber());
                writer.write(line);

                current = current.getNext();
                index++;
            }
        } catch (IOException e) {
            System.out.println("Export to CSV failed!" + e.getMessage());
        }
        System.out.println("Export successful!");
    }


    public static void addMoreLocation(){
        String newTrackingNumber;
        while(true){
            System.out.println("Please enter new package tracking number:");
            String inputVerify = sc.next().trim();
            if(inputVerify.matches("10010\\d{6}")){
                newTrackingNumber = inputVerify;
                break;
            }else {
                System.out.println("Invalid tracking number! Please make sure tracking number " +
                        "start with 10010 and total 11 digital numbers!");
            }
        }
        Random r = new Random();
        int newX = r.nextInt(1000);
        int newY = r.nextInt(1000);
        int newSerialNumber = deliveryRoute.getSize() + 1;

        Location newAddedLocation = new Location(newTrackingNumber, newX, newY, newSerialNumber);
        deliveryRoute.addLocation(newAddedLocation);

        System.out.println("Your location has been added!");
        deliveryRoute.optimizeRoute();
        System.out.println("Your route has been updated!");
    }

    public static void searchAndDeleteLocation(){
        String newTrackingNumber;
        while(true){
            System.out.println("Please enter package tracking number:");
            String inputVerify = sc.next().trim();
            if(inputVerify.matches("10010\\d{6}")){
                newTrackingNumber = inputVerify;
                break;
            }else {
                System.out.println("Invalid tracking number! Please make sure tracking number " +
                        "start with 10010 and total 11 digital numbers!");
            }
        }

        Node foundNode = deliveryRoute.findTrackingNumber(newTrackingNumber);
        if(foundNode != null){
            System.out.println("The package has been found!");
            System.out.println("Package serial number is:" + foundNode.getCurrentLocation().getSerialNumber() +
                    ", and the address is: " + foundNode.getCurrentLocation().getX() + ", " + foundNode.getCurrentLocation().getY());
            System.out.println("Are you sure you want to delete this package from your route? enter y/n.");
            while(true) {
                String confirm = sc.next();
                if (confirm.equalsIgnoreCase("y")) {
                    if (deliveryRoute.deleteLocation(newTrackingNumber)) {
                        deliveryRoute.deleteLocation(newTrackingNumber);
                        deliveryRoute.optimizeRoute();
                        System.out.println("Your package has been deleted and your route has been updated!");
                    }else {
                        System.out.println("Error: Couldn't delete the package. Please contact customer service!");
                    }
                    break;
                } else if (confirm.equalsIgnoreCase("n")) {
                    System.out.println("OK, back to menu now.");
                    break;
                } else {
                    System.out.println("Invalid confirmation! Please try again!");
                }
            }
        }else  {
            System.out.println("The package was not found! Please double check your tracking number.");
        }
    }

    private static boolean routeCheck(){
        if(deliveryRoute.getHead() == null) {
            System.out.println("Warning: No delivery route found. Please generate one first!");
            return true;
        }else {
            return false;
        }
    }
}
