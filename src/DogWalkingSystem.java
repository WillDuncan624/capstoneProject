import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Volunteer {
    String name;
    String password;

    public Volunteer(String name, String password) {
        this.name = name;
        this.password = password;
    }
}

class Dog {
    int id;
    String name;
    String breed;
    String location;
    LocalDateTime lastWalk;

    public Dog(int id, String name, String breed, String location, LocalDateTime lastWalk) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.location = location;
        this.lastWalk = lastWalk;
    }
}

class WalkLog {
    String volunteerName;
    int dogId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String bathroom;
    String notes;

    public WalkLog(String volunteerName, int dogId, LocalDateTime startTime, LocalDateTime endTime, String bathroom, String notes) {
        this.volunteerName = volunteerName;
        this.dogId = dogId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bathroom = bathroom;
        this.notes = notes;
    }
}

public class DogWalkingSystem {
    private static final String VOLUNTEER_FILE = "src/volunteers.txt";
    private static final String DOG_FILE = "src/dogs.txt";
    private static final String WALK_LOG_FILE = "walk_logs.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static String loggedInVolunteer = null;
    private static List<Dog> dogs = new ArrayList<>();

    public static void main(String[] args) {
        loadDogs();
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
                System.out.print("Enter Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Password: ");
                String password = scanner.nextLine();

                if (authenticateVolunteer(name, password)) {
                    loggedInVolunteer = name;
                    showVolunteerMenu();
                } else {
                    System.out.println("Invalid Credentials!");
                }
            } else if (choice == 2) {
                System.out.println("Exiting...");
                System.exit(0);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static boolean authenticateVolunteer(String name, String password) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(VOLUNTEER_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(name) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading volunteer file.");
        }
        return false;
    }

    private static void showVolunteerMenu() {
        while (true) {
            System.out.println("\n=== Volunteer Dashboard ===");
            System.out.println("1. View Sorted Dogs Needing Walks");
            System.out.println("2. Search Dog by Name (Recursion)");
            System.out.println("3. Start a Dog Walk");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                displaySortedDogs();
            } else if (choice == 2) {
                System.out.print("Enter Dog Name: ");
                String dogName = scanner.nextLine();
                Dog foundDog = searchDog(dogs, dogName, 0);
                if (foundDog != null) {
                    System.out.println("Dog Found: " + foundDog.name + " (Breed: " + foundDog.breed + ")");
                } else {
                    System.out.println("Dog not found.");
                }
            } else if (choice == 3) {
                System.out.print("Enter Dog ID (from the list): ");
                int dogId = scanner.nextInt();
                scanner.nextLine();
                startDogWalk(dogId);
            } else if (choice == 4) {
                loggedInVolunteer = null;
                System.out.println("Logged out.");
                showLoginMenu();
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void loadDogs() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DOG_FILE));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    LocalDateTime lastWalk = LocalDateTime.parse(parts[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    dogs.add(new Dog(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], lastWalk));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading dog file.");
        }
    }

    private static void updateDogLastWalk(int dogId, LocalDateTime newTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            List<String> lines = Files.readAllLines(Paths.get(DOG_FILE));
            BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE));

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 5 && Integer.parseInt(parts[0]) == dogId) {
                    // Replace the last walk time
                    String updatedLine = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newTime.format(formatter);
                    writer.write(updatedLine);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            writer.close();

            // Also update the dog object in memory
            for (Dog dog : dogs) {
                if (dog.id == dogId) {
                    dog.lastWalk = newTime;
                    break;
                }
            }

            System.out.println("Dog's last walk time updated in file.");
        } catch (IOException e) {
            System.err.println("Error updating dog's last walk time.");
            e.printStackTrace();
        }
    }

    private static void displaySortedDogs() {
        bubbleSortDogs(); // sorts from oldest walk to most recent
        System.out.println("\nTop 3 Dogs Needing Walks (Longest Wait):");
        int count = Math.min(3, dogs.size());
        for (int i = 0; i < count; i++) {
            Dog dog = dogs.get(i);
            System.out.println("ID: " + dog.id + ", Name: " + dog.name + ", Breed: " + dog.breed +
                    ", Location: " + dog.location + ", Last Walk: " + dog.lastWalk);
        }
    }

    private static void bubbleSortDogs() {
        int n = dogs.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (dogs.get(j).lastWalk.isAfter(dogs.get(j + 1).lastWalk)) {
                    Collections.swap(dogs, j, j + 1);
                }
            }
        }
    }

    private static Dog searchDog(List<Dog> dogList, String name, int index) {
        if (index >= dogList.size()) return null;
        if (dogList.get(index).name.equalsIgnoreCase(name)) return dogList.get(index);
        return searchDog(dogList, name, index + 1);
    }

    private static void startDogWalk(int dogId) {
        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Started walking Dog ID " + dogId);

        System.out.println("Walking... (Simulating 5 seconds)");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        endDogWalk(dogId, startTime);
    }

    private static void endDogWalk(int dogId, LocalDateTime startTime) {
        System.out.print("Did the dog use the bathroom? (none/pee/poop): ");
        String bathroom = scanner.nextLine();
        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine();

        LocalDateTime endTime = LocalDateTime.now();
        logWalk(dogId, startTime, endTime, bathroom, notes);
        updateDogLastWalk(dogId, endTime);
    }

    private static void logWalk(int dogId, LocalDateTime startTime, LocalDateTime endTime, String bathroom, String notes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WALK_LOG_FILE, true))) {
            writer.write(loggedInVolunteer + "," + dogId + "," + startTime + "," + endTime + "," + bathroom + "," + notes);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to walk log file.");
        }
    }
}