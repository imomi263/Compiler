package Lab5;



import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMPositionBuilderAtEnd;

public class MyVisitor extends SysYBaseVisitor<LLVMValueRef> {
    LLVMModuleRef module;
    LLVMBuilderRef builder;
    LLVMTypeRef i32Type;
    LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
    private GlobalScope globalScope=null;
    private Scope currentScope=null;

    private int tempCounter=1;
    private int labelCounter=1;

    private Stack<LLVMBasicBlockRef> breakStack=new Stack<>();

    private Stack<LLVMBasicBlockRef> continueStack=new Stack<>();
    private LLVMValueRef currentFunction;

    private Map<LLVMValueRef,LLVMTypeRef> funcMap=new LinkedHashMap<>();
    MyVisitor(LLVMModuleRef module,LLVMBuilderRef builder,LLVMTypeRef i32Type){
        this.module=module;
        this.builder=builder;
        this.i32Type=i32Type;
    }


    private String getNewTemp(){
        return "t"+tempCounter++;
    }

    private String getNewLabel(String label){
        return label+labelCounter++;
    }
    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        globalScope=new GlobalScope(null);
        currentScope=globalScope;
        return super.visitProgram(ctx);
    }


    @Override
    public LLVMValueRef visitVarDecl(SysYParser.VarDeclContext ctx) {
        //System.out.println(ctx.getText());

        if(currentScope==globalScope){
            // 创建全局变量
            //System.out.println(ctx.varDef(0).initVal().getText());
            //ctx.varDef()
            if(ctx.varDef(0).initVal()!=null){
                LLVMValueRef value=LLVMConstInt(i32Type,Integer.valueOf(ctx.varDef(0).initVal().getText()),0);
                LLVMValueRef globalvar=LLVMAddGlobal(module,i32Type,ctx.varDef(0).IDENT().getText());
                LLVMSetInitializer(globalvar, /* constantVal:LLVMValueRef*/value);
                //System.out.println(ctx.varDef(0).IDENT().getText());
                currentScope.define(ctx.varDef(0).IDENT().getText(),globalvar);
            }else{
                LLVMValueRef value=LLVMConstInt(i32Type,0,0);
                LLVMValueRef globalvar=LLVMAddGlobal(module,i32Type,ctx.varDef(0).IDENT().getText());
                LLVMSetInitializer(globalvar, /* constantVal:LLVMValueRef*/value);
                //System.out.println(ctx.varDef(0).IDENT().getText());
                currentScope.define(ctx.varDef(0).IDENT().getText(),globalvar);
            }

        }else{
            // 创建局部变量
            if(ctx.varDef(0).initVal()!=null){
                LLVMValueRef num=LLVMConstInt(i32Type,Integer.valueOf(ctx.varDef(0).initVal().getText()),0);

                LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.varDef(0).IDENT().getText());

                //将数值存入该内存
                LLVMBuildStore(builder,num , pointer);
                //System.out.println(ctx.varDef(0).IDENT().getText());
                currentScope.define(ctx.varDef(0).IDENT().getText(),pointer);
            }else{
                LLVMValueRef num=LLVMConstInt(i32Type,0,0);

                LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.varDef(0).IDENT().getText());

                //将数值存入该内存
                LLVMBuildStore(builder,num , pointer);
                //System.out.println(ctx.varDef(0).IDENT().getText());
                currentScope.define(ctx.varDef(0).IDENT().getText(),pointer);
            }

        }
        //return super.visitVarDecl(ctx);
        return null;
    }

    @Override
    public LLVMValueRef visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        //System.out.println(ctx.getText());
        if(ctx.L_BRACKT().size()==0){
            // 不是数组
            //System.out.println(ctx.IDENT().getText());
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.IDENT().getText());

            currentScope.define(ctx.IDENT().getText(),pointer);
            return pointer;

        }else{
            //LLVMTypeRef vectorType = LLVMVectorType(i32Type, 200);
            //申请一个可存放该vector类型的内存
            //LLVMValueRef vectorPointer = LLVMBuildAlloca(builder, vectorType, "vectorPointor");

            System.out.println("还没有实现函数参数有数组");
            return null;
        }

    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        //System.out.println(ctx.getText());
        Scope newScope=new LocalScope(currentScope);
        currentScope=newScope;
        LLVMValueRef function=null;

        if(ctx.funcType().getText().equals("int")){
            LLVMTypeRef returnType = i32Type;

            LLVMTypeRef ft=null;
            if(ctx.funcFParams()!=null){
                PointerPointer<Pointer> argumentTypes = new PointerPointer<>(2);
                //System.out.println(ctx.funcFParams().getChildCount());
                for(int i=0;i<ctx.funcFParams().getChildCount();i++){
                    // inti;
                    //visit(ctx.funcFParams().getChild(i));
                    argumentTypes.put(i,i32Type);
                }
                ft=LLVMFunctionType(returnType,argumentTypes,ctx.funcFParams().getChildCount(),0);
            }
            else{
                ft = LLVMFunctionType(returnType, (LLVMTypeRef) null, /* argumentCount */ 0, /* isVariadic */ 0);
            }

            function = LLVMAddFunction(module, ctx.IDENT().getText(), ft);
            LLVMBasicBlockRef block1 = LLVMAppendBasicBlock(function, ctx.IDENT().getText()+"Entry");
            LLVMPositionBuilderAtEnd(builder, block1);

            funcMap.put(function,ft);

            if(ctx.funcFParams()!=null){
                for(int i=0;i<ctx.funcFParams().getChildCount();i++){
                    // inti;
                    LLVMValueRef num=visit(ctx.funcFParams().getChild(i));
                    LLVMValueRef n = LLVMGetParam(function, /* parameterIndex */i);
                    LLVMBuildStore(builder, n, num);
                }
            }
            currentFunction=function;
        }

        if(ctx.block().blockItem().size()!=0){
            for(int i=0;i<ctx.block().blockItem().size();i++){
                visit(ctx.block().blockItem(i));

            }
        }

        currentScope=currentScope.getEnclosingScope();
        //return super.visitFuncDef(ctx);
        currentScope.define(ctx.IDENT().getText(),function);
        //currentFunction=null;
        return null;
    }


    @Override
    public LLVMValueRef visitStmt_return(SysYParser.Stmt_returnContext ctx) {
        //return super.visitStmt_return(ctx);
        if(ctx.exp()==null){
            LLVMBuildRetVoid(builder);
        }else{
//
            LLVMValueRef result=visit(ctx.exp());
            if(result!=null){
                LLVMBuildRet(builder,result);
            }

        }


        return null;
    }

    @Override
    public LLVMValueRef visitExp3(SysYParser.Exp3Context ctx) {
        // 整数
        LLVMValueRef pointer=LLVMConstInt(i32Type,Integer.valueOf(ctx.number().getText()),0);
        //LLVMValueRef value = LLVMBuildLoad(builder, pointer, /*varName:String*/ctx.getText());
        return pointer;
    }

    @Override
    public LLVMValueRef visitExp2(SysYParser.Exp2Context ctx) {
        // 变量
        //System.out.println("visit2" + ctx.getText());
        LLVMValueRef value=currentScope.resolve(ctx.lVal().getText());
        //System.out.println(value.toString());

            //System.out.println(ctx.lVal().getText());
            //LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.lVal().getText());
        LLVMValueRef num = LLVMBuildLoad(builder, value, /*varName:String*/ctx.lVal().getText());
        return num;



    }

    @Override
    public LLVMValueRef visitExp7(SysYParser.Exp7Context ctx) {

        //System.out.println("visit"+ctx.getText()+ctx.getChildCount()+ctx.getChild(0).getText());
        LLVMValueRef value1=visit(ctx.exp(0));
        //System.out.println(ctx.getText() + ctx.getChildCount());
        String op=ctx.getChild(1).getText();
        LLVMValueRef value2=visit(ctx.getChild(2));

        LLVMValueRef result=null;
        if(op.equals("+")){
            result = LLVMBuildAdd(builder, value1, value2, /* varName:String */"tmp_");
        }else{
            result=LLVMBuildSub(builder,value1,value2,"tmp_");
        }
        return result;
    }

    @Override
    public LLVMValueRef visitExp6(SysYParser.Exp6Context ctx) {
        LLVMValueRef value1=visit(ctx.exp(0));
        //System.out.println(ctx.getText() + ctx.getChildCount());
        String op=ctx.getChild(1).getText();
        LLVMValueRef value2=visit(ctx.getChild(2));
        //System.out.println(ctx.exp(0).getText()+op+ctx.getChild(2).getText());
        //LLVMValueRef pointer1 = LLVMBuildAlloca(builder, i32Type, ctx.exp(0).getText());
        //LLVMValueRef pointer2 = LLVMBuildAlloca(builder, i32Type, ctx.exp(2).getText());

        //LLVMValueRef num1 = LLVMBuildLoad(builder, value1,ctx.exp(0).getText() );
        //LLVMValueRef num2 = LLVMBuildLoad(builder, value2,ctx.getChild(2).getText() );

        LLVMValueRef result=null;
        if(op.equals("*")){
            result = LLVMBuildMul(builder, value1, value2, /* varName:String */"tmp_");
        }else if(op.equals("/")){
            result=LLVMBuildFDiv(builder,value1,value2,"tmp_");
        }else {
            result=LLVMBuildSRem(builder,value1,value2,"tmp_");
        }
        return result;

    }

    @Override
    public LLVMValueRef visitExp5(SysYParser.Exp5Context ctx) {
        LLVMValueRef value=visit(ctx.exp());
        String op=ctx.unaryOp().getText();
        LLVMValueRef tmp_;
        switch (op){
            case "!":

                tmp_ = LLVMBuildICmp(builder, LLVMIntNE, LLVMConstInt(i32Type, 0, 0), value, "tmp_");
                // 生成xor
                tmp_ = LLVMBuildXor(builder, tmp_, LLVMConstInt(LLVMInt1Type(), 1, 0), "tmp_");
                // 生成zext
                tmp_ = LLVMBuildZExt(builder, tmp_, i32Type, "tmp_");

                return tmp_;
            case "+":
                return value;

            case "-":
                tmp_=LLVMBuildNSWSub(builder,LLVMConstInt(LLVMInt32Type(), 0, 0),value,"tmp_");
                return tmp_;
        }
        return null;
        //return super.visitExp5(ctx);
    }



    @Override
    public LLVMValueRef visitExp4(SysYParser.Exp4Context ctx) {
        // 函数调用
        // TODO: 2024/9/1 函数调用
        //System.out.println(ctx.getText());
        LLVMValueRef function=currentScope.resolve(ctx.IDENT().getText());

        PointerPointer<Pointer> arguments = new PointerPointer<>(2);
        LLVMValueRef n = LLVMGetParam(function, /* parameterIndex */0);

        //System.out.println(ctx.funcRParams().getChildCount());
        for(int i=0;i<ctx.funcRParams().param().size();i++){
            //System.out.println(ctx.funcRParams().getChild(i).getText());
            //LLVMValueRef tmp=currentScope.resolve(ctx.funcRParams().getChild(i).getText());
            LLVMValueRef tmp=visit(ctx.funcRParams().param(i));
            if(tmp==null){
                System.out.println("没找到元素:"+ctx.funcRParams().getChild(i).getText());
                return null;
            }else{

                arguments.put(i,tmp);

            }
        }
        LLVMTypeRef funcType=funcMap.get(function);
        if(funcType==null){
            System.out.println("没找到函数");
            return null;
        }

        return LLVMBuildCall2(builder,funcType,function,arguments,1,"returnType");


        //return super.visitExp4(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_if(SysYParser.Stmt_ifContext ctx) {
        // 这里不太清楚该怎么实现嵌套if
        /* if(cond){
                if(cond){
                }
            }
            这里第一个if的代码怎么输出到最后
            我使用了先移除falseBasicBlock，然后在添加进去。
         */

        LLVMValueRef bool=visit(ctx.cond());

        LLVMBasicBlockRef trueBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("true"));
        LLVMBasicBlockRef falseBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("false"));
        //LLVMBasicBlockRef trueBlock = new LLVMBasicBlockRef();
        //LLVMBasicBlockRef falseBlock = new LLVMBasicBlockRef();


        //LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        LLVMBuildCondBr(builder,bool,trueBlock,falseBlock);
        LLVMRemoveBasicBlockFromParent(falseBlock);

        LLVMPositionBuilderAtEnd(builder, trueBlock);
        //System.out.println(ctx.stmt().getText());

        visit(ctx.stmt());

        LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        LLVMPositionBuilderAtEnd(builder, falseBlock);

        //LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        //LLVMPositionBuilderAtEnd(builder, falseBlock);
        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("end"));
        LLVMBuildBr(builder,endBlock);
        LLVMPositionBuilderAtEnd(builder, endBlock);

        return null;
        //return super.visitStmt_if(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_if_else(SysYParser.Stmt_if_elseContext ctx) {

        LLVMValueRef bool=visit(ctx.cond());

        LLVMBasicBlockRef trueBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("true"));
        LLVMBasicBlockRef falseBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("false"));

        LLVMBuildCondBr(builder,bool,trueBlock,falseBlock);
        LLVMRemoveBasicBlockFromParent(falseBlock);

        LLVMPositionBuilderAtEnd(builder, trueBlock);
        //System.out.println(ctx.stmt().getText());

        visit(ctx.stmt(0));

        LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        LLVMPositionBuilderAtEnd(builder, falseBlock);
        visit(ctx.stmt(1));

        LLVMBasicBlockRef endBlock = LLVMAppendBasicBlock(currentFunction,getNewLabel("end"));
        LLVMBuildBr(builder,endBlock);
        LLVMPositionBuilderAtEnd(builder, endBlock);
        //LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        //LLVMPositionBuilderAtEnd(builder, falseBlock);

        return null;

        //return super.visitStmt_if_else(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_while(SysYParser.Stmt_whileContext ctx) {

        LLVMBasicBlockRef beginBlock=LLVMAppendBasicBlock(currentFunction,getNewLabel("begin"));
        LLVMPositionBuilderAtEnd(builder, beginBlock);
        LLVMValueRef bool=visit(ctx.cond());
        continueStack.push(beginBlock);

        LLVMBasicBlockRef trueBlock=LLVMAppendBasicBlock(currentFunction,getNewLabel("b.true"));
        LLVMBasicBlockRef falseBlock=LLVMAppendBasicBlock(currentFunction,getNewLabel("b.false"));
        //LLVMRemoveBasicBlockFromParent(trueBlock);
        LLVMRemoveBasicBlockFromParent(falseBlock);
        breakStack.push(falseBlock);

        LLVMBuildCondBr(builder,bool,trueBlock,falseBlock);
        LLVMPositionBuilderAtEnd(builder, trueBlock);
        visit(ctx.stmt());

        LLVMBuildBr(builder,beginBlock);

        breakStack.pop();
        continueStack.pop();
        LLVMAppendExistingBasicBlock(currentFunction,falseBlock);
        LLVMPositionBuilderAtEnd(builder, falseBlock);
        return null;
        //return super.visitStmt_while(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_break(SysYParser.Stmt_breakContext ctx) {
        LLVMBasicBlockRef tmp=breakStack.peek();
        LLVMBuildBr(builder,tmp);
        //LLVMPositionBuilderAtEnd(builder,tmp);
        return null;
        //return super.visitStmt_break(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_continue(SysYParser.Stmt_continueContext ctx) {
        LLVMBasicBlockRef tmp=continueStack.peek();
        LLVMBuildBr(builder,tmp);
        //LLVMPositionBuilderAtEnd(builder,tmp);
        return null;
        //return super.visitStmt_continue(ctx);
    }

    @Override
    public LLVMValueRef visitStmt_assign(SysYParser.Stmt_assignContext ctx) {
        String var=ctx.lVal().IDENT().getText();
        LLVMValueRef ref=currentScope.resolve(var);
        LLVMBuildStore(builder,ref , visit(ctx.exp()));
        //return super.visitStmt_assign(ctx);
        return null;
    }

    @Override
    public LLVMValueRef visitCond_AND(SysYParser.Cond_ANDContext ctx) {

        LLVMValueRef lhs=visit(ctx.cond(0));
        //LLVMValueRef tmp=LLVMBuildICmp(builder,LLVMIntNE,lhs,LLVMConstInt(LLVMInt32Type(), 0, 0),getNewTemp());

        LLVMValueRef rhs=visit(ctx.cond(1));
        LLVMValueRef tmp=LLVMBuildAnd(builder,lhs,rhs,getNewTemp());
        return tmp;
        //return super.visitCond_AND(ctx);
    }

    @Override
    public LLVMValueRef visitCond_EQ(SysYParser.Cond_EQContext ctx) {
        LLVMValueRef lhs=visit(ctx.cond(0));
        LLVMValueRef rhs=visit(ctx.cond(1));
        LLVMValueRef tmp=null;
        if(ctx.EQ()!=null){
            // 判断等于
            tmp=LLVMBuildICmp(builder,LLVMIntEQ,lhs,rhs,getNewTemp());
        }else{
            tmp=LLVMBuildICmp(builder,LLVMIntNE,lhs,rhs,getNewTemp());
        }
        return tmp;
        //return super.visitCond_EQ(ctx);
    }

    @Override
    public LLVMValueRef visitCond_LT(SysYParser.Cond_LTContext ctx) {

        LLVMValueRef lhs=visit(ctx.cond(0));
        LLVMValueRef rhs=visit(ctx.cond(1));
        LLVMValueRef tmp=null;
        if(ctx.LT()!=null){
            // 小于
            tmp=LLVMBuildICmp(builder,LLVMIntSLT,lhs,rhs,getNewTemp());
        }else if(ctx.LE()!=null){
            // <=
            tmp=LLVMBuildICmp(builder,LLVMIntSLE,lhs,rhs,getNewTemp());
        }else if(ctx.GT()!=null){
            // >
            tmp=LLVMBuildICmp(builder,LLVMIntSGT,lhs,rhs,getNewTemp());
        }else{
            // >=
            tmp=LLVMBuildICmp(builder,LLVMIntSGE,lhs,rhs,getNewTemp());
        }
        return tmp;
        //return super.visitCond_LT(ctx);
    }

    @Override
    public LLVMValueRef visitCond_exp(SysYParser.Cond_expContext ctx) {

        LLVMValueRef tmp=visit(ctx.exp());
        //LLVMValueRef ret=LLVMBuildICmp(builder,LLVMIntNE,LLVMConstInt(LLVMInt1Type(),0,0),tmp,getNewTemp());
        return tmp;
        //return super.visitCond_exp(ctx);
    }

    @Override
    public LLVMValueRef visitCond_OR(SysYParser.Cond_ORContext ctx) {
        LLVMValueRef lhs=visit(ctx.cond(0));
        //LLVMValueRef tmp=LLVMBuildICmp(builder,LLVMIntNE,lhs,LLVMConstInt(LLVMInt32Type(), 0, 0),getNewTemp());

        LLVMValueRef tmp_lhs=LLVMBuildICmp(builder,LLVMIntEQ,lhs,LLVMConstInt(LLVMInt32Type(), 0, 0),getNewTemp());


        LLVMBasicBlockRef lhs_false=LLVMAppendBasicBlock(currentFunction,getNewLabel("lhs_false"));
        LLVMBasicBlockRef all_true=LLVMAppendBasicBlock(currentFunction,getNewLabel("all_true"));
        LLVMRemoveBasicBlockFromParent(all_true);

        LLVMBuildCondBr(builder,tmp_lhs,lhs_false,all_true);
        LLVMPositionBuilderAtEnd(builder,lhs_false);

        LLVMValueRef rhs=visit(ctx.cond(1));
        LLVMValueRef tmp_rhs=LLVMBuildICmp(builder,LLVMIntNE,rhs,LLVMConstInt(LLVMInt32Type(), 0, 0),getNewTemp());

        LLVMAppendExistingBasicBlock(currentFunction,all_true);
        //LLVMBasicBlockRef falseBlock=LLVMAppendBasicBlock(currentFunction,getNewLabel("all_false"));
        //LLVMBuildCondBr(builder,tmp_rhs,all_true,falseBlock);
        //LLVMPositionBuilderAtEnd(builder,all_true);

        return tmp_rhs;
        //return super.visitCond_OR(ctx);
    }
}
