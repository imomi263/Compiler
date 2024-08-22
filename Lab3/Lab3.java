package Lab3;


import Lab3.MySysYListener;

import Lab3.SysYParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class Lab3 {
    public static void main(String[] args) throws IOException {
        String source = "src/main/java/Lab3/text.txt";
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);


        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);


        ParseTree tree = sysYParser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        MySysYListener listener = new MySysYListener();
        walker.walk(listener,tree);
    }
}
