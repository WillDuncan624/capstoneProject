import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

class MainCode {
    public static void main(String[] args) {
        DogManager dogManager = new DogManager();
        VolunteerManager volunteerManager = new VolunteerManager();
        WalkManager walkManager = new WalkManager(dogManager);
        new DogWalkingGUI(dogManager, volunteerManager, walkManager);

        //DogWalkingApp dogWalkingApp = new DogWalkingApp();
        //dogWalkingApp.run();
    }
}

class DogWalkingApp {
    private VolunteerManager volunteerManager;
    private DogManager dogManager;
    private WalkManager walkManager;
    private MenuManager menuManager;

    public DogWalkingApp() {
        volunteerManager = new VolunteerManager();
        dogManager = new DogManager();
        walkManager = new WalkManager(dogManager);
        menuManager = new MenuManager(volunteerManager, dogManager, walkManager);
    }

    public void run() {
        menuManager.showGeneralLoginMenu();
    }
}

// Walk manager
class WalkManager {
    private static final String WALK_LOG_FILE = "walk_logs.txt";
    private LinkedStack<WalkLog> recentWalks;
    private DogManager dogManager;
    private String loggedInVolunteer;

    public WalkManager(DogManager dogManager) {
        this.dogManager = dogManager;
        this.recentWalks = new LinkedStack<>();
    }

    public void setLoggedInVolunteer(String name) {
        this.loggedInVolunteer = name;
    }

    public String getLoggedInVolunteer() {
        return this.loggedInVolunteer;
    }

    public LinkedStack<WalkLog> getRecentWalks() {
        return this.recentWalks;
    }

