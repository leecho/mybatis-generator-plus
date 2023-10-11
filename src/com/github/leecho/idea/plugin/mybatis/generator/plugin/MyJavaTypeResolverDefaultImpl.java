package com.github.leecho.idea.plugin.mybatis.generator.plugin;

import java.sql.Types;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

public class MyJavaTypeResolverDefaultImpl extends JavaTypeResolverDefaultImpl {

  @Override
  protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column,
      FullyQualifiedJavaType defaultType) {
    FullyQualifiedJavaType answer = defaultType;

    switch (column.getJdbcType()) {
      case Types.BIT:
        answer = calculateBitReplacement(column, defaultType);
        break;
      case Types.DATE:
        answer = calculateDateType(column, defaultType);
        break;
      case Types.DECIMAL:
      case Types.NUMERIC:
        answer = calculateBigDecimalReplacement(column, defaultType);
        break;
      case Types.TIME:
        answer = calculateTimeType(column, defaultType);
        break;
      case Types.TIMESTAMP:
        answer = calculateTimestampType(column, defaultType);
        break;
      case Types.TINYINT:
        answer = new FullyQualifiedJavaType("java.lang.Integer");
      default:
        break;
    }

    return answer;
  }
}
