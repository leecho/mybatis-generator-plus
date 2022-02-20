package com.github.leecho.idea.plugin.mybatis.generator.enums;

public enum PackageTypeEnum {
  SOURCE_ROOT(0, "sourceRoot"),
  RESOURCE_ROOT(1, "resourceRoot"),
  BASE(2, "basePackage"),
  DOMAIN(3, "domainPackage"),
  MAPPER(4, "mapperPackage"),
  EXAMPLE(5, "examplePackage"),
  XML(6, "xmlPackage"),
  ;
  private Integer code;
  private String desc;

  PackageTypeEnum(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }


}
