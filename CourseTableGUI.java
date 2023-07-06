import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtCid, txtCname, txtCdescription, txtCduration;
    private JTable tblCourses;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public CourseTableGUI() {
        initializeUI();
        connectToDatabase();
        displayCourses();
    }

    private void initializeUI() {
        txtCid = new JTextField();
        txtCid.setColumns(15);
        txtCname = new JTextField();
        txtCname.setColumns(15);
        txtCdescription = new JTextField();
        txtCdescription.setColumns(15);
        txtCduration = new JTextField();
        txtCduration.setColumns(15);

        tblCourses = new JTable();
        tblCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCourses.getSelectionModel().addListSelectionListener(e -> selectCourse());

        JScrollPane scrollPane = new JScrollPane(tblCourses);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel("CID:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCid, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCname, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCdescription, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        panel.add(txtCduration, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(btnAdd, gbc);
        gbc.gridy = 5;
        panel.add(btnModify, gbc);
        gbc.gridy = 6;
        panel.add(btnDelete, gbc);
        gbc.gridy = 7;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertCourse());

        btnModify.addActionListener(e -> modifyCourse());

        btnDelete.addActionListener(e -> deleteCourse());

        btnDisplay.addActionListener(e -> displayCourses());

        setTitle("Moocs Course App");
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

    private void insertCourse() {
        String cid = txtCid.getText();
        String cname = txtCname.getText();
        String cdescription = txtCdescription.getText();
        String cduration = txtCduration.getText();

        try {
            String query = "INSERT INTO course (cid, cname, cdescription, cduration) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cid);
            statement.setString(2, cname);
            statement.setString(3, cdescription);
            statement.setString(4, cduration);
            statement.executeUpdate();

            clearFields();
            displayCourses();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyCourse() {
        int selectedRow = tblCourses.getSelectedRow();
        if (selectedRow >= 0) {
            String cid = txtCid.getText();
            String cname = txtCname.getText();
            String cdescription = txtCdescription.getText();
            String cduration = txtCduration.getText();

            try {
                String query = "UPDATE course SET cname=?, cdescription=?, cduration=? WHERE cid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, cname);
                statement.setString(2, cdescription);
                statement.setString(3, cduration);
                statement.setString(4, cid);
                statement.executeUpdate();

                clearFields();
                displayCourses();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to modify.");
        }
    }

    private void deleteCourse() {
        int selectedRow = tblCourses.getSelectedRow();
        if (selectedRow >= 0) {
            String cid = tblCourses.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this course?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM course WHERE cid=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, cid);
                    statement.executeUpdate();

                    clearFields();
                    displayCourses();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.");
        }
    }

    private void displayCourses() {
        try {
            String query = "SELECT * FROM course";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Course> courses = new ArrayList<>();
            while (resultSet.next()) {
                String cid = resultSet.getString("cid");
                String cname = resultSet.getString("cname");
                String cdescription = resultSet.getString("cdescription");
                String cduration = resultSet.getString("cduration");
                courses.add(new Course(cid, cname, cdescription, cduration));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"CID", "Name", "Description", "Duration"});

            for (Course course : courses) {
                model.addRow(new String[]{course.getCid(), course.getCname(), course.getCdescription(), course.getCduration()});
            }

            tblCourses.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectCourse() {
        int selectedRow = tblCourses.getSelectedRow();
        if (selectedRow >= 0) {
            String cid = tblCourses.getValueAt(selectedRow, 0).toString();
            String cname = tblCourses.getValueAt(selectedRow, 1).toString();
            String cdescription = tblCourses.getValueAt(selectedRow, 2).toString();
            String cduration = tblCourses.getValueAt(selectedRow, 3).toString();

            txtCid.setText(cid);
            txtCname.setText(cname);
            txtCdescription.setText(cdescription);
            txtCduration.setText(cduration);
        }
    }

    private void clearFields() {
        txtCid.setText("");
        txtCname.setText("");
        txtCdescription.setText("");
        txtCduration.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CourseTableGUI::new);
    }

    private class Course {
        private String cid;
        private String cname;
        private String cdescription;
        private String cduration;

        public Course(String cid, String cname, String cdescription, String cduration) {
            this.cid = cid;
            this.cname = cname;
            this.cdescription = cdescription;
            this.cduration = cduration;
        }

        public String getCid() {
            return cid;
        }

        public String getCname() {
            return cname;
        }

        public String getCdescription() {
            return cdescription;
        }

        public String getCduration() {
            return cduration;
        }
    }
}
