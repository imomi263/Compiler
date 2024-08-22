package Lab2;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class MyVisitor extends SysYBaseVisitor<Void> {

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



    MyVisitor(){
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
    public Void visitDecl(SysYParser.DeclContext ctx) {
        //System.out.println("Decl: "+ ctx.getText());
        System.out.println();
        note=3;
        return super.visitDecl(ctx);
    }

//    @Override
//    public Void visitStmt1(SysYParser.Stmt1Context ctx) {
//
//        note=2;
//        return super.visitStmt1(ctx);
//    }
//    public Void visitStmt4(SysYParser.Stmt4Context ctx) {
//
//        note=2;
//        return super.visitStmt4(ctx);
//    }
//    public Void visitStmt2(SysYParser.Stmt2Context ctx) {
//
//        note=2;
//        return super.visitStmt2(ctx);
//    }
//    public Void visitStmt5(SysYParser.Stmt5Context ctx) {
//
//        note=2;
//        return super.visitStmt5(ctx);
//    }
//    public Void visitStmt6(SysYParser.Stmt6Context ctx) {
//
//        //System.out.println(ctx.getText());
//        note=2;
//        return super.visitStmt6(ctx);
//    }public Void visitStmt7(SysYParser.Stmt7Context ctx) {
//
//        note=2;
//        return super.visitStmt7(ctx);
//    }public Void visitStmt8(SysYParser.Stmt8Context ctx) {
//
//        note=2;
//        return super.visitStmt8(ctx);
//    }
//    public Void visitStmt9(SysYParser.Stmt9Context ctx) {
//
//        note=2;
//        return super.visitStmt9(ctx);
//    }
//    public Void visitStmt3(SysYParser.Stmt3Context ctx) {
//
//        return super.visitStmt3(ctx);
//    }





    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        note=1;
        //System.out.println(ctx.getText());
        return super.visitFuncDef(ctx);
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        //note=1;
        return super.visitFuncFParam(ctx);
    }

    @Override
    public Void visitNumber(SysYParser.NumberContext ctx) {
        System.out.print("\u001B[35m"+ctx.getText());
        clearColor();
        //return super.visitNumber(ctx);
        return null;
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {

        return super.visitConstDef(ctx);
    }

    @Override
    public Void visitChildren(RuleNode node){

        super.visitChildren(node);
        return null;
    }

    @Override
    public Void visitExp4(SysYParser.Exp4Context ctx) {
        note=1;
        return super.visitExp4(ctx);
    }

    @Override
    public Void visitTerminal(TerminalNode node) {

        if(hashset1.contains(node.getText())){
            System.out.print("\u001B[96m"+node.getText());
        }else if(hashset2.contains(node.getText())){
            System.out.print("\u001B[91m"+node.getText());
        }else if(hashset3.contains(node.getText())){
            System.out.print(String.format("\u001B[%dm",91+rank%6)+node.getText());
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
        return super.visitTerminal(node);
    }

}
