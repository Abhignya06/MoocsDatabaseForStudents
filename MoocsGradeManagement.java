import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoocsGradeManagement extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtGid, txtEid, txtCertificateName, txtScore;
    private JTable tblGrades;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public MoocsGradeManagement() {
        initializeUI();
        connectToDatabase();
        displayGrades();
    }

    private void initializeUI() {
        txtGid = new JTextField(15);
        txtEid = new JTextField(15);
        txtCertificateName = new JTextField(15);
        txtScore = new JTextField(15);

        tblGrades = new JTable();
        tblGrades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrades.getSelectionModel().addListSelectionListener(e -> selectGrade());

        JScrollPane scrollPane = new JScrollPane(tblGrades);

        btnAdd = new JButton("Add");
        btnModify = new JButton("Modify");
        btnDelete = new JButton("Delete");
        btnDisplay = new JButton("Display");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Grade ID:"), constraints);

        constraints.gridx = 1;
        panel.add(txtGid, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Enrollment ID:"), constraints);

        constraints.gridx = 1;
        panel.add(txtEid, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Certificate Name:"), constraints);

        constraints.gridx = 1;
        panel.add(txtCertificateName, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Score:"), constraints);

        constraints.gridx = 1;
        panel.add(txtScore, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(btnAdd, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(btnModify, constraints);

        constraints.gridx = 1;
        panel.add(btnDelete, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(btnDisplay, constraints);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertGrade());

        btnModify.addActionListener(e -> modifyGrade());

        btnDelete.addActionListener(e -> deleteGrade());

        btnDisplay.addActionListener(e -> displayGrades());

        setTitle("Moocs Grade Management");
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

    private void insertGrade() {
        String gid = txtGid.getText();
        String eid = txtEid.getText();
        String certificateName = txtCertificateName.getText();
        String score = txtScore.getText();

        try {
            String query = "INSERT INTO grade (gid, eid, certificatename, score) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, gid);
            statement.setString(2, eid);
            statement.setString(3, certificateName);
            statement.setString(4, score);
            statement.executeUpdate();

            clearFields();
            displayGrades();
            JOptionPane.showMessageDialog(this, "Enrollement inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyGrade() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow >= 0) {
            String gid = txtGid.getText();
            String eid = txtEid.getText();
            String certificateName = txtCertificateName.getText();
            String score = txtScore.getText();

            try {
                String query = "UPDATE grade SET eid=?, certificatename=?, score=? WHERE gid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, eid);
                statement.setString(2, certificateName);
                statement.setString(3, score);
                statement.setString(4, gid);
                statement.executeUpdate();

                clearFields();
                displayGrades();
                JOptionPane.showMessageDialog(this, "Enrollement modified successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a grade to modify.");
        }
    }

    private void deleteGrade() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow >= 0) {
            String gid = tblGrades.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this grade?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM grade WHERE gid=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, gid);
                    statement.executeUpdate();

                    clearFields();
                    displayGrades();
                    JOptionPane.showMessageDialog(this, "Enrollement deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a grade to delete.");
        }
    }

    private void displayGrades() {
        try {
            String query = "SELECT * FROM grade";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Grade> grades = new ArrayList<>();
            while (resultSet.next()) {
                String gid = resultSet.getString("gid");
                String eid = resultSet.getString("eid");
                String certificateName = resultSet.getString("certificatename");
                String score = resultSet.getString("score");
                grades.add(new Grade(gid, eid, certificateName, score));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Grade ID", "Enrollment ID", "Certificate Name", "Score"});

            for (Grade grade : grades) {
                model.addRow(new String[]{grade.getGid(), grade.getEid(), grade.getCertificateName(), grade.getScore()});
            }

            tblGrades.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectGrade() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow >= 0) {
            String gid = tblGrades.getValueAt(selectedRow, 0).toString();
            String eid = tblGrades.getValueAt(selectedRow, 1).toString();
            String certificateName = tblGrades.getValueAt(selectedRow, 2).toString();
            String score = tblGrades.getValueAt(selectedRow, 3).toString();

            txtGid.setText(gid);
            txtEid.setText(eid);
            txtCertificateName.setText(certificateName);
            txtScore.setText(score);
        }
    }

    private void clearFields() {
        txtGid.setText("");
        txtEid.setText("");
        txtCertificateName.setText("");
        txtScore.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MoocsGradeManagement::new);
    }

    private class Grade {
        private String gid;
        private String eid;
        private String certificateName;
        private String score;

        public Grade(String gid, String eid, String certificateName, String score) {
            this.gid = gid;
            this.eid = eid;
            this.certificateName = certificateName;
            this.score = score;
        }

        public String getGid() {
            return gid;
        }

        public String getEid() {
            return eid;
        }

        public String getCertificateName() {
            return certificateName;
        }

        public String getScore() {
            return score;
        }
    }
}
