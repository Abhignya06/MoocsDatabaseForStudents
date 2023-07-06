import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentTableGUI extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtSid, txtName, txtEmail, txtContact, txtAddress;
    private JTable tblStudents;
    private JButton btnAdd, btnModify, btnDelete, btnDisplay;

    private Connection connection;

    public StudentTableGUI() {
        initializeUI();
        connectToDatabase();
        displayStudents();
    }

    private void initializeUI() {
        txtSid = new JTextField();
        txtName = new JTextField();
        txtEmail = new JTextField();
        txtContact = new JTextField();
        txtAddress = new JTextField();

        tblStudents = new JTable();
        tblStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblStudents.getSelectionModel().addListSelectionListener(e -> selectStudent());

        JScrollPane scrollPane = new JScrollPane(tblStudents);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        panel.add(new JLabel("SID:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        panel.add(txtSid, gbc);
        gbc.gridy++;
        panel.add(txtName, gbc);
        gbc.gridy++;
        panel.add(txtEmail, gbc);
        gbc.gridy++;
        panel.add(txtContact, gbc);
        gbc.gridy++;
        panel.add(txtAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        panel.add(btnAdd, gbc);
        gbc.gridy++;
        panel.add(btnModify, gbc);
        gbc.gridy++;
        panel.add(btnDelete, gbc);
        gbc.gridy++;
        panel.add(btnDisplay, gbc);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> insertStudent());

        btnModify.addActionListener(e -> modifyStudent());

        btnDelete.addActionListener(e -> deleteStudent());

        btnDisplay.addActionListener(e -> displayStudents());

        setTitle("Moocs Student App");
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

    private void insertStudent() {
        String sid = txtSid.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String contact = txtContact.getText();
        String address = txtAddress.getText();

        try {
            String query = "INSERT INTO student (sid, name, email, contact, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, sid);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, contact);
            statement.setString(5, address);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Successfully inserted");
            /*int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Display success message
                JOptionPane optionPane = new JOptionPane("Student inserted successfully.", JOptionPane.INFORMATION_MESSAGE);
                JDialog dialog = optionPane.createDialog(this, "Success");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);

                clearFields();
                displayStudents();
            } else {
                // Display failure message
                JOptionPane optionPane = new JOptionPane("Failed to insert student.", JOptionPane.ERROR_MESSAGE);
                JDialog dialog = optionPane.createDialog(this, "Error");
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);}*/
            clearFields();
            displayStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modifyStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow >= 0) {
            String sid = txtSid.getText();
            String name = txtName.getText();
            String email = txtEmail.getText();
            String contact = txtContact.getText();
            String address = txtAddress.getText();

            try {
                String query = "UPDATE student SET name=?, email=?, contact=?, address=? WHERE sid=?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, contact);
                statement.setString(4, address);
                statement.setString(5, sid);
                statement.executeUpdate();
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Student modified successfully.");
                    clearFields();
                    displayStudents();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to modify student.");
                }
                clearFields();
                displayStudents();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to modify.");
        }
    }

    private void deleteStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow >= 0) {
            String sid = tblStudents.getValueAt(selectedRow, 0).toString();

            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    String query = "DELETE FROM student WHERE sid=?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, sid);
                    statement.executeUpdate();
                   // int rowsAffected = statement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Successfully deleted");
                  /*  if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Student deleted successfully.");
                        clearFields();
                        displayStudents();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete student.");*/
                    clearFields();
                    displayStudents();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
        }
    }

    private void displayStudents() {
        try {
            String query = "SELECT * FROM student";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                String sid = resultSet.getString("sid");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String contact = resultSet.getString("contact");
                String address = resultSet.getString("address");
                students.add(new Student(sid, name, email, contact, address));
            }

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"SID", "Name", "Email", "Contact", "Address"});

            for (Student student : students) {
                model.addRow(new String[]{student.getSid(), student.getName(), student.getEmail(), student.getContact(), student.getAddress()});
            }

            tblStudents.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow >= 0) {
            String sid = tblStudents.getValueAt(selectedRow, 0).toString();
            String name = tblStudents.getValueAt(selectedRow, 1).toString();
            String email = tblStudents.getValueAt(selectedRow, 2).toString();
            String contact = tblStudents.getValueAt(selectedRow, 3).toString();
            String address = tblStudents.getValueAt(selectedRow, 4).toString();

            txtSid.setText(sid);
            txtName.setText(name);
            txtEmail.setText(email);
            txtContact.setText(contact);
            txtAddress.setText(address);
        }
    }

    private void clearFields() {
        txtSid.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtContact.setText("");
        txtAddress.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentTableGUI::new);
    }

    private class Student {
        private String sid;
        private String name;
        private String email;
        private String contact;
        private String address;

        public Student(String sid, String name, String email, String contact, String address) {
            this.sid = sid;
            this.name = name;
            this.email = email;
            this.contact = contact;
            this.address = address;
        }

        public String getSid() {
            return sid;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getContact() {
            return contact;
        }

        public String getAddress() {
            return address;
        }
    }
}
