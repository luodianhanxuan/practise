package com.wangjg.generic;

public class TestGenericChild extends SimpleGeneric<String> {

    public static void main(String[] args) {
        System.out.println(SimpleGeneric.echo("test"));
        TypeReference<String> typeReference = SimpleGeneric.getTypeReference(new TypeReference<String>() {
        });
        System.out.println(typeReference);

        TestGenericChild testGenericChild = new TestGenericChild();
        System.out.println(testGenericChild.getTypeReference());
    }
}
