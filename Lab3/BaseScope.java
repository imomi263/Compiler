package Lab3;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements  Scope{
    private String name;
    private final Map<String,Type> types=new LinkedHashMap<>();

    private final Scope enclosingScope;

    public BaseScope(String name,Scope enclosingScope) {
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
    public Map<String, Type> getTypes() {
        return this.types;
    }

    @Override
    public Scope getEnclosingScope() {
        return this.enclosingScope;
    }

    @Override
    public void define(String name,Type type) {
        this.types.put(name,type);
        //System.out.println(name+"+"+type);
    }

    @Override
    public Type resolve(String name) {
        Type type=this.types.get(name);
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
    public Type find(String name) {
        Type type=types.get(name);
        return type;
    }
}
