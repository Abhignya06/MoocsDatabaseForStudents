import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ToppersList extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable tblToppers;
    private JButton btnMarksToppers, btnCertificateToppers;

    private Connection connection;

    public ToppersList() {
        initializeUI();
        connectToDatabase();
        setTitle("Toppers Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    private void initializeUI() {
        tblToppers = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblToppers);

        btnMarksToppers = new JButton(" Toppers based on marks");
        btnCertificateToppers = new JButton("Certificate_count");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(btnMarksToppers, BorderLayout.WEST);
        panel.add(btnCertificateToppers, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        btnMarksToppers.addActionListener(e -> generateMarksToppersReport());
       btnCertificateToppers.addActionListener(e -> generateCertificateToppersReport());

        setTitle("Toppers List");
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

    private void generateMarksToppersReport() {
        try {
            String query = "SELECT g.gid, g.eid, g.certificatename, g.score " +
                    "FROM (SELECT * FROM grade ORDER BY score DESC) g " +
                    "INNER JOIN enrollement e ON g.eid = e.eid " +
                    "WHERE ROWNUM <= 10";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Grade ID", "Enrollment ID", "Certificate Name", "Score"});

            while (resultSet.next()) {
                String gid = resultSet.getString("gid");
                String eid = resultSet.getString("eid");
                String certificateName = resultSet.getString("certificatename");
                String score = resultSet.getString("score");
                model.addRow(new String[]{gid, eid, certificateName, score});
            }

            tblToppers.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateCertificateToppersReport() {
        try {
            String query = "SELECT * " +
                    "FROM (SELECT g.certificatename, COUNT(*) as certificate_count " +
                    "      FROM grade g " +
                    "      GROUP BY g.certificatename " +
                    "      ORDER BY certificate_count DESC) " +
                    "WHERE ROWNUM <= 10";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Certificate Name", "Certificate Count"});

            while (resultSet.next()) {
                String certificateName = resultSet.getString("certificatename");
                String certificateCount = resultSet.getString("certificate_count");
                model.addRow(new String[]{certificateName, certificateCount});
            }

            tblToppers.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToppersList::new);
    }
}

