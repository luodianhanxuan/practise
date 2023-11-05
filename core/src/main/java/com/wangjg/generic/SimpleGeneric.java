package com.wangjg.generic;


public class SimpleGeneric<T> {

    private T f1;

    public T getF1() {
        return f1;
    }

    public void setF1(T f1) {
        this.f1 = f1;
    }

    public static <AnotherGeneric> AnotherGeneric echo(AnotherGeneric anotherGeneric){
        return anotherGeneric;
    }

    public static <ObjectType> TypeReference<ObjectType> getTypeReference(TypeReference<ObjectType> typeReference) {
        return typeReference;
    }

    public TypeReference<T> getTypeReference() {
        return new TypeReference<T>(){};
    }

}
