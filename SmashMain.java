import java.io.*;
import java.util.*;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ScannerBuffer;

class SmashMain {
	public static void main(String[] args) throws Exception {
        SmashShell shell = new SmashShell();
        ComplexSymbolFactory sf = new ComplexSymbolFactory();

		parser p;
        if(args.length > 0) {
            p = new parser(new Lexer(new BufferedReader(new FileReader(args[0])), sf), sf);
            try {
                Node ast = (Node)p.parse().value;
                ast.execute(shell);
            } catch(Exception e) {
                System.err.println("Failed execution");
            }
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            //p = new parser(new ScannerBuffer(new Lexer(new BufferedReader(new InputStreamReader(System.in)), sf)), sf);
            while(true) {
                String input = readCommand(in);
                if(input == null) 
                    break;
                try {
                    Lexer l = new Lexer(new BufferedReader(new StringReader(input)), sf);
                    p = new parser(l, sf);
                    Node ast = (Node)p.parse().value;
                    ast.execute(shell);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}

    static String readCommand(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean multiline = false;

        while(true) {
            System.out.print(!multiline ? "$ " : "> ");
            String line;
            line = in.readLine();
            if(line == null) return null;

            sb.append(line).append("\n");

            if(isComplete(sb.toString())) {
                return sb.toString();
            }
            multiline = true;
        }
    }
    static boolean isComplete(String s) {
        int quotes = 0;
        char last = '\0';
        for(char c : s.toCharArray()) {
            if(c == '"' && last != '\\') {
                quotes += 1;
            }
            last = c;
        }
        return quotes % 2 == 0;
    }
}

