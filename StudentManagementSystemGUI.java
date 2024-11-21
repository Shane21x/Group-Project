import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

// GUI-based Student Management System
public class StudentManagementSystemGUI {
    private Map<String, Student> students = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementSystemGUI().createAndShowGUI());
    }

    // Create the main GUI
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(0, 1));

        // Main menu buttons
        JButton addStudentButton = new JButton("Add Student");
        JButton updateStudentButton = new JButton("Update Student");
        JButton viewStudentsButton = new JButton("View Students");
        JButton generateReportButton = new JButton("Generate Report");
        JButton saveDataButton = new JButton("Save Data");
        JButton loadDataButton = new JButton("Load Data");

        frame.add(addStudentButton);
        frame.add(updateStudentButton);
        frame.add(viewStudentsButton);
        frame.add(generateReportButton);
        frame.add(saveDataButton);
        frame.add(loadDataButton);

        // Button actions
        addStudentButton.addActionListener(e -> addStudentDialog());
        updateStudentButton.addActionListener(e -> updateStudentDialog());
        viewStudentsButton.addActionListener(e -> viewStudentsDialog());
        generateReportButton.addActionListener(e -> generateReportDialog());
        saveDataButton.addActionListener(e -> saveData());
        loadDataButton.addActionListener(e -> loadData());

        frame.setVisible(true);
    }

    // Add Student Dialog
    private void addStudentDialog() {
        JFrame dialog = new JFrame("Add Student");
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(0, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        dialog.add(new JLabel("Student ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Student Name:"));
        dialog.add(nameField);

        JButton submitButton = new JButton("Add");
        dialog.add(submitButton);

        submitButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            if (!id.isEmpty() && !name.isEmpty()) {
                if (students.containsKey(id)) {
                    JOptionPane.showMessageDialog(dialog, "Student ID already exists!");
                } else {
                    students.put(id, new Student(id, name));
                    JOptionPane.showMessageDialog(dialog, "Student added!");
                }
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
            }
        });

        dialog.setVisible(true);
    }

    // Update Student Dialog
    private void updateStudentDialog() {
        JFrame dialog = new JFrame("Update Student");
        dialog.setSize(300, 300);
        dialog.setLayout(new GridLayout(0, 2));

        JTextField idField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextField assignmentField = new JTextField();
        JTextField gradeField = new JTextField();

        dialog.add(new JLabel("Student ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Subject Name:"));
        dialog.add(subjectField);
        dialog.add(new JLabel("Assignment Name:"));
        dialog.add(assignmentField);
        dialog.add(new JLabel("Grade:"));
        dialog.add(gradeField);

        JButton submitButton = new JButton("Update");
        dialog.add(submitButton);

        submitButton.addActionListener(e -> {
            String id = idField.getText();
            String subjectName = subjectField.getText();
            String assignmentName = assignmentField.getText();
            String gradeText = gradeField.getText();

            Student student = students.get(id);
            if (student != null) {
                try {
                    double grade = Double.parseDouble(gradeText);
                    Subject subject = student.getSubjects().stream()
                            .filter(s -> s.getName().equalsIgnoreCase(subjectName))
                            .findFirst()
                            .orElse(null);

                    if (subject == null) {
                        subject = new Subject(subjectName);
                        student.addSubject(subject);
                    }

                    subject.addAssignment(assignmentName, grade);
                    JOptionPane.showMessageDialog(dialog, "Assignment added/updated!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid grade. Enter a number.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Student not found.");
            }
        });

        dialog.setVisible(true);
    }

    // View Students Dialog
    private void viewStudentsDialog() {
        JFrame dialog = new JFrame("View Students");
        dialog.setSize(400, 400);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        StringBuilder content = new StringBuilder();
        for (Student student : students.values()) {
            content.append(student).append("\n");
            for (Subject subject : student.getSubjects()) {
                content.append("  ").append(subject).append("\n");
                for (Assignment assignment : subject.getAssignments()) {
                    content.append("    ").append(assignment).append("\n");
                }
            }
        }

        if (content.isEmpty()) {
            textArea.setText("No students available.");
        } else {
            textArea.setText(content.toString());
        }

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    // Generate Report Dialog
    private void generateReportDialog() {
        JFrame dialog = new JFrame("Generate Report");
        dialog.setSize(400, 400);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        StringBuilder report = new StringBuilder();
        for (Student student : students.values()) {
            report.append(student).append("\n");
            for (Subject subject : student.getSubjects()) {
                report.append("  ").append(subject).append("\n");
                for (Assignment assignment : subject.getAssignments()) {
                    report.append("    ").append(assignment).append("\n");
                }
            }
            report.append("Highest Grade: ").append(student.getHighestGrade()).append("\n");
            report.append("Lowest Grade: ").append(student.getLowestGrade()).append("\n\n");
        }

        if (report.isEmpty()) {
            textArea.setText("No data available.");
        } else {
            textArea.setText(report.toString());
        }

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    // Save Data to File
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("students.dat"))) {
            oos.writeObject(students);
            JOptionPane.showMessageDialog(null, "Data saved!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
        }
    }

    // Load Data from File
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("students.dat"))) {
            students = (Map<String, Student>) ois.readObject();
            JOptionPane.showMessageDialog(null, "Data loaded!");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
        }
    }
}