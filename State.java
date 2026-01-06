import java.util.*;

public class State {
    public Map<String, String> fileSystem;
    public boolean error;
    private static State monolith;
    public static State getState() {
        if(monolith == null) {
            new State();
        }
        return monolith;
    }
    private State() {
        fileSystem = new HashMap<>();
        error = false;
        monolith = this;
    }
}
