import java.util.*;

public class Comm {
    Constant con;
    List<String> args;
    public Comm(Constant c, List<String> args) {
        con = c;
        this.args = args;
    }

    public String doTheThing() {
        switch (con) {
            case ECHO:
                return String.join(" ", args);
            case WC:
                return "TODO WC";
            case CAT:
                return "TODO CAT";
            default:
                return "Unimplemented";
        }
    }
    public String toString() {
        return doTheThing();
    }
}
