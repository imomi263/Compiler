package Lab3;

public class ArrayType extends Type{
    public final static Type type=new ArrayType(null,0);
    private Type contained; // type of its elements, may be int or array
    private int num_elements;


    private int level; // a[2][2]
    public ArrayType(Type contained, int num_elements) {
        this.contained=contained;
        this.num_elements=num_elements;
        this.level=1;
    }
    public ArrayType(Type contained, int num_elements , int level) {
        this.contained=contained;
        this.num_elements=num_elements;
        this.level=level;
    }
    public int getLevel(){
        return this.level;
    }


}
