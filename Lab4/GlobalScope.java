package Lab4;


import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class GlobalScope extends BaseScope {
    public GlobalScope(Scope enclosingScope) {
        super("GlobalScope", enclosingScope);

    }

    @Override
    public void define(String name, LLVMValueRef type) {
        getTypes().put(name,type);
    }
}
