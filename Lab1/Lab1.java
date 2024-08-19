package Lab1;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.util.List;

public class Lab1 {
    public static void main(String[] args) throws IOException {

//        if (args.length < 1) {
//            System.err.println("input path is required");
//        }
        //String source = args[0];
        String source = "src/main/java/Lab1/test.txt";
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        sysYLexer.removeErrorListeners();
        MyErrorListener myErrorListener=new MyErrorListener();
        sysYLexer.addErrorListener(myErrorListener);

        List<? extends Token> myTokens = sysYLexer.getAllTokens();

        if (myErrorListener.getError()) {
            // 假设myErrorListener有一个错误信息输出函数printLexerErrorInformation.
            myErrorListener.printLexerErrorInformation();
        } else {
            for (Token t : myTokens) {
                printSysYTokenInformation(sysYLexer,t);
            }
        }

    }
    private static void printSysYTokenInformation(SysYLexer lexer,Token t){
        String out;
        if(t.getType()!=37)
                out=t.getText();
        else{
            int ans=0;
            for(int i=0;i<t.getText().length();i++){
                if(t.getText().charAt(i)=='X'||t.getText().charAt(i)=='x')continue;
                else ans=ans*10+t.getText().charAt(i)-'0';
            }
            out=String.valueOf(ans);
        }
        System.out.println(lexer.getRuleNames()[t.getType()-1]+" "+out+" at Line "+t.getLine()+'.');

    }

}
