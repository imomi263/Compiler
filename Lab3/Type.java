package Lab3;



public abstract  class Type {
    @Override
    public boolean equals(Object obj) {
        return getClass()==obj.getClass();
    }
}
