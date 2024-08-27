package  Lab4;
import Lab2.SysYLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.io.IOException;

import static org.bytedeco.llvm.global.LLVM.*;

public class Lab4 {
    public static void main(String[] args) throws IOException {
        String source = "src/main/java/Lab4/text.txt";
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        ParseTree tree = sysYParser.program();


        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
        //创建module
        LLVMModuleRef module = LLVMModuleCreateWithName("module");

        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        LLVMBuilderRef builder = LLVMCreateBuilder();

        //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
        LLVMTypeRef i32Type = LLVMInt32Type();
        //System.out.println("hello world");

        MyVisitor visitor = new MyVisitor(module,builder,i32Type);
        visitor.visit(tree);


        LLVMDumpModule(module);

        BytePointer error = new BytePointer();
        LLVMPrintModuleToFile(module,"src/main/java/lab4/test.ll",error);
    }
}
