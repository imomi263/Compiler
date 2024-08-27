package Lab4;



import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYBaseVisitor<LLVMValueRef> {
    LLVMModuleRef module;
    LLVMBuilderRef builder;
    LLVMTypeRef i32Type;

    private GlobalScope globalScope=null;
    private Scope currentScope=null;

    private LLVMValueRef currentFunction;

    MyVisitor(LLVMModuleRef module,LLVMBuilderRef builder,LLVMTypeRef i32Type){
        this.module=module;
        this.builder=builder;
        this.i32Type=i32Type;
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
            LLVMValueRef value=LLVMConstInt(i32Type,Integer.valueOf(ctx.varDef(0).initVal().getText()),0);
            LLVMValueRef globalvar=LLVMAddGlobal(module,i32Type,ctx.varDef(0).IDENT().getText());
            LLVMSetInitializer(globalvar, /* constantVal:LLVMValueRef*/value);
            //System.out.println(ctx.varDef(0).IDENT().getText());
            currentScope.define(ctx.varDef(0).IDENT().getText(),globalvar);
        }else{
            // 创建局部变量
            LLVMValueRef num=LLVMConstInt(i32Type,Integer.valueOf(ctx.varDef(0).initVal().getText()),0);

            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.varDef(0).IDENT().getText());

            //将数值存入该内存
            LLVMBuildStore(builder,num , pointer);
            //System.out.println(ctx.varDef(0).IDENT().getText());
            currentScope.define(ctx.varDef(0).IDENT().getText(),pointer);
        }
        //return super.visitVarDecl(ctx);
        return null;
    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        //System.out.println(ctx.getText());
        Scope newScope=new LocalScope(currentScope);
        currentScope=newScope;
        if(ctx.funcType().getText().equals("int")){
            LLVMTypeRef returnType = i32Type;
            //PointerPointer<Pointer> argumentTypes = new PointerPointer<>(2)
            //        .put(0, i32Type)
            //        .put(1, i32Type);
            LLVMTypeRef ft = LLVMFunctionType(returnType, (LLVMTypeRef) null, /* argumentCount */ 0, /* isVariadic */ 0);
            LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/ctx.IDENT().getText(), ft);
            LLVMBasicBlockRef block1 = LLVMAppendBasicBlock(function, ctx.IDENT().getText()+"Entry");
            LLVMPositionBuilderAtEnd(builder, block1);
            currentFunction=function;
        }
        if(ctx.block().blockItem().size()!=0){
            for(int i=0;i<ctx.block().blockItem().size();i++){
                visit(ctx.block().blockItem(i));

            }
        }

        currentScope=currentScope.getEnclosingScope();
        //return super.visitFuncDef(ctx);
        currentFunction=null;
        return null;
    }


    @Override
    public LLVMValueRef visitStmt_return(SysYParser.Stmt_returnContext ctx) {
        //return super.visitStmt_return(ctx);
        if(ctx.exp()==null){
            LLVMBuildRetVoid(builder);
        }else{
//            int size=ctx.exp().getChildCount();
//            for(int i=0;i<size;i++){
//                //System.out.println(ctx.exp().getChild(i).getText());
//                LLVMValueRef tmp=visit(ctx.exp().getChild(i));
//                //System.out.println(tmp);
//
//            }
            //LLVMValueRef result = LLVMBuildAdd(builder, n, zero, /* varName:String */"result");
            //LLVMBuildRet(builder,result);
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
        //System.out.println(ctx.exp(0).getText()+op+ctx.getChild(2).getText());
        //LLVMValueRef pointer1 = LLVMBuildAlloca(builder, i32Type, ctx.exp(0).getText());
        //LLVMValueRef pointer2 = LLVMBuildAlloca(builder, i32Type, ctx.exp(2).getText());

        //LLVMValueRef num1 = LLVMBuildLoad(builder, value1,ctx.exp(0).getText() );
        //LLVMValueRef num2 = LLVMBuildLoad(builder, value2,ctx.getChild(2).getText() );

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
}
