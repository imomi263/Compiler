package Lab3;

import java.util.Map;

public interface Scope {
    public String getName();

    public void setName(String name);

    Map<String,Type> getTypes();

    Scope getEnclosingScope();

    void define(String name,Type type);

    Type resolve(String name);

    Type find(String name);
}
