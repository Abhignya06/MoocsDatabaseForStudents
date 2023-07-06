import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Retreive {
    private JLabel studentIdLabel;
    private JTextField studentIdField;
    private JButton retrieveButton;

    public Retreive() {
        // Set frame properties
        JFrame frame = new JFrame("MOOCs Database Front End");
       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Initialize components
        studentIdLabel = new JLabel("Student ID:");
        studentIdField = new JTextField(10);
        retrieveButton = new JButton("Retrieve Grade");

        // Add components to the frame
        frame.add(studentIdLabel);
        frame.add(studentIdField);
        frame.add(retrieveButton);

        // Add action listener to the retrieveButton
        retrieveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int studentId = Integer.parseInt(studentIdField.getText());
                retrieveGradeAndCertificateCount(studentId);
            }
        });

        // Set frame properties
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    private void retrieveGradeAndCertificateCount(int studentId) {
        // Database connection parameters
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "abhignya";
        String password = "abhignya";

        // SQL query to retrieve the grade with the highest score and count of different certificates
        String query = "SELECT g.gid, g.eid, g.certificatename, g.score, COUNT(*) AS certificate_count " +
                "FROM grade g " +
                "JOIN enrollement e ON g.eid = e.eid " +
                "WHERE e.sid = ? " +
                "GROUP BY g.gid, g.eid, g.certificatename, g.score " +
                "HAVING g.score = (SELECT MAX(score) FROM grade WHERE eid = g.eid) " +
                "ORDER BY g.score DESC";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the student ID as a parameter in the query
            statement.setInt(1, studentId);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            StringBuilder resultBuilder = new StringBuilder();
            boolean foundGrade = false;

            while (resultSet.next()) {
                int gradeId = resultSet.getInt("gid");
                int enrollmentId = resultSet.getInt("eid");
                String certificateName = resultSet.getString("certificatename");
                int score = resultSet.getInt("score");
                int certificateCount = resultSet.getInt("certificate_count");

                resultBuilder.append("Grade ID: ").append(gradeId).append("\n");
                resultBuilder.append("Enrollment ID: ").append(enrollmentId).append("\n");
                resultBuilder.append("Certificate Name: ").append(certificateName).append("\n");
                resultBuilder.append("Score: ").append(score).append("\n");
                resultBuilder.append("Certificate Count: ").append(certificateCount).append("\n\n");

                foundGrade = true;
            }

            if (foundGrade) {
                JOptionPane.showMessageDialog(null, resultBuilder.toString(), "Grade and Certificate Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No grade found for student ID: " + studentId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Retreive();
            }
        });
    }
}