    public void startDogWalk(int dogId) {
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

    private void endDogWalk(int dogId, LocalDateTime startTime) {
        Scanner scanner = new Scanner(System.in);

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
        dogManager.updateDogLastWalk(dogId, endTime);
    }

    public void logWalk(WalkLog log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WALK_LOG_FILE, true))) {
            writer.write(log.volunteerName + "," + log.dogId + "," + log.startTime + "," + log.endTime + "," + log.bathroom + "," + log.notes);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to walk log file.");
        }
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

    public boolean isEmpty() {
        return front == null;
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

// MenuManager declaration placeholder (you can insert full MenuManager implementation here if needed)
class MenuManager {
    private final Scanner scanner;
    private final VolunteerManager volunteerManager;
    private final DogManager dogManager;
    private final WalkManager walkManager;

    public MenuManager(VolunteerManager vm, DogManager dm, WalkManager wm) {
        this.volunteerManager = vm;
        this.dogManager = dm;
        this.walkManager = wm;
        scanner = new Scanner(System.in);
    }

    public void showGeneralLoginMenu() {
        while (true) {
            System.out.println("=== Dog Walking System Login ===");
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

                if (volunteerManager.authenticate(name, password)) {
                    walkManager.setLoggedInVolunteer(name);
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

    private void showEmployeeLoginMenu() {
        System.out.println("=== Employee Login ===");
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

    private void showVolunteerCoordinatorMenu() {
        while (true) {
            System.out.println("=== Volunteer Coordinator Menu ===");
            System.out.println("1. Add Volunteer");
            System.out.println("2. Remove Volunteer");
            System.out.println("3. Print Volunteers List");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Volunteer Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter 4-digit Password: ");
                String password = scanner.nextLine();
                volunteerManager.addVolunteer(name, password);
            } else if (choice == 2) {
                System.out.print("Enter Volunteer Name to remove: ");
                String name = scanner.nextLine();
                volunteerManager.removeVolunteer(name);
            } else if (choice == 3) {
                volunteerManager.printVolunteers();
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void showAnimalCoordinatorMenu() {
        while (true) {
            System.out.println("=== Animal Coordinator Menu ===");
            System.out.println("1. Add Dog");
            System.out.println("2. Remove Dog");
            System.out.println("3. Print Dog List");
            System.out.println("4. Edit Dog Section");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter Dog ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter Dog Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Breed: ");
                String breed = scanner.nextLine();
                System.out.print("Enter Location: ");
                String location = scanner.nextLine();
                LocalDateTime now = LocalDateTime.now();
                dogManager.addDog(new Dog(id, name, breed, location, now));
            } else if (choice == 2) {
                System.out.print("Enter Dog ID to remove: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                dogManager.removeDog(id);
            } else if (choice == 3) {
                dogManager.printDogs();
            } else if (choice == 4) {
                System.out.print("Enter Dog ID to update location: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Enter New Location: ");
                String location = scanner.nextLine();
                dogManager.editDogLocation(id, location);
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void showVolunteerMenu() {
        while (true) {
            DogBathroom dogBathroom = new DogBathroom();
            ToBathroom takeDogToBathroom = new ToBathroom();
            System.out.println("=== Volunteer Dashboard ===");
            System.out.println("1. View Dogs Needing Walks");
            System.out.println("2. View Dogs Needing To Go To Bathroom");
            System.out.println("3. Search Dog by Name");
            System.out.println("4. Start a Dog Walk");
            System.out.println("5. Take a Dog To Bathroom");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                dogManager.updatePriorityQueue();
                List<Dog> priorityList = dogManager.getPriorityQueue().toList();
                System.out.println("Top 5 Dogs Needing Walks:");
                for (int i = 0; i < Math.min(5, priorityList.size()); i++) {
                    Dog dog = priorityList.get(i);
                    System.out.println("ID: " + dog.id + ", Name: " + dog.name + ", Breed: " + dog.breed +
                            ", Location: " + dog.location + ", Last Walk: " + dog.lastWalk);
                }
            } else if (choice == 2) {
                System.out.print("Top 5 Dogs Needing To Go To Bathroom:");
                dogBathroom.printDogsNeedingBathroom();
            }
            else if (choice == 3) {
                System.out.print("Enter Dog Name: ");
                String name = scanner.nextLine();
                Dog foundDog = dogManager.searchDogByName(name);
                if (foundDog != null) {
                    System.out.println("Dog Found: " + foundDog.name + " ID: " + foundDog.id + " (" + foundDog.breed + ")");
                } else {
                    System.out.println("Dog not found.");
                }
            } else if (choice == 4) {
                System.out.print("Enter Dog ID (from the list): ");
                int dogId = scanner.nextInt();
                scanner.nextLine();
                walkManager.startDogWalk(dogId);
            } else if (choice == 5) {
                takeDogToBathroom.takeDogOut();
            }
            else if (choice == 6) {
                System.out.println("Logged out.");
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
class ToBathroom {
    private DogBathroom dogBathroom;
    private Scanner scanner;

    public ToBathroom() {
        dogBathroom = new DogBathroom();
        scanner = new Scanner(System.in);
    }

    public void takeDogOut() {
        dogBathroom.printDogsNeedingBathroom();
        System.out.print("\nEnter the Dog ID you'd like to take out to the bathroom: ");
        int dogId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Taking Dog ID " + dogId + " out to the bathroom...");

        try {
            Thread.sleep(3000); // Simulate short walk
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        System.out.print("Optional notes: ");
        String notes = scanner.nextLine();

        WalkLog log = new WalkLog("bathroom-run", dogId, LocalDateTime.now().minusMinutes(5), LocalDateTime.now(), bathroom, notes);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("walk_logs.txt", true))) {
            writer.write(log.volunteerName + "," + log.dogId + "," + log.startTime + "," + log.endTime + "," + log.bathroom + "," + log.notes);
            writer.newLine();
            System.out.println("Walk log successfully recorded.");
        } catch (IOException e) {
            System.err.println("Failed to write walk log.");
        }
    }
}

class DogBathroom {
    private LinkedQueue<WalkLog> logQueue;

    public DogBathroom() {
        logQueue = new LinkedQueue<>();
        loadLogs();
    }
    public LinkedQueue<WalkLog> getLogQueue() {
        return logQueue;
    }

    private void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader("walk_logs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length == 6) {
                    try {
                        String volunteer = parts[0];
                        int dogId = Integer.parseInt(parts[1]);
                        LocalDateTime start = LocalDateTime.parse(parts[2]);
                        LocalDateTime end = LocalDateTime.parse(parts[3]);
                        String bathroom = parts[4];
                        String notes = parts[5];
                        logQueue.enqueue(new WalkLog(volunteer, dogId, start, end, bathroom, notes));
                    } catch (Exception e) {
                        System.err.println("Skipping malformed walk log: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading walk logs: " + e.getMessage());
        }
    }

    public void printDogsNeedingBathroom() {
        LinkedQueue<WalkLog> copy = new LinkedQueue<>();
        List<Integer> dogIds = new ArrayList<>();
        List<LocalDateTime> lastTimes = new ArrayList<>();

        while (!copy.isEmpty()) {
            WalkLog log = copy.dequeue();
            if (log.bathroom != null && (log.bathroom.contains("pee") || log.bathroom.contains("poop"))) {
                int index = dogIds.indexOf(log.dogId);
                if (index == -1) {
                    dogIds.add(log.dogId);
                    lastTimes.add(log.endTime);
                } else if (log.endTime.isAfter(lastTimes.get(index))) {
                    lastTimes.set(index, log.endTime);
                }
            }
        }

        for (int i = 0; i < lastTimes.size() - 1; i++) {
            for (int j = i + 1; j < lastTimes.size(); j++) {
                if (lastTimes.get(i).isAfter(lastTimes.get(j))) {
                    LocalDateTime tmpTime = lastTimes.get(i);
                    lastTimes.set(i, lastTimes.get(j));
                    lastTimes.set(j, tmpTime);

                    int tmpId = dogIds.get(i);
                    dogIds.set(i, dogIds.get(j));
                    dogIds.set(j, tmpId);
                }
            }
        }

        System.out.println("\nTop 5 Dogs That Haven't Gone to Bathroom Recently:");
        for (int i = 0; i < Math.min(5, dogIds.size()); i++) {
            System.out.println("Dog ID: " + dogIds.get(i) + " - Last Bathroom: " + lastTimes.get(i));
        }
    }
}


class VolunteerManager {
    private static final String VOLUNTEER_FILE = "src/volunteers.txt";
    private List<Volunteer> volunteers;

    public VolunteerManager() {
        volunteers = new LinkedList<>();
        loadVolunteers();
    }

    private void loadVolunteers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(VOLUNTEER_FILE))) {
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

    public boolean authenticate(String name, String password) {
        for (Volunteer v : volunteers) {
            if (v.name.equals(name) && v.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public void addVolunteer(String name, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VOLUNTEER_FILE, true))) {
            writer.write(name + "," + password);
            writer.newLine();
            volunteers.add(new Volunteer(name, password));
            System.out.println("Volunteer added successfully.");
        } catch (IOException e) {
            System.err.println("Error adding volunteer: " + e.getMessage());
        }
    }

    public void removeVolunteer(String name) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(VOLUNTEER_FILE));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(VOLUNTEER_FILE))) {
                for (String line : lines) {
                    if (!line.startsWith(name + ",")) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            volunteers.removeIf(v -> v.name.equals(name));
            System.out.println("Volunteer removed successfully.");
        } catch (IOException e) {
            System.err.println("Error removing volunteer: " + e.getMessage());
        }
    }

    public void printVolunteers() {
        System.out.println("\n=== All Volunteers in the System ===");
        if (volunteers.isEmpty()) {
            System.out.println("No volunteers available.");
            return;
        }
        for (Volunteer v : volunteers) {
            System.out.println("Name: " + v.name + ", Password: " + v.password);
        }
    }
}

// --- DOG MANAGER ---
class DogManager {
    private static final String DOG_FILE = "src/dogs.txt";
    private List<Dog> dogs;
    private LinkedQueue<Dog> dogPriorityQueue;

    public DogManager() {
        dogs = new LinkedList<>();
        dogPriorityQueue = new LinkedQueue<>();
        loadDogs();
        updatePriorityQueue();
    }

    private void loadDogs() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try (BufferedReader reader = new BufferedReader(new FileReader(DOG_FILE))) {
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

    public void updatePriorityQueue() {
        List<Dog> sortedDogs = new ArrayList<>(dogs);
        sortedDogs.sort(Comparator.comparing(d -> d.lastWalk));
        dogPriorityQueue.clear();
        for (Dog dog : sortedDogs) {
            dogPriorityQueue.enqueue(dog);
        }
    }

    public void printDogs() {
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

    public void addDog(Dog newDog) {
        for (Dog dog : dogs) {
            if (dog.id == newDog.id) {
                System.out.println("A dog with this ID already exists. Please use a unique ID.");
                return;
            }
        }

        dogs.add(newDog);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE, true))) {
            writer.newLine();
            writer.write(newDog.toFileFormat());
            System.out.println("Dog added successfully.");
        } catch (IOException e) {
            System.err.println("Error adding dog to file: " + e.getMessage());
        }
    }

    public void removeDog(int id) {
        dogs.removeIf(d -> d.id == id);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE))) {
            for (Dog d : dogs) {
                writer.write(d.toFileFormat());
                writer.newLine();
            }
            System.out.println("Dog removed successfully.");
        } catch (IOException e) {
            System.err.println("Error removing dog.");
        }
    }

    public void editDogLocation(int id, String newLocation) {
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

    public Dog searchDogByName(String name) {
        return searchDogByNameRecursive(name, 0);
    }

    private Dog searchDogByNameRecursive(String name, int index) {
        if (index >= dogs.size()) return null;

        Dog current = dogs.get(index);
        if (current.name.equalsIgnoreCase(name)) {
            return current;
        }

        return searchDogByNameRecursive(name, index + 1);
    }

    public void updateDogLastWalk(int dogId, LocalDateTime newTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            List<String> lines = Files.readAllLines(Paths.get(DOG_FILE));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOG_FILE))) {
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 5 && Integer.parseInt(parts[0]) == dogId) {
                        writer.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newTime.format(formatter));
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
            }

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

    public LinkedQueue<Dog> getPriorityQueue() {
        return dogPriorityQueue;
    }
}





class DogWalkingGUI {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private DogManager dogManager;
    private VolunteerManager volunteerManager;
    private WalkManager walkManager;

    public DogWalkingGUI(DogManager dm, VolunteerManager vm, WalkManager wm) {
        this.dogManager = dm;
        this.volunteerManager = vm;
        this.walkManager = wm;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Dog Walking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createStartupPanel(), "Startup");
        mainPanel.add(createVolunteerLoginPanel(), "VolunteerLogin");
        mainPanel.add(createEmployeeLoginPanel(), "EmployeeLogin");
        mainPanel.add(createVolunteerDashboard(), "VolunteerDashboard");
        mainPanel.add(createVolunteerCoordinatorPanel(), "VolunteerCoordinator");
        mainPanel.add(createAnimalCoordinatorPanel(), "AnimalCoordinator");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createStartupPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JButton volunteerButton = new JButton("Login as Volunteer");
        JButton employeeButton = new JButton("Login as Employee");

        volunteerButton.addActionListener(e -> cardLayout.show(mainPanel, "VolunteerLogin"));
        employeeButton.addActionListener(e -> cardLayout.show(mainPanel, "EmployeeLogin"));

        panel.add(volunteerButton);
        panel.add(employeeButton);
        return panel;
    }

    private JPanel createVolunteerLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        JTextField nameField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        panel.add(new JLabel("Volunteer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginButton);
        panel.add(backButton);

        loginButton.addActionListener(e -> {
            String name = nameField.getText();
            String pass = new String(passField.getPassword());
            if (volunteerManager.authenticate(name, pass)) {
                walkManager.setLoggedInVolunteer(name);
                cardLayout.show(mainPanel, "VolunteerDashboard");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Startup"));
        return panel;
    }

    private JPanel createEmployeeLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        panel.add(new JLabel("Employee Password:"));
        panel.add(passField);
        panel.add(loginButton);
        panel.add(backButton);

        loginButton.addActionListener(e -> {
            String password = new String(passField.getPassword());
            if ("vc4321".equals(password)) {
                cardLayout.show(mainPanel, "VolunteerCoordinator");
            } else if ("ac9876".equals(password)) {
                cardLayout.show(mainPanel, "AnimalCoordinator");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid employee password.");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Startup"));
        return panel;
    }

    private JPanel createVolunteerDashboard() {
        JPanel panel = new JPanel(new GridLayout(7, 1));
        JButton viewDogsButton = new JButton("View Dogs Needing Walks");
        JButton viewBathroomDogsButton = new JButton("View Dogs Needing Bathroom");
        JButton searchDogButton = new JButton("Search Dog by Name");
        JButton startWalkButton = new JButton("Start a Dog Walk");
        JButton takeBathroomButton = new JButton("Take Dog to Bathroom");
        JButton logoutButton = new JButton("Logout");

        viewDogsButton.addActionListener(e -> {
            dogManager.updatePriorityQueue();
            List<Dog> topDogs = dogManager.getPriorityQueue().toList();
            StringBuilder sb = new StringBuilder("Top 5 Dogs Needing Walks:\n");
            for (int i = 0; i < Math.min(5, topDogs.size()); i++) {
                Dog dog = topDogs.get(i);
                sb.append("ID: ").append(dog.id).append(", Name: ").append(dog.name).append(", Location: ").append(dog.location).append(", Last Walk: ").append(dog.lastWalk).append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        viewBathroomDogsButton.addActionListener(e -> {
            DogBathroom bathroom = new DogBathroom();
            LinkedQueue<WalkLog> copy = bathroom.getLogQueue();
            ArrayList<Integer> dogIds = new ArrayList<>();
            ArrayList<java.time.LocalDateTime> lastTimes = new ArrayList<>();

            while (!copy.isEmpty()) {
                WalkLog log = copy.dequeue();
                if (log.bathroom != null && (log.bathroom.contains("pee") || log.bathroom.contains("poop"))) {
                    int index = dogIds.indexOf(log.dogId);
                    if (index == -1) {
                        dogIds.add(log.dogId);
                        lastTimes.add(log.endTime);
                    } else if (log.endTime.isAfter(lastTimes.get(index))) {
                        lastTimes.set(index, log.endTime);
                    }
                }
            }

            for (int i = 0; i < lastTimes.size() - 1; i++) {
                for (int j = i + 1; j < lastTimes.size(); j++) {
                    if (lastTimes.get(i).isAfter(lastTimes.get(j))) {
                        java.time.LocalDateTime tmpTime = lastTimes.get(i);
                        lastTimes.set(i, lastTimes.get(j));
                        lastTimes.set(j, tmpTime);
                        int tmpId = dogIds.get(i);
                        dogIds.set(i, dogIds.get(j));
                        dogIds.set(j, tmpId);
                    }
                }
            }

            StringBuilder sb = new StringBuilder("Top 5 Dogs That Haven't Gone to Bathroom Recently:\n");
            for (int i = 0; i < Math.min(5, dogIds.size()); i++) {
                sb.append("Dog ID: ").append(dogIds.get(i)).append(" - Last Bathroom: ").append(lastTimes.get(i)).append("\n");
            }

            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        searchDogButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter Dog Name to Search:");
            if (name != null) {
                Dog dog = dogManager.searchDogByName(name);
                if (dog != null) {
                    JOptionPane.showMessageDialog(frame, "Dog Found: ID " + dog.id + ", Name: " + dog.name);
                } else {
                    JOptionPane.showMessageDialog(frame, "Dog not found.");
                }
            }
        });

        startWalkButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Dog ID to walk:");
            if (input != null) {
                try {
                    int dogId = Integer.parseInt(input);
                    LocalDateTime startTime = LocalDateTime.now();
                    JOptionPane.showMessageDialog(frame, "Started walking Dog ID " + dogId + "... Simulating 5 seconds");
                    Thread.sleep(5000);

                    String[] options = {"Yes", "No"};
                    int peed = JOptionPane.showOptionDialog(frame, "Did the dog pee?", "Bathroom Check",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    int pooped = JOptionPane.showOptionDialog(frame, "Did the dog poop?", "Bathroom Check",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    String bathroom;
                    if (peed == 0 && pooped == 0) {
                        bathroom = "pee and poop";
                    } else if (peed == 0) {
                        bathroom = "pee";
                    } else if (pooped == 0) {
                        bathroom = "poop";
                    } else {
                        bathroom = "none";
                    }

                    String notes = JOptionPane.showInputDialog("Optional Notes:");
                    LocalDateTime endTime = LocalDateTime.now();

                    WalkLog walkLog = new WalkLog(walkManager.getLoggedInVolunteer(), dogId, startTime, endTime, bathroom, notes);
                    walkManager.getRecentWalks().push(walkLog);
                    walkManager.logWalk(walkLog);
                    dogManager.updateDogLastWalk(dogId, endTime);

                    JOptionPane.showMessageDialog(frame, "Walk complete and logged for Dog ID " + dogId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Dog ID or error during walk.");
                }
            }
        });

        takeBathroomButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter Dog ID to take to bathroom:");
            if (input != null) {
                try {
                    int dogId = Integer.parseInt(input);
                    JOptionPane.showMessageDialog(frame, "Taking Dog ID " + dogId + " to bathroom... Simulating 3 seconds");
                    Thread.sleep(3000);

                    String[] options = {"Yes", "No"};
                    int peed = JOptionPane.showOptionDialog(frame, "Did the dog pee?", "Bathroom Check",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    int pooped = JOptionPane.showOptionDialog(frame, "Did the dog poop?", "Bathroom Check",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                    String bathroom;
                    if (peed == 0 && pooped == 0) {
                        bathroom = "pee and poop";
                    } else if (peed == 0) {
                        bathroom = "pee";
                    } else if (pooped == 0) {
                        bathroom = "poop";
                    } else {
                        bathroom = "none";
                    }

                    String notes = JOptionPane.showInputDialog("Optional Notes:");
                    LocalDateTime endTime = LocalDateTime.now();
                    LocalDateTime startTime = endTime.minusMinutes(5);

                    WalkLog log = new WalkLog("bathroom-run", dogId, startTime, endTime, bathroom, notes);
                    walkManager.getRecentWalks().push(log);
                    walkManager.logWalk(log);

                    JOptionPane.showMessageDialog(frame, "Bathroom break logged for Dog ID " + dogId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid Dog ID or error during bathroom break.");
                }
            }
        });

        logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "Startup"));

        panel.add(viewDogsButton);
        panel.add(viewBathroomDogsButton);
        panel.add(searchDogButton);
        panel.add(startWalkButton);
        panel.add(takeBathroomButton);
        panel.add(logoutButton);
        return panel;
    }

    private JPanel createVolunteerCoordinatorPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JButton addVolunteer = new JButton("Add Volunteer");
        JButton removeVolunteer = new JButton("Remove Volunteer");
        JButton printVolunteers = new JButton("Print Volunteers");
        JButton back = new JButton("Back");

        addVolunteer.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter Volunteer Name:");
            String pass = JOptionPane.showInputDialog("Enter Password:");
            volunteerManager.addVolunteer(name, pass);
        });

        removeVolunteer.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter Volunteer Name to Remove:");
            volunteerManager.removeVolunteer(name);
        });

        printVolunteers.addActionListener(e -> volunteerManager.printVolunteers());
        back.addActionListener(e -> cardLayout.show(mainPanel, "Startup"));

        panel.add(addVolunteer);
        panel.add(removeVolunteer);
        panel.add(printVolunteers);
        panel.add(back);
        return panel;
    }

    private JPanel createAnimalCoordinatorPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));
        JButton addDog = new JButton("Add Dog");
        JButton removeDog = new JButton("Remove Dog");
        JButton searchDogButton = new JButton("Search Dog by Name");
        JButton printDogs = new JButton("Print Dogs");
        JButton editDog = new JButton("Edit Dog Location");
        JButton back = new JButton("Back");

        addDog.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Dog ID:"));
                String name = JOptionPane.showInputDialog("Enter Dog Name:");
                String breed = JOptionPane.showInputDialog("Enter Breed:");
                String location = JOptionPane.showInputDialog("Enter Location:");
                LocalDateTime now = LocalDateTime.now();
                dogManager.addDog(new Dog(id, name, breed, location, now));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        });

        removeDog.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Dog ID to Remove:"));
            dogManager.removeDog(id);
        });

        searchDogButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter Dog Name to Search:");
            if (name != null) {
                Dog dog = dogManager.searchDogByName(name);
                if (dog != null) {
                    JOptionPane.showMessageDialog(frame, "Dog Found: ID " + dog.id + ", Name: " + dog.name);
                } else {
                    JOptionPane.showMessageDialog(frame, "Dog not found.");
                }
            }
        });
        printDogs.addActionListener(e -> dogManager.printDogs());

        editDog.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Dog ID to Edit:"));
            String location = JOptionPane.showInputDialog("Enter New Location:");
            dogManager.editDogLocation(id, location);
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "Startup"));

        panel.add(addDog);
        panel.add(removeDog);
        panel.add(searchDogButton);
        panel.add(printDogs);
        panel.add(editDog);
        panel.add(back);
        return panel;
    }
}


