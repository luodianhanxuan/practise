package com.wangjg.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface Test {
    Test INSTANCE = Mappers.getMapper(Test.class);
    HashMap<String,String> toMap(TestObj obj);
    TestObj toObj(Map<String,String> map);
}
