package Lab3;

public class ErrorType extends Type{

    public enum ErrorCode{
        NO_ERROR,UNDEF_VAR, UNDEF_FUNC,REDEF_VAR,REDEF_FUNC,ASSIGN_MISMATCH,OP2_MISMATCH,
        RETURN_MISMATCH,FUNC_MISMATCH,OP1_MISMATCH,FUNC_FOR_VAR,ASSIGN_FOR_FUNC;
    }

    //static Type REDEF_FUNC=new ErrorType(1);
    //static Type REDEF_FUNC2=new ErrorType(2);
    public Type getType(){
        return null;
    }

}
