package Lab3;

public  class OutPutHelper {
    public static void printSemanticError(ErrorType.ErrorCode errorCode, int line, String context){

        switch(errorCode){
            case UNDEF_VAR:
                System.out.println(String.format("Error type %d at Line %d: Undefined variable: %s",
                        errorCode.ordinal(),line,context));
                break;
            case UNDEF_FUNC:
                System.out.println(String.format("Error type %d at Line %d: Undefined function: %s",
                        errorCode.ordinal(),line,context));
                break;
            case REDEF_FUNC :
                System.out.println(String.format("Error type %d at Line %d: Redefined function: %s",
                        errorCode.ordinal(),line,context));
                    break;
            case REDEF_VAR:
                System.out.println(String.format("Error type %d at Line %d: Redefined variable: %s",
                        errorCode.ordinal(),line,context));
                break;
            case ASSIGN_MISMATCH:
                System.out.println(String.format("Error type %d at Line %d: type.Type mismatched for assignment",
                        errorCode.ordinal(),line));
                break;
            case OP2_MISMATCH:
                System.out.println(String.format("Error type %d at Line %d: type.Type mismatched for operands",
                        errorCode.ordinal(),line));
                break;
            case FUNC_MISMATCH:
                System.out.println(String.format("Error type %d at Line %d: Function " +
                                "is not applicable for arguments.",
                        errorCode.ordinal(),line));
                break;
            case OP1_MISMATCH:
                System.out.println(String.format("Error type %d at Line %d: Not an array: %s",
                        errorCode.ordinal(),line,context));
                break;
            case FUNC_FOR_VAR:
                System.out.println(String.format("Error type %d at Line %d: Not an function: %s",
                        errorCode.ordinal(),line,context));
                break;
            case ASSIGN_FOR_FUNC:
                System.out.println(String.format("Error type %d at Line %d: The left-hand side of an " +
                                "assignment must be a variable.",
                        errorCode.ordinal(),line));
                break;
            case RETURN_MISMATCH:
                System.out.println(String.format("Error type %d at Line %d: " +
                                "type.Type mismatched for return.",
                        errorCode.ordinal(),line));
                break;
            default:
                System.out.println(String.format("Error type %d at Line %d:  %s",
                        errorCode.ordinal(),line,context));
        }
    }
}
