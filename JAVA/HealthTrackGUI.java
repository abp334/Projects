import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class HealthTrackGUI {
    public static final HashMap<String, String> userCredentials = new HashMap<>();
    static {
        checkUserCredentials();
    }

    public static void main(String[] args) {
        splashScreenGUI();
    }

    public static void showGUI() {
        JFrame frame = new JFrame();
        frame.setTitle("HealthTrack Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        JLabel titleLabel = new JLabel("HealthTrack", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        northPanel.add(titlePanel, BorderLayout.CENTER);
        frame.getContentPane().add(northPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new GridBagLayout());
        frame.add(centerPanel, BorderLayout.CENTER);
        loginComponents(centerPanel);
        frame.setVisible(true);
    }

    public static void splashScreenGUI() {
        JWindow splashWindow = new JWindow();
        splashWindow.setSize(700, 600);
        splashWindow.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        JLabel splashLabel = new JLabel(
                new ImageIcon("/Users/aayushpandya/Desktop/Java/ VS Code Projects/healthtrack/src/Slide1.png"));
        content.add(splashLabel, BorderLayout.CENTER);

        JLabel loadingLabel = new JLabel("Loading HealthTrack...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        content.add(loadingLabel, BorderLayout.SOUTH);

        splashWindow.setContentPane(content);
        splashWindow.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        splashWindow.dispose();
        SwingUtilities.invokeLater(HealthTrackGUI::showGUI);
    }

    public static void loginComponents(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel userLabel = new JLabel("User:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(userLabel, gbc);
        JTextField userText = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(userText, gbc);
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);
        JPasswordField passwordText = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordText, gbc);
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userText.getText();
                String pw = new String(passwordText.getPassword());
                try {
                    if (userCredentials.get(user).equals(pw)) {
                        JOptionPane.showMessageDialog(panel, "Login successful!");
                        MainScreen.showMainScreen(user);
                    } else {
                        JOptionPane.showMessageDialog(panel, "USER NOT FOUND PLEASE REGISTER");
                    }
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(panel, "USER NOT FOUND PLEASE REGISTER");
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    showNewUserDialog(panel);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    public static void showNewUserDialog(JPanel panel) throws IOException {
        File f = new File("/Users/aayushpandya/Desktop/Java/ VS Code Projects/healthtrack/src/abc.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        JDialog dialog = new JDialog((Frame) null, "New User Registration", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel newUserLabel = new JLabel("New User:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(newUserLabel, gbc);

        JTextField newUserText = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.add(newUserText, gbc);

        JLabel newPasswordLabel = new JLabel("New Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        dialog.add(newPasswordLabel, gbc);

        JPasswordField newPasswordText = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dialog.add(newPasswordText, gbc);

        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(registerButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUser = newUserText.getText();
                String newPwd = new String(newPasswordText.getPassword());

                if (!newUser.isEmpty() && !newPwd.isEmpty()) {
                    userCredentials.put(newUser, newPwd);
                    try {
                        bw.write("User: " + newUser);
                        bw.newLine();
                        bw.write("Password: " + newPwd);
                        bw.newLine();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(panel, "Registration successful! You can now log in.");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Username or password cannot be empty.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
        bw.flush();
        bw.close();
    }

    public static void checkUserCredentials() {
        File f = new File("/Users/aayushpandya/Desktop/Java/ VS Code Projects/healthtrack/src/abc.txt");
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String user = null;
            String password = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("User: ")) {
                    user = line.split(": ")[1];
                } else if (line.startsWith("Password: ")) {
                    password = line.split(": ")[1];
                }
                if (user != null && password != null) {
                    userCredentials.put(user, password);
                    user = null;
                    password = null;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MainScreen {
    static String currentUser;

    public static void showMainScreen(String user) {
        currentUser = user;
        JFrame mainframe = new JFrame("HealthTrack Main Screen");
        mainframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainframe.setSize(1000, 800);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainframe.add(mainPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        JButton userManagementButton = new JButton("User Management");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(userManagementButton, gbc);
        JButton healthMetricsButton = new JButton("Health Metrics");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(healthMetricsButton, gbc);
        JButton appointmentsButton = new JButton("Appointments");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(appointmentsButton, gbc);
        JButton heartRateButton = new JButton("Find Highest HeartRate");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(heartRateButton, gbc);
        JButton bPButton = new JButton("Find Highest BloodPressure");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(bPButton, gbc);
        JButton logOutButton = new JButton("LogOut");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(logOutButton, gbc);
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserManagementScreen(currentUser);
            }
        });

        healthMetricsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHealthMetrics(currentUser);
            }
        });

        appointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointments(currentUser);
            }
        });
        heartRateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMaxHeartRate(currentUser);
            }

        });
        bPButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMaxBP(currentUser);
            }

        });
        logOutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });

        mainframe.setVisible(true);
    }

    public static void showUserManagementScreen(String user) {
        JFrame userManagementFrame = new JFrame("User Management");
        userManagementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userManagementFrame.setSize(800, 400);

        JTabbedPane tabbedPane = new JTabbedPane();
        userManagementFrame.add(tabbedPane, BorderLayout.CENTER);

        JPanel metricsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Health Metrics", metricsPanel);

        String[] columnNames = { "User", "Date", "Blood Pressure", "Heart Rate", "Blood Oxygen", "Calories Burned" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable metricsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(metricsTable);
        metricsPanel.add(scrollPane, BorderLayout.CENTER);

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM public.\"HealthMetrics\"where \"user\"= ?\n" + //
                    "ORDER BY id ASC ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getLong("bp"),
                        rs.getLong("heartRate"),
                        rs.getInt("bloodOxygen"),
                        rs.getLong("calories")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel appointmentsPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("Appointments", appointmentsPanel);

        String[] apptColumnNames = { "User", "Date", "Time", "Reason", "Doctor" };
        DefaultTableModel apptTableModel = new DefaultTableModel(apptColumnNames, 0);
        JTable appointmentsTable = new JTable(apptTableModel);
        JScrollPane apptScrollPane = new JScrollPane(appointmentsTable);
        appointmentsPanel.add(apptScrollPane, BorderLayout.CENTER);

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM public.\"Appointments\"where \"user\" = ?\n" + //
                    "ORDER BY id ASC ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                apptTableModel.addRow(new Object[] {
                        rs.getString("user"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getString("reason"),
                        rs.getString("doctor")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        userManagementFrame.setVisible(true);
    }

    public static void showHealthMetrics(String user) {
        JFrame metricsFrame = new JFrame("Health Metrics");
        metricsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        metricsFrame.setSize(800, 400);

        String[] columnNames = { "Date", "Blood Pressure", "Heart Rate", "Blood Oxygen", "Calories Burned" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        JTable metricsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(metricsTable);

        JButton addButton = new JButton("Add Metric");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(0, 2));
                inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
                JTextField dateField = new JTextField();
                inputPanel.add(dateField);

                inputPanel.add(new JLabel("Blood Pressure (mmHg):"));
                JTextField bpField = new JTextField();
                inputPanel.add(bpField);

                inputPanel.add(new JLabel("Heart Rate (bpm):"));
                JTextField hrField = new JTextField();
                inputPanel.add(hrField);

                inputPanel.add(new JLabel("Blood Oxygen (%):"));
                JTextField boField = new JTextField();
                inputPanel.add(boField);

                inputPanel.add(new JLabel("Calories Burned (kcal):"));
                JTextField calField = new JTextField();
                inputPanel.add(calField);

                int result = JOptionPane.showConfirmDialog(metricsFrame, inputPanel, "Add New Metric Entry",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String date = dateField.getText();
                    String bp = bpField.getText();
                    String hr = hrField.getText();
                    String bo = boField.getText();
                    String cal = calField.getText();
                    tableModel.addRow(new Object[] { date, bp, hr, bo, cal });
                    try {
                        DatabaseConnection.insertHealthMetric(date, bp, hr, bo, cal, user);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        metricsFrame.setLayout(new BorderLayout());
        metricsFrame.add(scrollPane, BorderLayout.CENTER);
        metricsFrame.add(addButton, BorderLayout.SOUTH);

        metricsFrame.setVisible(true);
    }

    public static void showAppointments(String user) {
        JFrame appointmentsFrame = new JFrame("Appointments");
        appointmentsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        appointmentsFrame.setSize(800, 400);

        String[] columnNames = { "Date", "Time", "Reason", "Doctor" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        JTable appointmentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);

        JButton addButton = new JButton("Schedule Appointment");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel inputPanel = new JPanel(new GridLayout(0, 2));
                inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
                JTextField dateField = new JTextField();
                inputPanel.add(dateField);

                inputPanel.add(new JLabel("Time (HH:MM):"));
                JTextField timeField = new JTextField();
                inputPanel.add(timeField);

                inputPanel.add(new JLabel("Reason:"));
                JTextField reasonField = new JTextField();
                inputPanel.add(reasonField);

                inputPanel.add(new JLabel("Doctor:"));
                JTextField doctorField = new JTextField();
                inputPanel.add(doctorField);

                int result = JOptionPane.showConfirmDialog(appointmentsFrame, inputPanel,
                        "Schedule New Appointment",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String date = dateField.getText();
                    String time = timeField.getText();
                    String reason = reasonField.getText();
                    String doctor = doctorField.getText();
                    tableModel.addRow(new Object[] { date, time, reason, doctor });
                    try {
                        DatabaseConnection.insertAppointment(date, time, reason, doctor, user);
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        appointmentsFrame.setLayout(new BorderLayout());
        appointmentsFrame.add(scrollPane, BorderLayout.CENTER);
        appointmentsFrame.add(addButton, BorderLayout.SOUTH);

        appointmentsFrame.setVisible(true);
    }

    public static void showMaxBP(String user) {
        BST bst = new BST();
        String sql = "SELECT \"bp\", \"date\" \n" + //
                "FROM public.\"HealthMetrics\" \n" + //
                "WHERE \"user\" = ?";

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long bp = rs.getLong("bp");
                Date date = rs.getDate("date");
                bst.insert(bp, date);
            }

            BST.Node maxNode = bst.max();
            String message = "Highest Blood Pressure: " + maxNode.data + "\nDate: " + maxNode.date;
            JOptionPane.showMessageDialog(null, message, "Highest Blood Pressure", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showMaxHeartRate(String user) {
        BST bst = new BST();
        String sql = "SELECT \"heartRate\", \"date\" \n" + //
                "FROM public.\"HealthMetrics\" \n" + //
                "WHERE \"user\" = ?";

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long heartRate = rs.getLong("heartRate");
                Date date = rs.getDate("date");
                bst.insert(heartRate, date);
            }

            BST.Node maxNode = bst.max();
            String message = "Highest Heart Rate: " + maxNode.data + "\nDate: " + maxNode.date;
            JOptionPane.showMessageDialog(null, message, "Highest Heart Rate", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres";
    private static final String DB_USER = "postgres.gfxpqszteijvpgjeszbm";
    private static final String DB_PASSWORD = "RagingKando@334";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertHealthMetric(String date, String bp, String hr, String bo, String cal, String user)
            throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        Date sqlDate = Date.valueOf(localDate);
        long bp_long = Long.parseLong(bp);
        long hr_long = Long.parseLong(hr);
        int bo_int = Integer.parseInt(bo);
        long cal_long = Long.parseLong(cal);

        String sql = "INSERT INTO\n" + //
                "  public.\"HealthMetrics\" (date, bp, \"heartRate\", \"bloodOxygen\", calories, \"user\")\n" + //
                "VALUES\n" + //
                "  (?,?,?,?,?,?); ";

        try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, sqlDate);
            pstmt.setLong(2, bp_long);
            pstmt.setLong(3, hr_long);
            pstmt.setInt(4, bo_int);
            pstmt.setLong(5, cal_long);
            pstmt.setString(6, user);

            pstmt.executeUpdate();
            System.out.println("Health metric inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting health metric: " + e.getMessage());
            throw e;
        }
    }

    public static void insertAppointment(String date, String time, String reason, String doctor, String user)
            throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        Date sqlDate = Date.valueOf(localDate);
        String sql = "INSERT INTO\n" + //
                "  public.\"Appointments\" (date, time, reason, doctor,\"user\")\n" + //
                "VALUES\n" + //
                "  (?,?,?,?,?); ";

        try (Connection con = getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, sqlDate);
            pstmt.setTime(2, Time.valueOf(time + ":00"));
            pstmt.setString(3, reason);
            pstmt.setString(4, doctor);
            pstmt.setString(5, user);
            pstmt.executeUpdate();
            System.out.println("Appointment scheduled successfully.");
        } catch (SQLException e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
            throw e;
        }
    }
}

class BST {
    public class Node {
        public long data;
        public Date date;
        public Node left, right;

        public Node(long data, Date date) {
            this.data = data;
            this.date = date;
            left = right = null;
        }
    }

    public Node root = null;

    public void insert(long data, Date date) {
        Node n = new Node(data, date);
        if (root == null) {
            root = n;
            return;
        } else {
            Node temp = root;
            while (true) {
                if (temp.left == null && data < temp.data) {
                    temp.left = n;
                    return;
                } else if (temp.right == null && data > temp.data) {
                    temp.right = n;
                    return;
                } else {
                    if (data < temp.data)
                        temp = temp.left;
                    else
                        temp = temp.right;

                }
            }
        }
    }

    public Node max() {
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }

}