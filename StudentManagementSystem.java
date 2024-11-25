import java.io.*;
import java.util.*;

// Assignment class to manage individual assignments
class Assignment implements Serializable {
    private String name;
    private double grade;

    public Assignment(String name, double grade) {
        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        if (grade >= 0 && grade <= 100) {
            this.grade = grade;
        } else {
            System.out.println("Grade must be between 0 and 100.");
        }
    }

    @Override
    public String toString() {
        return name + ": " + grade;
    }
}

// Subject class to manage subjects and their assignments
class Subject implements Serializable {
    private String name;
    private List<Assignment> assignments;

    public Subject(String name) {
        this.name = name;
        this.assignments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addAssignment(String assignmentName, double grade) {
        assignments.add(new Assignment(assignmentName, grade));
    }

    public void removeAssignment(String assignmentName) {
        assignments.removeIf(a -> a.getName().equalsIgnoreCase(assignmentName));
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public double calculateAverage() {
        double sum = 0;
        for (Assignment assignment : assignments) {
            sum += assignment.getGrade();
        }
        return assignments.isEmpty() ? 0 : sum / assignments.size();
    }

    @Override
    public String toString() {
        return name + " (Average: " + calculateAverage() + ")";
    }
}

// Student class to manage student data
class Student implements Serializable {
    private String id;
    private String name;
    private List<Subject> subjects;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.subjects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void removeSubject(String subjectName) {
        subjects.removeIf(subject -> subject.getName().equalsIgnoreCase(subjectName));
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public double calculateAverage() {
        double sum = 0;
        for (Subject subject : subjects) {
            sum += subject.calculateAverage();
        }
        return subjects.isEmpty() ? 0 : sum / subjects.size();
    }

    public double getHighestGrade() {
        return subjects.stream()
                .mapToDouble(Subject::calculateAverage)
                .max()
                .orElse(0);
    }

    public double getLowestGrade() {
        return subjects.stream()
                .mapToDouble(Subject::calculateAverage)
                .min()
                .orElse(0);
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Average Grade: " + calculateAverage();
    }
}

// StudentManagementSystem class to handle SMS operations
public class StudentManagementSystem {
    private Map<String, Student> students = new HashMap<>();
    private transient Scanner scanner = new Scanner(System.in); // Scanner is transient for serialization

    public void menu() {
        int choice = 0;
        do {
            System.out.println("\n--- Student Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. Remove Student");
            System.out.println("3. Update Student");
            System.out.println("4. View All Students");
            System.out.println("5. Generate Report");
            System.out.println("6. Save Data");
            System.out.println("7. Load Data");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid choice. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> removeStudent();
                case 3 -> updateStudent();
                case 4 -> viewAllStudents();
                case 5 -> generateReport();
                case 6 -> saveData();
                case 7 -> loadData();
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void addStudent() {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        Student student = new Student(id, name);
        students.put(id, student);
        System.out.println("Student added.");
    }

    private void removeStudent() {
        System.out.print("Enter student ID to remove: ");
        String id = scanner.nextLine();
        if (students.remove(id) != null) {
            System.out.println("Student removed.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private void updateStudent() {
        System.out.print("Enter student ID to update: ");
        String id = scanner.nextLine();
        Student student = students.get(id);
        if (student != null) {
            System.out.print("Enter subject name: ");
            String subjectName = scanner.nextLine();

            Subject subject = student.getSubjects().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(subjectName))
                    .findFirst()
                    .orElse(null);
            if (subject == null) {
                subject = new Subject(subjectName);
                student.addSubject(subject);
            }

            System.out.print("Enter assignment name: ");
            String assignmentName = scanner.nextLine();
            System.out.print("Enter grade for " + assignmentName + ": ");
            if (!scanner.hasNextDouble()) {
                System.out.println("Invalid grade. Please enter a number.");
                scanner.nextLine();
                return;
            }
            double grade = scanner.nextDouble();
            scanner.nextLine();
            subject.addAssignment(assignmentName, grade);
            System.out.println("Assignment added/updated.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students to display.");
        } else {
            for (Student student : students.values()) {
                System.out.println(student);
                for (Subject subject : student.getSubjects()) {
                    System.out.println("  " + subject);
                    for (Assignment assignment : subject.getAssignments()) {
                        System.out.println("    " + assignment);
                    }
                }
            }
        }
    }

    private void generateReport() {
        System.out.println("\n--- Report ---");
        if (students.isEmpty()) {
            System.out.println("No students to report on.");
            return;
        }

        for (Student student : students.values()) {
            System.out.println("\n" + student);
            for (Subject subject : student.getSubjects()) {
                System.out.println("  " + subject);
                for (Assignment assignment : subject.getAssignments()) {
                    System.out.println("    " + assignment);
                }
            }
            System.out.println("Highest Grade: " + student.getHighestGrade());
            System.out.println("Lowest Grade: " + student.getLowestGrade());
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.dat"))) {
            oos.writeObject(students);
            System.out.println("Data saved.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("students.dat"))) {
            students = (Map<String, Student>) ois.readObject();
            System.out.println("Data loaded.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        StudentManagementSystem sms = new StudentManagementSystem();
        sms.menu();
    }
}