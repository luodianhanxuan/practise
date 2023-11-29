package com.wangjg.freemarker;

import lombok.Getter;

@Getter
public enum DictEnum {
    ONE("1", "一"),
    TWO("2", "二"),
    ;

    private final String code;
    private final String name;
    DictEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        for (DictEnum dictEnum : DictEnum.values()) {
            if (dictEnum.getCode().equals(code)) {
                return dictEnum.getName();
            }
        }
        return null;
    }
}
