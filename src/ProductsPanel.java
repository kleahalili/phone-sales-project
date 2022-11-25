import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

public class ProductsPanel extends JPanel{
    private final JTextField brandTextField;
    private final JTextField cameraTextField;
    private final JTextField storageTextField;
    private final JTextField priceTextField;

    public static final String[] columns = { "ID", "Brand", "Camera", "Storage", "Price"};
    private final DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public ProductsPanel(Database db) {
        setLayout(new BorderLayout());

        JButton addButton = new JButton("+ Add Product");
        JButton updateButton = new JButton("Update Selected Product");
        JButton clearButton = new JButton("Clear Fields");
        JButton deleteButton = new JButton("- Delete Selected Product");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel textPanel = new JPanel();
        brandTextField = new JTextField(10);
        cameraTextField = new JTextField(10);
        storageTextField = new JTextField(10);
        priceTextField = new JTextField(10);
        textPanel.add(new JLabel("Brand: "));
        textPanel.add(brandTextField);
        textPanel.add(new JLabel("Camera: "));
        textPanel.add(cameraTextField);
        textPanel.add(new JLabel("Storage: "));
        textPanel.add(storageTextField);
        textPanel.add(new JLabel("Price: "));
        textPanel.add(priceTextField);
        textPanel.add(clearButton);

        add(textPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateTable(db);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int camera = Integer.parseInt(cameraTextField.getText());
                    int storage = Integer.parseInt(storageTextField.getText());
                    double price = Float.parseFloat(priceTextField.getText());

                    try {
                        int rs = db.executeUpdate(
                                String.format("INSERT INTO products (brand, camera, storage, price) VALUES('%s', %s, %s, %s)",
                                        brandTextField.getText(), camera, storage, price));
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Produkti u shtua me sukses");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Invalid values in fields");
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Invalid values in fields");
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow == -1){
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje produkt");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try{
                        int camera = Integer.parseInt(cameraTextField.getText());
                        int storage = Integer.parseInt(storageTextField.getText());
                        double price = Float.parseFloat(priceTextField.getText());

                        try {
                            int rs = db.executeUpdate(
                                    String.format("UPDATE products SET brand = '%s', camera = %s, storage = %s, price = %s WHERE id = %s",
                                            brandTextField.getText(), camera, storage, price, id)
                            );
                            System.out.println(rs);
                            updateTable(db);
                            JOptionPane.showMessageDialog(null, "Produkti u perditesua me sukses");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Invalid values in fields");
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Invalid values in fields");
                    }
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if(selectedRow == -1){
                    JOptionPane.showMessageDialog(null, "Nuk keni selektuar asnje produkt");
                }else{
                    int id = (int) model.getValueAt(selectedRow, 0);
                    try {
                        int rs = db.executeUpdate(
                                String.format("DELETE FROM clients WHERE id = %s", id)
                        );
                        System.out.println(rs);
                        updateTable(db);
                        JOptionPane.showMessageDialog(null, "Produkti u fshi me sukses");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brandTextField.setText("");
                cameraTextField.setText("");
                storageTextField.setText("");
                priceTextField.setText("");
            }
        });

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                int selectedRow = lsm.getMinSelectionIndex();
                if(selectedRow > -1){
                    brandTextField.setText(model.getValueAt(selectedRow, 1).toString());
                    cameraTextField.setText(model.getValueAt(selectedRow, 2).toString());
                    storageTextField.setText(model.getValueAt(selectedRow, 3).toString());
                    priceTextField.setText(model.getValueAt(selectedRow, 4).toString());
                }
            }
        });
    }

    private void updateTable(Database db){
        model.setRowCount(0);
        try {
            ResultSet rs = db.executeQuery("SELECT * FROM products");
            while(rs.next()){
                model.addRow(
                        new Object[]{
                                rs.getInt("id"),
                                rs.getString("brand"),
                                rs.getString("camera"),
                                rs.getString("storage"),
                                rs.getString("price")
                        }
                );
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}