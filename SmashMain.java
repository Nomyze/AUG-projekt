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
            p = new parser(new ScannerBuffer(new Lexer(new BufferedReader(new InputStreamReader(System.in)), sf)), sf);
            try {
                Node ast = (Node)p.parse().value;
                ast.execute(shell);
            } catch(Exception e) {
            }
        }
	}
}

