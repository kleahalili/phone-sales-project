import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(Database db) {
        setTitle("Projekt");

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutMenuItem = new JMenuItem("Log out");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        logoutMenuItem.addActionListener(e -> {
            LoginFrame f = new LoginFrame(db);
            f.setVisible(true);
            dispose();
        });
        exitMenuItem.addActionListener(e -> {
            System.exit(0);
        });

        fileMenu.add(logoutMenuItem);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        JMenuItem clientsMenuItem = new JMenuItem("Clients");
        viewMenu.add(clientsMenuItem);

        clientsMenuItem.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane()
                    .add(new ClientsPanel(db));
            setVisible(true);
        });
        menuBar.add(viewMenu);

        JMenuItem productsMenuItem = new JMenuItem("Products");
        viewMenu.add(productsMenuItem);

        productsMenuItem.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane()
                    .add(new ProductsPanel(db));
            setVisible(true);
        });

        JMenuItem billsMenuItem = new JMenuItem("Bills");
        viewMenu.add(billsMenuItem);

        billsMenuItem.addActionListener(e -> {
            getContentPane().removeAll();
            getContentPane()
                    .add(new BillsPanel(db));
            setVisible(true);
        });

        menuBar.add(viewMenu);

        getContentPane()
                .add(new ClientsPanel(db));

        setJMenuBar(menuBar);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
