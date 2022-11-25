import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.xml.crypto.Data;

public class BillsPanel extends JPanel{
    private final JTextField clientIDTextField;
    private final JTextField productIDTextField;
    private final JTextField quantityTextField;
    private int currentID;

    public static final String[] columns = { "ID", "clientID", "productID", "Brand", "Quantity", "Date", "Total"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public BillsPanel(Database db) {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("+ Add Bill");
        JButton updateButton = new JButton("Update Selected Bill");
        JButton clearButton = new JButton("Clear Fields");
        JButton deleteButton = new JButton("- Delete Selected Bill");
        JButton printButton = new JButton("Print Bill");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(printButton);

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel textPanel = new JPanel();
        clientIDTextField = new JTextField(10);
        productIDTextField = new JTextField(10);
        quantityTextField = new JTextField(10);
        textPanel.add(new JLabel("ClientID: "));
        textPanel.add(clientIDTextField);
        textPanel.add(new JLabel("ProductID: "));
        textPanel.add(productIDTextField);
        textPanel.add(new JLabel("Quantity: "));
        textPanel.add(quantityTextField);
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
                            String.format("INSERT INTO bills (clientID, productID, quantity) VALUES(%s, %s, %s)",
                                    clientIDTextField.getText(), productIDTextField.getText(), quantityTextField.getText())
                    );
                    System.out.println(rs);
                    updateTable(db);
                    JOptionPane.showMessageDialog(null, "Fatura u shtua me sukses");
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
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje fature");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try {
                        int rs = db.executeUpdate(
                                String.format("UPDATE bills SET clientID = %s, productID = %s, quantity = %s WHERE id = %s",
                                        clientIDTextField.getText(), productIDTextField.getText(), quantityTextField.getText(), id)
                        );
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Fatura u perditesua me sukses");
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
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje fature");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try {
                        int rs = db.executeUpdate(
                                String.format("DELETE FROM bills WHERE id = %s", id)
                        );
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Fatura u fshi me sukses");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientIDTextField.setText("");
                productIDTextField.setText("");
                quantityTextField.setText("");
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                int selectedRow = lsm.getMinSelectionIndex();
                if(selectedRow > -1){
                    currentID = (int) model.getValueAt(selectedRow, 1);
                    clientIDTextField.setText(model.getValueAt(selectedRow, 1).toString());
                    productIDTextField.setText(model.getValueAt(selectedRow, 2).toString());
                    quantityTextField.setText(model.getValueAt(selectedRow, 3).toString());

                }
            }
        });

        printButton.addActionListener(e -> {
            printBill(db, currentID);
        });
    }

    private void printBill(Database db, int id){
        try{
            ResultSet rs = db.executeQuery(String.format("SELECT * from bills WHERE id = %s", id));
            rs.next();
            FileWriter f = new FileWriter(String.format("bill%s.txt", id));
            f.write("Product ID: " + rs.getInt("productID") + "\n" +
                        "Quantity: " + rs.getInt("quantity") + "\n" +
                        "Price: " + rs.getInt("quantity") * getProductPrice(db, rs.getInt("productID")));
            f.close();
            JOptionPane.showMessageDialog(null, "Fatura u printua");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private double getProductPrice(Database db, int id){
        try{
            ResultSet rs = db.executeQuery(String.format("SELECT * from products WHERE id = %s", id));
            rs.next();
            return rs.getDouble("price");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return 0;
    }

    private String getProductName(Database db, int id){
        try{
            ResultSet rs = db.executeQuery(String.format("SELECT * from products WHERE id = %s", id));
            rs.next();
            return rs.getString("brand");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private void updateTable(Database db){
        model.setRowCount(0);
        try {
            ResultSet rs = db.executeQuery("SELECT * from bills");
            while(rs.next()){
                model.addRow(
                        new Object[]{
                                rs.getInt("id"),
                                rs.getInt("clientID"),
                                rs.getInt("productID"),
                                getProductName(db, rs.getInt("id")),
                                rs.getInt("quantity"),
                                rs.getDate("date"),
                                rs.getInt("quantity") * getProductPrice(db, rs.getInt("productID"))
                        }
                );
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}