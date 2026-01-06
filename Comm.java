import java.util.*;

public class Comm {
    Constant con;
    List<String> args;
    Integer mode;
    String filename;
    String input;
    public Comm(Constant c, List<String> args) {
        con = c;
        this.args = args;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public void setFile(String file) {
        filename = file;
    }
    public void setInput(String input) {
        this.input = input;
    }
    private void prepareArgs() {
        args.replaceAll(s -> s.equals("$?") ? (State.getState().error ? "1" : "0") : s);
    }

    private String internal_doTheThing() {
        String s = "";
        prepareArgs();
        Map<String, String> fs = State.getState().fileSystem;
        switch (con) {
            case ECHO:
                State.getState().error = false;
                return String.join(" ", args) + "\n";
            case WC:
                if(args.size() > 0) {
                    for(String file : args) {
                        String content = fs.get(file);
                        if(content == null) {
                            System.err.println("No such file: " + file);
                            State.getState().error = true;
                        } else {
                            s += "" + content.chars().filter(ch -> ch == '\n').count() + "\n";
                            State.getState().error = false;
                        }
                    }
                } else if (input != null) {
                    s += "" + input.chars().filter(ch -> ch == '\n').count() + "\n";
                    State.getState().error = false;
                }
                return s;
            case CAT:
                for(String file : args) {
                    String content = fs.get(file);
                    if(content == null) {
                        System.err.println("No such file: " + file);
                        State.getState().error = true;
                    } else {
                        s += content + "\n";
                        State.getState().error = false;
                    }
                }
                if(s.length() > 0)
                    return s.substring(0, s.length() - 1);
                return s;
            case GREP:
                System.out.println("Grep pattern: " + args.get(0));
                System.out.println("Grep input: " + input);

                if(args.size() == 0) {
                    return "";
                }
                else if(args.size() > 1) {
                    String pattern = args.get(0);
                    for(int i = 1; i < args.size(); i++) {
                        String content = fs.get(args.get(i));
                        if(content == null) {
                            System.err.println("No such file: " + args.get(i));
                            State.getState().error = true;
                        } else {
                            for(String line: content.split("\n")) {
                                if(line.contains(pattern)) 
                                    s += line + "\n";
                            }
                            State.getState().error = false;
                        }
                    }
                } else if (input != null) {
                    String pattern = args.get(0);
                    for(String line: input.split("\n")) {
                        if(line.contains(pattern)) 
                            s += line + "\n";
                    }
                    State.getState().error = false;
                }
                return s;
            default:
                return "Unimplemented";
        }
    }
    public String doTheThing() {
        if(mode == null || filename == null) {
            return internal_doTheThing();
        }
        String s = internal_doTheThing();
        Map<String, String> fs = State.getState().fileSystem;
        if(!State.getState().error) {
            switch(mode) {
                case 0:
                    fs.remove(filename);
                    fs.put(filename, s);
                    return "";
                case 1:
                    if(fs.containsKey(filename)) {
                        fs.replace(filename, fs.get(filename) + s);
                    } else {
                        fs.put(filename, s);
                    }
                    return "";
                default:
                    return "";
            }
        }
        return "";
    }
    public String toString() {
        return doTheThing();
    }
}
