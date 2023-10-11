package com.github.leecho.idea.plugin.mybatis.generator.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum MbgJavaClientConfigTypeEnum {
  MAPPER("MAPPER", "包含 Mapper 接口和 XML 文件，不包含注解"),
  XMLMAPPER("XMLMAPPER", "包含 Mapper 接口和 XML 文件，不包含注解"),
  ANNOTATEDMAPPER("ANNOTATEDMAPPER", "包含 Mapper 接口和 SqlProvider 辅助类，全注解，不包含 XML 文件"),
  MIXEDMAPPER("MIXEDMAPPER", "包含 Mapper 接口和 XML 文件，简单的 CRUD 使用注解，高级条件查询使用 XML 文件"),
  ;
  private String name;
  private String desc;

  MbgJavaClientConfigTypeEnum(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static List<String> getValuesByTargetRuntime(MbgTargetRuntimeEnum runtimeEnum) {
    List<String> list = new ArrayList<>();
    switch (runtimeEnum) {
      case MY_BATIS3_DYNAMIC_SQL:
      case MY_BATIS3_KOTLIN:
        break;
      case MY_BATIS3:
        list = Arrays.stream(MbgJavaClientConfigTypeEnum.values())
            .map(MbgJavaClientConfigTypeEnum::getName).collect(
                Collectors.toList());
        break;
      case MY_BATIS3_SIMPLE:
        list = Arrays.stream(MbgJavaClientConfigTypeEnum.values())
            .filter(i -> !Objects.equals(i, MbgJavaClientConfigTypeEnum.MIXEDMAPPER))
            .map(MbgJavaClientConfigTypeEnum::getName).collect(
                Collectors.toList());
        break;
    }
    return list;
  }
}
