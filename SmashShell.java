import java.io.*;
import java.util.*;

class SmashShell {
    Map<String, String> fs = new HashMap<>();
    String inputStack = null;
    boolean error = false;

    private List<String> prepareArgs(List<String> args) {
        args.replaceAll(s -> s.equals("$?") ? (error ? "1" : "0") : s);
        return args;
    }
    String execute(String cmd, List<String> args) throws Exception {
        if(cmd == null) return "";
        String out = "";
        args = prepareArgs(args);
        switch(cmd) {
            case "echo":
                out += String.join(" ", args) + "\n";
                error = false;
                break;
            case "cat":
                if(!args.isEmpty()) {
                    for(String file : args) {
                        String content = fs.get(file);
                        if(content != null) {
                            out += content;
                            error = false;
                        } else {
                            System.err.println("No such file: " + file);
                            error = true;
                        }
                    }
                } else if(inputStack != null) {
                    out += inputStack;
                }
                break;
            case "wc":
                int count = 0;
                if(!args.isEmpty()) {
                    for(String file : args) {
                        String content = fs.get(file);
                        if(content != null) {
                            count += content.chars().filter(ch -> ch == '\n').count();
                            error = false;
                        } else {
                            System.err.println("No such file: " + file);
                            error = true;
                        }
                    }
                } else if (inputStack != null){
                    count += inputStack.chars().filter(ch -> ch == '\n').count();
                    error = false;
                } else {
                    count = 0;
                }
                out += "" + count + "\n";
                break;
            case "grep":
                if(args.size() > 1) {
                    String pattern = args.get(0);
                    for(int i = 1; i < args.size(); i++) {
                        String content = fs.get(args.get(i));
                        if(content == null) {
                            System.err.println("No such file: " + args.get(i));
                            error = true;
                        } else {
                            for(String line: content.split("\n")) {
                                if(line.contains(pattern)) 
                                    out += line + "\n";
                            }
                            error = false;
                        }
                    }
                } else if (inputStack != null) {
                    String pattern = args.get(0);
                    for(String line: inputStack.split("\n")) {
                        if(line.contains(pattern)) 
                            out += line + "\n";
                    }
                    error = false;
                }
                break;
            default:
                out += "Unknown command: " + cmd;
        }
        return out;
    }

    String captureOut(Node node) throws Exception {
        String oldStack = inputStack;
        inputStack = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream sysOut = System.out;
        System.setOut(new PrintStream(baos));
        node.execute(this);
        System.out.flush();
        System.setOut(sysOut);
        inputStack = oldStack;
        return baos.toString();
    }
}
