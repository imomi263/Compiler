package Lab1;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyErrorListener extends BaseErrorListener {
    boolean error;
    MyErrorListener(){
        error=false;
    }
    class ErrorMsg{
        int line;
        String msg;
        ErrorMsg(int i,String s){
            msg=s;
            line =i;
        }

    };
    List<ErrorMsg> arrayList  = new ArrayList<>();
    public void syntaxError(Recognizer<?,?>recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        error=true;
        arrayList.add(new ErrorMsg(line,msg));
    }
    public  void printLexerErrorInformation(){
        for(int i=0;i<arrayList.size();i++){
            System.out.println("Error type A at Line "+ arrayList.get(i).line +':'+arrayList.get(i).msg);
        }
    }
    public  boolean getError(){
        return error;
    }
}
