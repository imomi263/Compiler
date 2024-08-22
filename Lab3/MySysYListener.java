package Lab3;


import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

import static Lab3.ErrorType.ErrorCode.*;
import static java.lang.Integer.parseInt;

public class MySysYListener extends SysYBaseListener {
    private GlobalScope globalScope=null;
    private Scope currentScope=null;

    private String currentFunction;
    private static int countCharUsingIndexOf(String str, char ch) {
        int index = str.indexOf(ch);
        if (index == -1) {
            return 0; // 没有找到字符
        }
        return 1 + countCharUsingIndexOf(str.substring(index + 1), ch);
    }
    // for func params
    ArrayList<Type> arrayListType=new ArrayList<Type>();
    @Override
    public void enterProgram(SysYParser.ProgramContext ctx) {
        globalScope = new GlobalScope(null);
        currentScope = globalScope;
        super.enterProgram(ctx);
    }


    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        Scope newScope=new LocalScope(currentScope);
        currentScope=newScope;
        super.enterBlock(ctx);
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        currentScope=currentScope.getEnclosingScope();
        super.exitBlock(ctx);
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        arrayListType.clear();
        String funcName=ctx.IDENT().getText();

        if(currentScope.find(funcName)!=null){
            OutPutHelper.printSemanticError(ErrorType.ErrorCode.REDEF_FUNC,
                    ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return;
        }
        currentFunction=funcName;
        Type retType=null;
        if(ctx.funcType().getText().equals("int")){
            retType=IntType.getType();
        }else{
            retType=VoidType.getType();
        }
        if(ctx.funcFParams()!=null){
            //System.out.println(ctx.funcFParams().getChildCount());
            for(int i=0;i<=((ctx.funcFParams().getChildCount()+1)/2)-1;i++){
                //System.out.println(i);
                //System.out.println(ctx.funcFParams().funcFParam(i).getText());
                if(ctx.funcFParams().funcFParam(i).L_BRACKT(0)!=null){
                    //System.out.println(ctx.funcFParams().funcFParam(i).L_BRACKT(0));
                    arrayListType.add(ArrayType.type);
                }else{
                    arrayListType.add(IntType.getType());
                }
            }
        }else{
            arrayListType.add(VoidType.getType());
        }

        FunctionType functionType=new FunctionType(retType,arrayListType);
        currentScope.define(funcName,functionType);
        super.enterFuncDef(ctx);

    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {

        super.exitFuncDef(ctx);
    }

    @Override
    public void enterFuncFParam(SysYParser.FuncFParamContext ctx) {
        String varName=ctx.IDENT().getText();
        if(currentScope.find(varName)!=null){
            OutPutHelper.printSemanticError(ErrorType.ErrorCode.REDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return;
        }
        super.enterFuncFParam(ctx);
    }

    @Override
    public void enterVarDecl(SysYParser.VarDeclContext ctx) {
        for(int i=0;i<ctx.varDef().size();i++){
            if(currentScope.find(ctx.varDef(i).IDENT().getText())!=null){
                // redef
                OutPutHelper.printSemanticError(REDEF_VAR,
                        ctx.varDef(i).IDENT().getSymbol().getLine(),ctx.varDef(i).IDENT().getText());
                continue;
            }

            //System.out.println(ctx.varDef(i).getText());
            if(ctx.varDef(i).L_BRACKT().size()!=0){
                //System.out.println(ctx.varDef(i).L_BRACKT().size());
                currentScope.define(ctx.varDef(i).IDENT().getText(),new ArrayType(IntType.getType(),
                        parseInt(ctx.varDef(i).constExp().get(i).getText()),
                                ctx.varDef(i).L_BRACKT().size()));
            }else{
                currentScope.define(ctx.varDef(i).IDENT().getText(),IntType.getType());
            }
        }
        super.enterVarDecl(ctx);
    }

    @Override
    public void enterConstDecl(SysYParser.ConstDeclContext ctx) {
        for(int i=0;i<ctx.constDef().size();i++){
            if(currentScope.find(ctx.constDef(i).IDENT().getText())!=null){
                // redef
                OutPutHelper.printSemanticError(REDEF_VAR,
                        ctx.constDef(i).IDENT().getSymbol().getLine(),ctx.constDef(i).IDENT().getText());
                continue;
            }

            System.out.println(ctx.constDef(i).getText());
            if(ctx.constDef(i).L_BRACKT().size()!=0){
                //System.out.println(ctx.varDef(i).L_BRACKT().size());
                currentScope.define(ctx.constDef(i).IDENT().getText(),new ArrayType(IntType.getType(),
                        parseInt(ctx.constDef(i).constExp().get(i).getText(),
                                ctx.constDef(i).L_BRACKT().size())));
            }else{
                currentScope.define(ctx.constDef(i).IDENT().getText(),IntType.getType());
            }
        }
        super.enterConstDecl(ctx);
    }

    @Override
    public void visitTerminal(TerminalNode node) {

        //if(node.getSymbol().getType()==33)
        //System.out.println(node);

        //System.out.println(node.getText());

        super.visitTerminal(node);
    }

    @Override
    public void enterExp4(SysYParser.Exp4Context ctx) {
        // 使用函数
        String funcName=ctx.IDENT().getText();
        //System.out.println(ctx.funcRParams()+ctx.getText());
        Type type=currentScope.resolve(funcName);
        if(type==null){
            OutPutHelper.printSemanticError(UNDEF_FUNC,
                    ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return;
        }else if(!type.equals(FunctionType.type)){
            // 不是函数
            //System.out.println(type.getClass()==FunctionType.type.getClass());
            //System.out.println(ctx.funcRParams()+ctx.getText());
            OutPutHelper.printSemanticError(FUNC_FOR_VAR,
                    ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return;
        }
        arrayListType.clear();
//        System.out.println(ctx.funcRParams().param().size());
//        System.out.println(ctx.funcRParams().getText());
        //System.out.println(ctx.getText());

        if(ctx.funcRParams()!=null) {
            for (int i = 0; i < ctx.funcRParams().param().size(); i++) {
                String string = ctx.funcRParams().param(i).getText();
                //System.out.println(string);
                if (string.charAt(0) >= '0' || string.charAt(0) <= '9') {
                    // 整数
                    arrayListType.add(IntType.getType());
                } else {
                    Type m_type = currentScope.resolve(string);
                    if (m_type == null) {
                        OutPutHelper.printSemanticError(UNDEF_VAR,
                                ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                        continue;
                    } else {
                        arrayListType.add(m_type);
                    }
                }
            }

            //if(type)
            if (((FunctionType) type).getParamsList().size() != arrayListType.size()) {
                OutPutHelper.printSemanticError(FUNC_MISMATCH,
                        ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            }
            for (int i = 0; i < arrayListType.size(); i++) {
                if (!arrayListType.get(i).equals(((FunctionType) type).getParamsList().get(i))) {
                    OutPutHelper.printSemanticError(FUNC_MISMATCH,
                            ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                }
            }
        }else{
            // 没有参数

        }
        super.enterExp4(ctx);
    }

    @Override
    public void enterExp7(SysYParser.Exp7Context ctx) {
        // 假定这里必须是int相加减
        //System.out.println(ctx.exp(0).getText());

        for(int i=0;i<=1;i++){
            if(ctx.exp(i).getText().charAt(0)>='0' &&ctx.exp(i).getText().charAt(0)<='9'){
                // 是int
                continue;
            }
            int index1=ctx.exp(i).getText().indexOf("[");
            //System.out.println(index1);
            if(index1==-1){ // 操作数是int类型
                Type type11=currentScope.resolve(ctx.exp(i).getText());
                if(type11==null){
                    OutPutHelper.printSemanticError(UNDEF_VAR,
                            ctx.exp(i).getStart().getLine()
                            ,ctx.exp(i).getText());
                }else if(type11!=IntType.getType()){
                    OutPutHelper.printSemanticError(OP2_MISMATCH,
                            ctx.exp(i).getStart().getLine()
                            ,ctx.exp(i).getText());
                }
            }else{ // a[1][2]
                String string=ctx.exp(i).getText().substring(0,index1);
                //System.out.println(string);
                int level=countCharUsingIndexOf(ctx.exp(i).getText(),'[');
                Type type=currentScope.resolve(string);
                if(type==null){
                    OutPutHelper.printSemanticError(UNDEF_VAR,
                            ctx.exp(i).getStart().getLine()
                            ,ctx.exp(i).getText());

                }else if(!type.equals(ArrayType.type)){
                    //System.out.println(type+" "+ArrayType.type);
                    OutPutHelper.printSemanticError(OP1_MISMATCH,
                            ctx.exp(i).getStart().getLine()
                            ,ctx.exp(i).getText());

                }else{
                    if(level!=((ArrayType)type).getLevel()){
                        OutPutHelper.printSemanticError(OP2_MISMATCH,
                                ctx.exp(i).getStart().getLine()
                                ,ctx.exp(i).getText());
                    }
                }
            }
        }

        super.enterExp7(ctx);
    }

    @Override
    public void enterStmt_return(SysYParser.Stmt_returnContext ctx) {
        //System.out.println(ctx.exp().getText());
        if(ctx.exp()==null){
            Type type=currentScope.resolve(currentFunction);
            if(type==null){
                return;
            }
            if(!((FunctionType)type).getRetType().equals(VoidType.getType())){
                OutPutHelper.printSemanticError(RETURN_MISMATCH,
                        ctx.getStart().getLine()
                        ,ctx.getText());
            }
        }else{
            Type type=currentScope.resolve(currentFunction);

            if(type==null){
                return;
            }
            String ident=ctx.exp().getText();
            Type ret_type=currentScope.resolve(ident);

            Type func_ret_type=((FunctionType)type).getRetType();
            if(ret_type==null || func_ret_type==null){
                return;
            }
            if(ident.charAt(0)>='0' && ident.charAt(0)<='9'){
                // 返回一个int
                if(!ret_type.equals(func_ret_type)){
                    OutPutHelper.printSemanticError(RETURN_MISMATCH,
                            ctx.getStart().getLine()
                            ,ctx.getText());
                }
            }else{

                //System.out.println(type.getClass());
                //System.out.println(ident+currentScope.resolve(ident).getClass());

                //System.out.println();
                if(!ret_type.equals(func_ret_type)){
                    OutPutHelper.printSemanticError(RETURN_MISMATCH,
                            ctx.getStart().getLine()
                            ,ctx.getText());
                }
            }
        }
        super.enterStmt_return(ctx);
    }

    @Override
    public void enterLVal(SysYParser.LValContext ctx) {
        Type type=currentScope.resolve(ctx.IDENT().getText());
        //System.out.println(ctx.getParent().getParent().getText());
        //if(ctx.getParent().getParent())
        if(type==null){
            OutPutHelper.printSemanticError(UNDEF_VAR,
                    ctx.getStart().getLine()
                    ,ctx.getText());
        }
        if(ctx.L_BRACKT().size()!=0){
            // 是数组
            if(!type.equals(ArrayType.type)){
                OutPutHelper.printSemanticError(OP1_MISMATCH,
                        ctx.getStart().getLine()
                        ,ctx.IDENT().getText());
            }
        }

        super.enterLVal(ctx);
    }

    @Override
    public void enterStmt_assign(SysYParser.Stmt_assignContext ctx) {
        Type type=currentScope.resolve(ctx.lVal().getText());
//        if(type==null){
//            OutPutHelper.printSemanticError(UNDEF_VAR,
//                    ctx.getStart().getLine()
//                    ,ctx.lVal().getText());
//            return;
//        }
        if(type==null){
            return;
        }
        if(!type.equals(ArrayType.type) && !type.equals(IntType.getType())){
            OutPutHelper.printSemanticError(ASSIGN_FOR_FUNC,
                    ctx.getStart().getLine()
                    ,ctx.getText());
        }
        super.enterStmt_assign(ctx);
    }
}
