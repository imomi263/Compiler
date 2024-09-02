package Lab5;



import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements Scope {
    private String name;
    private final Map<String, LLVMValueRef> types=new LinkedHashMap<>();

    private final Scope enclosingScope;

    public BaseScope(String name, Scope enclosingScope) {
        this.name=name;
        this.enclosingScope=enclosingScope;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name=name;
    }

    @Override
    public Map<String, LLVMValueRef> getTypes() {
        return this.types;
    }

    @Override
    public Scope getEnclosingScope() {
        return this.enclosingScope;
    }

    @Override
    public void define(String name,LLVMValueRef type) {
        //this.types.put(name,type);
        //System.out.println(name+"+"+type);
    }

    @Override
    public LLVMValueRef resolve(String name) {
        LLVMValueRef type=types.get(name);
        if(type!=null){
            //System.out.println("-"+name);
            return type;
        }
        if(this.enclosingScope!=null){
            return this.enclosingScope.resolve(name);
        }
        //System.out.println("?" + name);
        return null;
    }

    @Override
    public LLVMValueRef find(String name) {
        LLVMValueRef type=types.get(name);
        return type;
    }
}
