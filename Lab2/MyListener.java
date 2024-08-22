package Lab2;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Arrays;
import java.util.HashSet;

public class MyListener extends SysYBaseListener{
    String [] values1={"const","void","else","while","break","continue", "return","int","if"};
    String [] values2={"+","-","%","*", "/","==","=","!=",">","<","&&","!","||", ",",";"};


    private int rank=0;
    String [] values3={"(","[","{"};
    String [] values4={")","]","}"};
    private HashSet<String> hashset1=new HashSet<>();
    private HashSet<String> hashset2=new HashSet<>();;
    private HashSet<String> hashset3=new HashSet<>();;
    private HashSet<String> hashset4=new HashSet<>();;

    private int note=0;
    // note=1 是函数名
    // note=2 是语句
    // note=3 是声明



    MyListener(){
        System.out.println("MyVisitor init");
        hashset1.addAll(Arrays.asList(values1));
        hashset2.addAll(Arrays.asList(values2));
        hashset3.addAll(Arrays.asList(values3));
        hashset4.addAll(Arrays.asList(values4));
    }
    private void clearColor(){
        System.out.print("\u001B[0m");
    }
    private String getColor(int t){
        return String.format("\u001B[%dm",t);
    }

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {
        note=3;
        super.enterDecl(ctx);
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx){
        note=0;
        System.out.println();
        super.exitDecl(ctx);
    }
    @Override
    public void enterStmt(SysYParser.StmtContext ctx) {

        note=2;
        super.enterStmt(ctx);
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        note=0;
        System.out.println();
        super.exitStmt(ctx);
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        note=0;
        super.enterBlock(ctx);
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        note=1;
        super.enterFuncDef(ctx);
    }

    @Override
    public void enterFuncFParam(SysYParser.FuncFParamContext ctx) {
        note=1;
        super.enterFuncFParam(ctx);
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {
        note=0;
        super.exitFuncDef(ctx);
    }

    @Override
    public void enterExp4(SysYParser.Exp4Context ctx) {
        note=1;
        super.enterExp4(ctx);
    }

    @Override
    public void exitExp4(SysYParser.Exp4Context ctx) {
        note=0;
        super.exitExp4(ctx);
    }

    @Override
    public void enterNumber(SysYParser.NumberContext ctx) {
        //clearColor();
        //System.out.print("\u001B[35m");
        //clearColor();
        super.enterNumber(ctx);
    }

    public void visitTerminal(TerminalNode node) {
        if(node.getSymbol().getType()==37){
            clearColor();
            System.out.print("\u001B[35m"+node.getText());
        }
        else if(hashset1.contains(node.getText())){
            System.out.print("\u001B[96m"+node.getText()+" ");
            clearColor();

        }else if(hashset2.contains(node.getText())){
            System.out.print(" ");
            System.out.print("\u001B[91m"+node.getText());
            System.out.print(" ");
        }else if(hashset3.contains(node.getText())){
            System.out.print(String.format("\u001B[%dm",91+rank%6)+node.getText());
            if(node.getText().equals("{")){
                System.out.println();
            }
            rank++;
        }else if(hashset4.contains(node.getText())){
            rank--;
            System.out.print(String.format("\u001B[%dm",91+rank%6)+node.getText());
        }else if(note==1 && node.getSymbol().getType()==33){
            // here 函数
            //System.out.println(node.getText());
            System.out.print(getColor(93)+node.getText());
            note=0;

        }else if(note==2){
            System.out.print(getColor(97)+node.getText());
            //note=0;
        }else if(note==3){
            System.out.print(getColor(95)+getColor(4)+node.getText());
            //note=0;
        }
        else{
            System.out.print(node.getText());
        }
        clearColor();
        super.visitTerminal(node);
    }
}
