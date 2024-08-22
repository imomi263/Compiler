package Lab3;

import java.util.*;
public class FunctionType extends Type{
    public final static Type type=new FunctionType(null,null);
    private Type retType;

    private ArrayList<Type>params=new ArrayList<Type>();

    public FunctionType(Type retTy,ArrayList<Type>params){
        this.retType=retTy;
        if(params!=null){
            this.params.addAll(params);
        }

    }
    public ArrayList<Type> getParamsList(){
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        return getClass()==obj.getClass();
    }

    public Type getRetType(){
        return retType;
    }
}
