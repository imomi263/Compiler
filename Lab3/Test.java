package Lab3;

public class Test {
    public static void main(String[] args) {

        //System.out.println(IntType.getType());
        Type type=FunctionType.type;
        System.out.println(type.getClass());
        System.out.println(FunctionType.type==type);
        System.out.println(FunctionType.type);
        //System.out.println(ErrorType.ErrorCode.REDEF_FUNC);

    }
}
