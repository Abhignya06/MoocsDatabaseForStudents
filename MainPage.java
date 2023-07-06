import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainPage extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton retrieveMarksButton;

    public MainPage() {
        // Set frame properties
        setTitle("MOOCs Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create label
        JLabel welcomeLabel = new JLabel("Welcome to MOOCs Database");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Create panel for the button
        JPanel buttonPanel = new JPanel();
        retrieveMarksButton = new JButton("Retrieve Marks");
        buttonPanel.add(retrieveMarksButton);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu studentMenu = new JMenu("Student Details");
        JMenu courseMenu = new JMenu("Course Details");
        JMenu enrollmentMenu = new JMenu("Enrollment Details");
        JMenu semesterMenu = new JMenu("Semester Details");
        JMenu gradeMenu = new JMenu("Grade Details");

        // Create menu item for student menu
        JMenuItem viewStudentDetails = new JMenuItem("View Student Details");
        viewStudentDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StudentTableGUI();
            }
        });

        // Create menu item for course menu
        JMenuItem viewCourseDetails = new JMenuItem("View Course Details");
        viewCourseDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CourseTableGUI();
            }
        });

        // Create menu item for enrollment menu
        JMenuItem viewEnrollmentDetails = new JMenuItem("View Enrollment Details");
        viewEnrollmentDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MoocsEnrollmentManagement();
            }
        });

        // Create menu item for semester menu
        JMenuItem viewSemesterDetails = new JMenuItem("View Semester Details");
        viewSemesterDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MoocsSemesterGUI();
            }
        });

        // Create menu item for grade menu
        JMenuItem viewGradeDetails = new JMenuItem("View Grade Details");
        viewGradeDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MoocsGradeManagement();
            }
        });

        // Create menu item for toppers menu
        JMenuItem viewToppersDetails = new JMenuItem("Toppers Details");
        viewToppersDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ToppersList();
            }
        });

        // Add menu items to respective menus
        studentMenu.add(viewStudentDetails);
        courseMenu.add(viewCourseDetails);
        enrollmentMenu.add(viewEnrollmentDetails);
        semesterMenu.add(viewSemesterDetails);
        gradeMenu.add(viewGradeDetails);

        // Add menu item for toppers menu
        gradeMenu.add(viewToppersDetails);

        // Add menus to the menu bar
        menuBar.add(studentMenu);
        menuBar.add(courseMenu);
        menuBar.add(enrollmentMenu);
        menuBar.add(semesterMenu);
        menuBar.add(gradeMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.CENTER);

        // Set button action for "Retrieve Marks"
        retrieveMarksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Retreive();
            }
        });

        // Add window listener to handle maximizing the window
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    System.out.println("Window maximized");
                } else {
                    System.out.println("Window not maximized");
                }
            }
        });

        // Set frame size and visibility
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainPage();
    }
}
