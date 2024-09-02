package Lab5;


import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.Map;

public interface Scope {
    public String getName();

    public void setName(String name);

    Map<String, LLVMValueRef> getTypes();

    Scope getEnclosingScope();

    void define(String name, LLVMValueRef type);

    LLVMValueRef resolve(String name);

    LLVMValueRef find(String name);
}
