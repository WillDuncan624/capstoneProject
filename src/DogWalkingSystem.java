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

    public String toFileFormat() {
        return name + "," + password;
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

    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return id + "," + name + "," + breed + "," + location + "," + lastWalk.format(formatter);
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

class Node<T> {
    T data;
    Node<T> next;

    public Node(T data) {
        this.data = data;
        this.next = null;
    }
}

class LinkedStack<T> {
    private Node<T> top;

    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
    }

    public T pop() {
        if (top == null) return null;
        T data = top.data;
        top = top.next;
        return data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public T peek() {
        return top != null ? top.data : null;
    }
}

class LinkedQueue<T> {
    private Node<T> front, rear;

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (rear != null) {
            rear.next = newNode;
        }
        rear = newNode;
        if (front == null) {
            front = rear;
        }
    }

    public T dequeue() {
        if (front == null) return null;
        T data = front.data;
        front = front.next;
        if (front == null) rear = null;
        return data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public void clear() {
        front = rear = null;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        Node<T> current = front;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}

public class DogWalkingSystem {
    private static final String VOLUNTEER_FILE = "src/volunteers.txt";
    private static final String DOG_FILE = "src/dogs.txt";
    private static final String WALK_LOG_FILE = "walk_logs.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static String loggedInVolunteer = null;
    private static List<Dog> dogs = new LinkedList<>();
    private static List<Volunteer> volunteers = new LinkedList<>();
    private static LinkedStack<WalkLog> recentWalks = new LinkedStack<>();
    private static LinkedQueue<Dog> dogPriorityQueue = new LinkedQueue<>();

    public static void main(String[] args) {
        loadDogs();
        loadVolunteers();
        updatePriorityQueue();
        showGeneralLoginMenu();
    }


    private static void updatePriorityQueue() {
        List<Dog> sortedDogs = new ArrayList<>(dogs);
        sortedDogs.sort(Comparator.comparing(d -> d.lastWalk));
        dogPriorityQueue.clear();
        for (Dog dog : sortedDogs) {
            dogPriorityQueue.enqueue(dog);
        }
    }

    private static void displaySortedDogs() {
        updatePriorityQueue();
        System.out.println("\nTop 5 Dogs Needing Walks:");
        List<Dog> priorityList = dogPriorityQueue.toList();
        for (int i = 0; i < Math.min(5, priorityList.size()); i++) {
            Dog dog = priorityList.get(i);
            System.out.println("ID: " + dog.id + ", Name: " + dog.name + ", Breed: " + dog.breed +
                    ", Location: " + dog.location + ", Last Walk: " + dog.lastWalk);
        }
    }


    private static void showGeneralLoginMenu() {
        while (true) {
            System.out.println("\n=== Dog Walking System Login ===");
            System.out.println("1. Login as Volunteer");
            System.out.println("2. Login as Employee");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Volunteer Name: ");
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
                showEmployeeLoginMenu();
            } else if (choice == 3) {
                System.out.println("Exiting...");
                System.exit(0);
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void showEmployeeLoginMenu() {
        System.out.println("\n=== Employee Login ===");
        System.out.print("Enter Employee Password: ");
        String password = scanner.nextLine();

        if (password.equals("vc4321")) {
            showVolunteerCoordinatorMenu();
        } else if (password.equals("ac9876")) {
            showAnimalCoordinatorMenu();
        } else {
            System.out.println("Invalid Employee Password.");
        }
    }

    private static void showVolunteerCoordinatorMenu() {
        while (true) {
            System.out.println("\n=== Volunteer Coordinator Menu ===");
            System.out.println("1. Add Volunteer");
            System.out.println("2. Remove Volunteer");
            System.out.println("3. Print Volunteers List");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                addVolunteer();
            } else if (choice == 2) {
                removeVolunteer();
            } else if (choice == 3) {
                printAllVolunteers();
            }
            else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void showAnimalCoordinatorMenu() {
        while (true) {
            System.out.println("\n=== Animal Coordinator Menu ===");
            System.out.println("1. Add Dog");
            System.out.println("2. Remove Dog");
            System.out.println("3. Print Dog List");
            System.out.println("4. Edit Dog Section");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                addDog();
            }
            else if (choice == 2) {
                removeDog();
            }
            else if (choice == 3) {
                printAllDogs();
            }
            else if (choice == 4) {
                editDogLocation();
            }
            else if (choice == 5) {
                break;
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


    private static void addDog() {
        System.out.print("Enter Dog ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        // Check for duplicate ID
        for (Dog dog : dogs) {
            if (dog.id == id) {
                System.out.println("A dog with this ID already exists. Please use a unique ID.");
                return;
            }
        }

        System.out.print("Enter Dog Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Breed: ");
        String breed = scanner.nextLine();
        System.out.print("Enter Location: ");
        String location = scanner.nextLine();
        LocalDateTime now = LocalDateTime.now();

        Dog newDog = new Dog(id, name, breed, location, now);
        dogs.add(newDog);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE, true))) {
            writer.newLine(); // Ensure each dog is added on a new line
            writer.write(newDog.toFileFormat());
            System.out.println("Dog added successfully.");
        } catch (IOException e) {
            System.err.println("Error adding dog to file: " + e.getMessage());
        }
    }

    private static void removeDog() {
        System.out.print("Enter Dog ID to remove: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        dogs.removeIf(d -> d.id == id);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE));
            for (Dog d : dogs) {
                writer.write(d.toFileFormat());
                writer.newLine();
            }
            writer.close();
            System.out.println("Dog removed successfully.");
        } catch (IOException e) {
            System.err.println("Error removing dog.");
        }
    }

    private static void editDogLocation() {
        System.out.print("Enter Dog ID to update location: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Dog targetDog = null;
        for (Dog dog : dogs) {
            if (dog.id == id) {
                targetDog = dog;
                break;
            }
        }

        if (targetDog == null) {
            System.out.println("Dog with ID " + id + " not found.");
            return;
        }

        System.out.println("Current Location: " + targetDog.location);
        System.out.print("Enter New Location: ");
        String newLocation = scanner.nextLine();
        targetDog.location = newLocation;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE))) {
            for (Dog dog : dogs) {
                writer.write(dog.toFileFormat());
                writer.newLine();
            }
            System.out.println("Dog location updated successfully.");
        } catch (IOException e) {
            System.err.println("Error updating dog file: " + e.getMessage());
        }
    }

    private static void printAllDogs() {
        System.out.println("\n=== All Dogs in the System ===");
        if (dogs.isEmpty()) {
            System.out.println("No dogs available.");
            return;
        }
        for (Dog dog : dogs) {
            System.out.println("ID: " + dog.id + ", Name: " + dog.name + ", Breed: " + dog.breed +
                    ", Location: " + dog.location + ", Last Walk: " + dog.lastWalk);
        }
    }

    private static void addVolunteer() {
        try {
            System.out.print("Enter Volunteer Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter 4-digit Password: ");
            String password = scanner.nextLine();

            BufferedWriter writer = new BufferedWriter(new FileWriter(VOLUNTEER_FILE, true));
            writer.write(name + "," + password);
            writer.newLine();
            writer.close();
            System.out.println("Volunteer added successfully.");
        } catch (IOException e) {
            System.err.println("Error adding volunteer.");
        }
    }

    private static void removeVolunteer() {
        System.out.print("Enter Volunteer Name to remove: ");
        String name = scanner.nextLine();

        try {
            List<String> lines = Files.readAllLines(Paths.get(VOLUNTEER_FILE));
            BufferedWriter writer = new BufferedWriter(new FileWriter(VOLUNTEER_FILE));
            for (String line : lines) {
                if (!line.startsWith(name + ",")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
            System.out.println("Volunteer removed successfully.");
        } catch (IOException e) {
            System.err.println("Error removing volunteer.");
        }
    }
    private static void printAllVolunteers() {
        System.out.println("\n=== All Volunteers in the System ===");
        if (volunteers.isEmpty()) {
            System.out.println("No volunteers available.");
            return;
        }
        for (Volunteer v : volunteers) {
            System.out.println("Name: " + v.name + ", Password: " + v.password);
        }
    }

    private static void showVolunteerMenu() {
        while (true) {
            System.out.println("\n=== Volunteer Dashboard ===");
            System.out.println("1. View Dogs Needing Walks");
            System.out.println("2. Search Dog by Name");
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
                    System.out.println("Dog Found: " + foundDog.name + " ID: " + foundDog.id + " (" + foundDog.breed + ")");
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
                showGeneralLoginMenu();
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private static void loadVolunteers() {
        try (FileInputStream fis = new FileInputStream(VOLUNTEER_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    volunteers.add(new Volunteer(parts[0], parts[1]));
                } else {
                    System.err.println("Skipping invalid volunteer entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading volunteer file: " + e.getMessage());
        }
    }

    private static void loadDogs() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (FileInputStream fis = new FileInputStream(DOG_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String breed = parts[2];
                        String location = parts[3];
                        LocalDateTime lastWalk = LocalDateTime.parse(parts[4], formatter);
                        dogs.add(new Dog(id, name, breed, location, lastWalk));
                    } catch (Exception e) {
                        System.err.println("Skipping malformed dog entry: " + line);
                    }
                } else {
                    System.err.println("Skipping incomplete dog entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading dog file: " + e.getMessage());
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

/*
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
*/

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
        System.out.print("Did the dog pee? (yes/no): ");
        String peed = scanner.nextLine().trim().toLowerCase();

        System.out.print("Did the dog poop? (yes/no): ");
        String pooped = scanner.nextLine().trim().toLowerCase();

        String bathroom;
        if (peed.equals("yes") && pooped.equals("yes")) {
            bathroom = "pee and poop";
        } else if (peed.equals("yes")) {
            bathroom = "pee";
        } else if (pooped.equals("yes")) {
            bathroom = "poop";
        } else {
            bathroom = "none";
        }

        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine();

        LocalDateTime endTime = LocalDateTime.now();
        WalkLog walkLog = new WalkLog(loggedInVolunteer, dogId, startTime, endTime, bathroom, notes);
        recentWalks.push(walkLog);
        logWalk(walkLog);
        updateDogLastWalk(dogId, endTime);
    }

    private static void logWalk(WalkLog log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WALK_LOG_FILE, true))) {
            writer.write(log.volunteerName + "," + log.dogId + "," + log.startTime + "," + log.endTime + "," + log.bathroom + "," + log.notes);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to walk log file.");
        }
    }

}
