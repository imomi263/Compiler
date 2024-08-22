package Lab3;

public class IntType extends Type{
    private static final Type type=new IntType();
    public static Type getType(){
        return type;
    }
}
