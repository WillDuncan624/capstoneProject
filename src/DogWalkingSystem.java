import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class DogWalkingSystem {
    private static final String volunteerFile = "volunteers.txt";
    private static final String dogFile = "dogs.txt";
    private static final String walkLogFile = "walk_logs.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static String loggedInVolunteer = null;

    public static void main(String[] args) {
        showLoginMenu();
    }

    private static void showLoginMenu() {
        while (true) {
            System.out.println("\n=== Dog Walking System ===");
            System.out.println("1. Login as Volunteer");
            System.out.println("2. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (authenticateVolunteer(username, password)) {
                    loggedInVolunteer = username;
                    showVolunteerMenu();
                } else {
                    System.out.println("Invalid Credentials!");
                }
            } else if (choice == 2) {
                System.out.println("Exiting...");
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static boolean authenticateVolunteer(String username, String password) {
            /*for (String line : ) { //the volunteer login list
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }*/
        return false;
    }

    private static void showVolunteerMenu() {
        while (true) {
            System.out.println("\n=== Volunteer Dashboard ===");
            System.out.println("1. View Dogs Needing Walks");
            System.out.println("2. Start a Dog Walk");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                displayDogList();
            } else if (choice == 2) {
                System.out.print("Enter Dog ID (from the list): ");
                int dogId = scanner.nextInt();
                scanner.nextLine();
                startDogWalk(dogId);
            } else if (choice == 3) {
                loggedInVolunteer = null;
                System.out.println("Logged out.");
                showLoginMenu();
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void displayDogList() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(dogFile));
            System.out.println("\nDogs Needing Walks:");
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    System.out.println("ID: " + parts[0] +
                            ", Name: " + parts[1] +
                            ", Breed: " + parts[2] +
                            ", Location: " + parts[3] +
                            ", Last Walk: " + parts[4]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading dog file.");
        }
    }

    private static void startDogWalk(int dogId) {

    }

    private static void endDogWalk(int dogId, LocalDateTime startTime) {

    }

    private static void logWalk(int dogId, LocalDateTime startTime, LocalDateTime endTime, String bathroom, String notes) {

    }

    private static void updateLastWalkTime(int dogId, LocalDateTime newTime) {
        //use
    }
}