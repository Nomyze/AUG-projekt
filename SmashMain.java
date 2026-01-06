import java.io.*;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ScannerBuffer;

class SmashMain {
	public static void main(String[] args) throws Exception {
        ComplexSymbolFactory sf = new ComplexSymbolFactory();

		parser p;
        if(args.length > 0) {
            p = new parser(new ScannerBuffer(new Lexer(new BufferedReader(new FileReader(args[0])), sf)), sf);
        } else {
            p = new parser(new ScannerBuffer(new Lexer(new BufferedReader(new InputStreamReader(System.in)), sf)), sf);
        }
		p.parse();
	}
}

