import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

public class ClientsPanel extends JPanel{
    private final JTextField nameTextField;
    private final JTextField emailTextField;
    private final JTextField phoneTextField;

    public static final String[] columns = { "ID", "Name", "Email", "Phone"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public ClientsPanel(Database db) {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("+ Add Client");
        JButton updateButton = new JButton("Update Selected Client");
        JButton clearButton = new JButton("Clear Fields");
        JButton deleteButton = new JButton("- Delete Selected Client");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel textPanel = new JPanel();
        nameTextField = new JTextField(10);
        emailTextField = new JTextField(10);
        phoneTextField = new JTextField(10);
        textPanel.add(new JLabel("Name: "));
        textPanel.add(nameTextField);
        textPanel.add(new JLabel("Email: "));
        textPanel.add(emailTextField);
        textPanel.add(new JLabel("Phone: "));
        textPanel.add(phoneTextField);
        textPanel.add(clearButton);

        add(textPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateTable(db);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int rs = db.executeUpdate(
                            String.format("INSERT INTO clients (name, email, phone) VALUES('%s', '%s', '%s')",
                                    nameTextField.getText(), emailTextField.getText(), phoneTextField.getText())
                    );
                    System.out.println(rs);
                    updateTable(db);
                    JOptionPane.showMessageDialog(null, "Klienti u shtua me sukses");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow == -1){
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje klient");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try {
                        int rs = db.executeUpdate(
                                String.format("UPDATE clients SET name = '%s', email = '%s', phone = '%s' WHERE id = %s",
                                        nameTextField.getText(), emailTextField.getText(), phoneTextField.getText(), id)
                        );
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Klienti u perditesua me sukses");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow == -1){
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje klient");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try {
                        int rs = db.executeUpdate(
                                String.format("DELETE FROM clients WHERE id = %s", id)
                        );
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Klienti u fshi me sukses");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameTextField.setText("");
                emailTextField.setText("");
                phoneTextField.setText("");
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                int selectedRow = lsm.getMinSelectionIndex();
                if(selectedRow > -1){
                    nameTextField.setText(model.getValueAt(selectedRow, 1).toString());
                    emailTextField.setText(model.getValueAt(selectedRow, 2).toString());
                    phoneTextField.setText(model.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    }

    private void updateTable(Database db){
        model.setRowCount(0);
        try {
            ResultSet rs = db.executeQuery("SELECT * from clients");
            while(rs.next()){
                model.addRow(
                        new Object[]{
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("phone")
                        }
                );
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}