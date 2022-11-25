public class Main {
    public static void main(String[] args) {
        Database db = new Database();

        LoginFrame f = new LoginFrame(db);
        f.setVisible(true);
    }
}
