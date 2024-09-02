package Lab5;



import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class LocalScope extends BaseScope {
    private static int localScopeCounter=0;
    public LocalScope(Scope enclosingScope) {
        super("LocalScope", enclosingScope);

        String localScopeName=getName()+localScopeCounter;
        setName(localScopeName);
        localScopeCounter++;
    }

    public void define(String name, LLVMValueRef type) {
        getTypes().put(name,type);
    }
}
