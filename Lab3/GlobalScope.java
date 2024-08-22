package Lab3;

public class GlobalScope extends BaseScope{
    public GlobalScope( Scope enclosingScope) {
        super("GlobalScope", enclosingScope);
        define("int",IntType.getType());
        define("void",VoidType.getType());
    }
}
