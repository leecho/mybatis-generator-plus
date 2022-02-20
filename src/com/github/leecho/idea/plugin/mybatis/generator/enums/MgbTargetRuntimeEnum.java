package com.github.leecho.idea.plugin.mybatis.generator.enums;

public enum MgbTargetRuntimeEnum {
  MY_BATIS3_DYNAMIC_SQL("MyBatis3DynamicSql","动态sql"),
  MY_BATIS3_KOTLIN("MyBatis3Kotlin","支持Kotlin语言"),
  MY_BATIS3("MyBatis3","最基础的"),
  MY_BATIS3_SIMPLE("MyBatis3Simple","简单的（没有example）"),
  ;
  private String name;
  private String desc;

  MgbTargetRuntimeEnum(String name, String desc) {
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
}
