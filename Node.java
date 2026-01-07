import java.util.*;

abstract class Node {
    abstract void execute(SmashShell shell) throws Exception;
}

class CommNode extends Node {
    String comm;
    List<String> args;
    List<RedirNode> redirs;

    CommNode(List<String> args, List<RedirNode> redirs) {
        this.comm = args.get(0);
        this.args = args.subList(1, args.size());
        this.redirs = redirs;
    }

    void execute(SmashShell shell) throws Exception {
        String out = shell.execute(comm, args);
        for(RedirNode r : redirs) {
            r.filename = shell.vars.getOrDefault(r.filename, r.filename);
            if(r.type == 1) {
                shell.fs.put(r.filename, out);
            } else if(r.type == 2) {
                if(shell.fs.containsKey(r.filename)) {
                    shell.fs.replace(r.filename, shell.fs.get(r.filename) + out);
                } else {
                    shell.fs.put(r.filename, out);
                }
            }
        }

        if(redirs.isEmpty()) {
            System.out.print(out);
        }
    }
}

class PipeNode extends Node {
    Node left;
    Node right;

    PipeNode(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    void execute(SmashShell shell) throws Exception {
        String leftOut = shell.captureOut(left);
        shell.inputStack = leftOut;
        right.execute(shell);
        shell.inputStack = null;
    }
}

class ErrCondNode extends Node {
    Node left;
    Node right;
    boolean on_succ;
    ErrCondNode(int mode, Node left, Node right) {
        this.left = left;
        this.right = right;
        on_succ = mode == 1;
    }

    void execute(SmashShell shell) throws Exception {
        left.execute(shell);
        if((on_succ && !shell.error()) || (!on_succ && shell.error())){
            right.execute(shell);
        }
    }
}

class RedirNode {
    int type;
    String filename;

    RedirNode(int type, String filename) {
        this.type = type;
        this.filename = filename;
    }
}

class ListNode extends Node {
    List<Node> nodes;

    ListNode(List<Node> nodes) {
        this.nodes = nodes;
    }

    void execute(SmashShell shell) throws Exception {
        for(Node node : nodes) {
            node.execute(shell);
        }
    }
}
