import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoocsSemesterGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSemId, txtSemName, txtStartDate, txtEndDate;
    private JTable tblSemesters;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public MoocsSemesterGUI() {
        initializeUI();
        connectToDatabase();
        displaySemesters();
    }

    private void initializeUI() {
        txtSemId = new JTextField();
        txtSemName = new JTextField();
        txtStartDate = new JTextField();
        txtEndDate = new JTextField();

        tblSemesters = new JTable();
        tblSemesters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSemesters.getSelectionModel().addListSelectionListener(e -> selectSemester());

        JScrollPane scrollPane = new JScrollPane(tblSemesters);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Semester ID
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Semester ID:"), constraints);

        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtSemId, constraints);

        // Semester Name
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Semester Name:"), constraints);

        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtSemName, constraints);

        // Start Date
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Start Date:"), constraints);

        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtStartDate, constraints);

        // End Date
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("End Date:"), constraints);

        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtEndDate, constraints);

        // Add button
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(btnAdd, constraints);

        // Modify button
        constraints.gridx = 2;
        panel.add(btnModify, constraints);

        // Delete button
        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(btnDelete, constraints);

        // Display button
        constraints.gridx = 2;
        panel.add(btnDisplay, constraints);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertSemester());

        btnModify.addActionListener(e -> modifySemester());

        btnDelete.addActionListener(e -> deleteSemester());

        btnDisplay.addActionListener(e -> displaySemesters());

        setTitle("Moocs Semester App");
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

    private void insertSemester() {
        String semid = txtSemId.getText();
        String semname = txtSemName.getText();
        String startDate = txtStartDate.getText();
        String endDate = txtEndDate.getText();

        try {
            String query = "INSERT INTO semester (semid, semname, startdate, enddate) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, semid);
            statement.setString(2, semname);
            statement.setString(3, startDate);
            statement.setString(4, endDate);
            statement.executeUpdate();

            clearFields();
            displaySemesters();
            JOptionPane.showMessageDialog(this, "Semester inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifySemester() {
        int selectedRow = tblSemesters.getSelectedRow();
        if (selectedRow >= 0) {
            String semid = txtSemId.getText();
            String semname = txtSemName.getText();
            String startDate = txtStartDate.getText();
            String endDate = txtEndDate.getText();

            try {
                String query = "UPDATE semester SET semname=?, startdate=?, enddate=? WHERE semid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, semname);
                statement.setString(2, startDate);
                statement.setString(3, endDate);
                statement.setString(4, semid);
                statement.executeUpdate();

                clearFields();
                displaySemesters();
                JOptionPane.showMessageDialog(this, "Semester modified successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a semester to modify.");
        }
    }

    private void deleteSemester() {
        int selectedRow = tblSemesters.getSelectedRow();
        if (selectedRow >= 0) {
            String semid = tblSemesters.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this semester?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM semester WHERE semid=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, semid);
                    statement.executeUpdate();

                    clearFields();
                    JOptionPane.showMessageDialog(this, "Semester deleted successfully.");
                    displaySemesters();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a semester to delete.");
        }
    }

    private void displaySemesters() {
        try {
            String query = "SELECT * FROM semester";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Semester> semesters = new ArrayList<>();
            while (resultSet.next()) {
                String semid = resultSet.getString("semid");
                String semname = resultSet.getString("semname");
                String startDate = resultSet.getString("startdate");
                String endDate = resultSet.getString("enddate");
                semesters.add(new Semester(semid, semname, startDate, endDate));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Semester ID", "Semester Name", "Start Date", "End Date"});

            for (Semester semester : semesters) {
                model.addRow(new String[]{semester.getSemId(), semester.getSemName(), semester.getStartDate(), semester.getEndDate()});
            }

            tblSemesters.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectSemester() {
        int selectedRow = tblSemesters.getSelectedRow();
        if (selectedRow >= 0) {
            String semid = tblSemesters.getValueAt(selectedRow, 0).toString();
            String semname = tblSemesters.getValueAt(selectedRow, 1).toString();
            String startDate = tblSemesters.getValueAt(selectedRow, 2).toString();
            String endDate = tblSemesters.getValueAt(selectedRow, 3).toString();

            txtSemId.setText(semid);
            txtSemName.setText(semname);
            txtStartDate.setText(startDate);
            txtEndDate.setText(endDate);
        }
    }

    private void clearFields() {
        txtSemId.setText("");
        txtSemName.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoocsSemesterGUI::new);
    }

    private class Semester {
        private String semId;
        private String semName;
        private String startDate;
        private String endDate;

        public Semester(String semId, String semName, String startDate, String endDate) {
            this.semId = semId;
            this.semName = semName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getSemId() {
            return semId;
        }

        public String getSemName() {
            return semName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }
    }
}
