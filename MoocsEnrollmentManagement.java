import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class MoocsEnrollmentManagement extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtEnrollId, txtEnrollDate, txtSemesterId, txtCourseId, txtStudentId;
    private JTable tblEnrollments;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public MoocsEnrollmentManagement() {
        initializeUI();
        connectToDatabase();
        displayEnrollments();
    }

    private void initializeUI() {
        txtEnrollId = new JTextField();
        txtEnrollDate = new JTextField();
        txtSemesterId = new JTextField();
        txtCourseId = new JTextField();
        txtStudentId = new JTextField();

        tblEnrollments = new JTable();
        tblEnrollments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEnrollments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selectEnrollment();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblEnrollments);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Enroll ID:"));
        panel.add(txtEnrollId);
        panel.add(new JLabel("Enroll Date:"));
        panel.add(txtEnrollDate);
        panel.add(new JLabel("Semester ID:"));
        panel.add(txtSemesterId);
        panel.add(new JLabel("Course ID:"));
        panel.add(txtCourseId);
        panel.add(new JLabel("Student ID:"));
        panel.add(txtStudentId);
        panel.add(btnAdd);
        panel.add(btnModify);
        panel.add(btnDelete);
        panel.add(btnDisplay);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertEnrollment();
            }
        });

        btnModify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyEnrollment();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEnrollment();
            }
        });

        btnDisplay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayEnrollments();
            }
        });

        setTitle("Moocs Enrollment App");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "abhignya";
        String password = "abhignya";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertEnrollment() {
        String enrollId = txtEnrollId.getText();
        String enrollDate = txtEnrollDate.getText();
        String semesterId = txtSemesterId.getText();
        String courseId = txtCourseId.getText();
        String studentId = txtStudentId.getText();

        try {
            String query = "INSERT INTO enrollement (eid, enrolldate, semid, cid, sid) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, enrollId);
            statement.setString(2, enrollDate);
            statement.setString(3, semesterId);
            statement.setString(4, courseId);
            statement.setString(5, studentId);
            statement.executeUpdate();

            clearFields();
            displayEnrollments();
            JOptionPane.showMessageDialog(this, "Enrollement inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyEnrollment() {
        int selectedRow = tblEnrollments.getSelectedRow();
        if (selectedRow >= 0) {
            String enrollId = txtEnrollId.getText();
            String enrollDate = txtEnrollDate.getText();
            String semesterId = txtSemesterId.getText();
            String courseId = txtCourseId.getText();
            String studentId = txtStudentId.getText();

            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDate = dateFormat.parse(enrollDate);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                String query = "UPDATE enrollement SET enrolldate=?, semid=?, cid=?, sid=? WHERE eid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setDate(1, sqlDate);
                statement.setString(2, semesterId);
                statement.setString(3, courseId);
                statement.setString(4, studentId);
                statement.setString(5, enrollId);
                statement.executeUpdate();

                clearFields();
                displayEnrollments();
                JOptionPane.showMessageDialog(this, "Enrollement modified successfully.");
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void deleteEnrollment() {
        int selectedRow = tblEnrollments.getSelectedRow();
        if (selectedRow >= 0) {
            String enrollId = txtEnrollId.getText();

            try {
                String query = "DELETE FROM enrollement WHERE eid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, enrollId);
                statement.executeUpdate();

                clearFields();
                displayEnrollments();
                JOptionPane.showMessageDialog(this, "Enrollement deleted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectEnrollment() {
        int selectedRow = tblEnrollments.getSelectedRow();
        if (selectedRow >= 0) {
            String enrollId = tblEnrollments.getValueAt(selectedRow, 0).toString();
            String enrollDate = tblEnrollments.getValueAt(selectedRow, 1).toString();
            String semesterId = tblEnrollments.getValueAt(selectedRow, 2).toString();
            String courseId = tblEnrollments.getValueAt(selectedRow, 3).toString();
            String studentId = tblEnrollments.getValueAt(selectedRow, 4).toString();

            txtEnrollId.setText(enrollId);
            txtEnrollDate.setText(enrollDate);
            txtSemesterId.setText(semesterId);
            txtCourseId.setText(courseId);
            txtStudentId.setText(studentId);
        }
    }

    private void displayEnrollments() {
        try {
            String query = "SELECT * FROM enrollement";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Enrollment> enrollments = new ArrayList<>();
            while (resultSet.next()) {
                String enrollId = resultSet.getString("eid");
                String enrollDate = resultSet.getString("enrolldate");
                String semesterId = resultSet.getString("semid");
                String courseId = resultSet.getString("cid");
                String studentId = resultSet.getString("sid");
                enrollments.add(new Enrollment(enrollId, enrollDate, semesterId, courseId, studentId));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Enroll ID");
            model.addColumn("Enroll Date");
            model.addColumn("Semester ID");
            model.addColumn("Course ID");
            model.addColumn("Student ID");

            for (Enrollment enrollment : enrollments) {
                model.addRow(new Object[]{enrollment.getEnrollId(), enrollment.getEnrollDate(), enrollment.getSemesterId(),
                        enrollment.getCourseId(), enrollment.getStudentId()});
            }

            tblEnrollments.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtEnrollId.setText("");
        txtEnrollDate.setText("");
        txtSemesterId.setText("");
        txtCourseId.setText("");
        txtStudentId.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MoocsEnrollmentManagement();
            }
        });
    }

    private class Enrollment {
        private String enrollId;
        private String enrollDate;
        private String semesterId;
        private String courseId;
        private String studentId;

        public Enrollment(String enrollId, String enrollDate, String semesterId, String courseId, String studentId) {
            this.enrollId = enrollId;
            this.enrollDate = enrollDate;
            this.semesterId = semesterId;
            this.courseId = courseId;
            this.studentId = studentId;
        }

        public String getEnrollId() {
            return enrollId;
        }

        public String getEnrollDate() {
            return enrollDate;
        }

        public String getSemesterId() {
            return semesterId;
        }

        public String getCourseId() {
            return courseId;
        }

        public String getStudentId() {
            return studentId;
        }
    }
}
