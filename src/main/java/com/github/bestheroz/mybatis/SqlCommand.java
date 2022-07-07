package com.github.bestheroz.mybatis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.lang.NonNull;

@Slf4j
public class SqlCommand {
  public static final String SELECT_ITEMS = "getDistinctAndTargetItemsByMapOrderBy";
  public static final String SELECT_ITEM_BY_MAP = "getItemByMap";
  public static final String COUNT_BY_MAP = "countByMap";
  public static final String SELECT_ITEMS_BY_DATATABLE = "getItemsForDataTable";
  public static final String COUNT_BY_DATATABLE = "countForDataTable";
  public static final String INSERT = "insert";
  public static final String INSERT_BATCH = "insertBatch";
  public static final String UPDATE_MAP_BY_MAP = "updateMapByMap";
  public static final String DELETE_BY_MAP = "deleteByMap";
  private static final String TABLE_COLUMN_NAME_CREATED_BY = "CREATED_BY"; // no use "-"
  private static final String TABLE_COLUMN_NAME_CREATED = "CREATED"; // no use "-"
  private static final String TABLE_COLUMN_NAME_UPDATED_BY = "UPDATED_BY"; // no use "-"
  private static final String TABLE_COLUMN_NAME_UPDATED = "UPDATED"; // no use "-"
  private static final String VARIABLE_NAME_CREATED_BY = "createdBy"; // no use "-"
  private static final String VARIABLE_NAME_CREATED = "created"; // no use "-"
  private static final String VARIABLE_NAME_UPDATED_BY = "updatedBy"; // no use "-"
  private static final String VARIABLE_NAME_UPDATED = "updated"; // no use "-"
  private static final String SYSDATE = "NOW()";
  public static final Set<String> EXCLUDE_FIELD_SET =
      ImmutableSet.of(
          "SERIAL_VERSION_U_I_D", "serialVersionUID", "E_N_C_R_Y_P_T_E_D__C_O_L_U_M_N__L_I_S_T");
  private static final Set<String> METHOD_LIST =
      ImmutableSet.of(
          SELECT_ITEMS,
          SELECT_ITEMS_BY_DATATABLE,
          SELECT_ITEM_BY_MAP,
          COUNT_BY_MAP,
          COUNT_BY_DATATABLE,
          INSERT,
          UPDATE_MAP_BY_MAP,
          DELETE_BY_MAP);

  public String getTableName() {
    return getTableName(this.getEntityClass().getSimpleName());
  }

  public static String getTableName(final String javaClassName) {
    return StringUtils.lowerCase(ConverterUtils.getCamelCaseToSnakeCase(javaClassName));
  }

  private void verifyWhereKey(final Map<String, Object> whereConditions) {
    if (whereConditions == null || whereConditions.size() < 1) {
      log.warn("whereConditions is empty");
      throw new RuntimeException("No data Found");
    }
  }

  public String countByMap(final Map<String, Object> whereConditions) {
    final SQL sql = new SQL();
    sql.SELECT("COUNT(1) AS CNT").FROM(this.getTableName());
    this.getWhereSql(sql, whereConditions);
    log.debug(sql.toString());
    return sql.toString();
  }

  private Class<?> getEntityClass() {
    final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    final Optional<StackTraceElement> stackTraceElements =
        Arrays.stream(stackTrace)
            .filter(
                item -> {
                  try {
                    return (METHOD_LIST.contains(item.getMethodName())
                        && Class.forName(item.getClassName()).getInterfaces().length > 0
                        && Class.forName(item.getClassName())
                                .getInterfaces()[0]
                                .getGenericInterfaces()
                                .length
                            > 0);
                  } catch (final ClassNotFoundException e) {
                    log.warn("Failed to getEntityClass");
                    throw new RuntimeException("Failed to getEntityClass");
                  }
                })
            .findFirst();
    if (!stackTraceElements.isPresent()) {
      log.warn("stackTraceElements is Empty()");
      throw new RuntimeException("stackTraceElements is Empty()");
    }
    final StackTraceElement item = stackTraceElements.get();
    try {
      final Class<?> cInterface = Class.forName(item.getClassName()).getInterfaces()[0];
      return Class.forName(
          StringUtils.substringBetween(
              cInterface.getGenericInterfaces()[0].getTypeName(), "<", ">"));
    } catch (final ClassNotFoundException e) {
      log.warn("Failed to getEntityClass");
      throw new RuntimeException("Failed to getEntityClass");
    }
  }

  // ordered list required
  private <T> Set<String> getEntityFields(final T entity) {
    return Stream.concat(
            Arrays.stream(entity.getClass().getDeclaredFields()).map(Field::getName),
            Arrays.stream(entity.getClass().getSuperclass().getDeclaredFields())
                .map(Field::getName))
        .distinct()
        .filter(fieldName -> !EXCLUDE_FIELD_SET.contains(fieldName))
        .collect(Collectors.toSet());
  }

  private Set<String> getEntityFields() {
    final Class<?> tClass = this.getEntityClass();
    return Stream.concat(
            Arrays.stream(tClass.getDeclaredFields()).map(Field::getName),
            Arrays.stream(tClass.getSuperclass().getDeclaredFields()).map(Field::getName))
        .distinct()
        .filter(fieldName -> !EXCLUDE_FIELD_SET.contains(fieldName))
        .collect(Collectors.toSet());
  }

  public String getDistinctAndTargetItemsByMapOrderBy(
      final Set<String> distinctColumns,
      final Set<String> targetColumns,
      final Map<String, Object> whereConditions,
      final List<String> orderByConditions) {
    final SQL sql = new SQL();
    if (distinctColumns.isEmpty() && targetColumns.isEmpty()) {
      for (final String targetColumn : this.getEntityFields()) {
        sql.SELECT(this.wrapIdentifier(ConverterUtils.getCamelCaseToSnakeCase(targetColumn)));
      }
    } else {
      for (final String distinctColumn : distinctColumns) {
        sql.SELECT_DISTINCT(
            this.wrapIdentifier(ConverterUtils.getCamelCaseToSnakeCase(distinctColumn)));
      }
      for (final String targetColumn : targetColumns) {
        if (!distinctColumns.contains(targetColumn)) {
          sql.SELECT(this.wrapIdentifier(ConverterUtils.getCamelCaseToSnakeCase(targetColumn)));
        }
      }
    }
    sql.FROM(this.getTableName());
    this.getWhereSql(sql, whereConditions);
    for (final String orderByCondition : orderByConditions) {
      final String column = ConverterUtils.getCamelCaseToSnakeCase(orderByCondition);
      sql.ORDER_BY(
          StringUtils.startsWith(column, "-")
              ? this.wrapIdentifier(StringUtils.remove(column, "-")) + " desc"
              : this.wrapIdentifier(column));
    }
    log.debug(sql.toString());
    return sql.toString();
  }

  public String getItemByMap(final Map<String, Object> whereConditions) {
    this.verifyWhereKey(whereConditions);
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), ImmutableSet.of(), whereConditions, ImmutableList.of());
  }

  public <T> String insert(@NonNull final T entity) {
    final SQL sql = new SQL();
    sql.INSERT_INTO(getTableName(entity.getClass().getSimpleName()));
    ConverterUtils.toMap(entity).entrySet().stream()
        .filter(
            item ->
                !StringUtils.equalsAny(
                        item.getKey(),
                        VARIABLE_NAME_CREATED,
                        VARIABLE_NAME_CREATED_BY,
                        VARIABLE_NAME_UPDATED,
                        VARIABLE_NAME_UPDATED_BY)
                    && !EXCLUDE_FIELD_SET.contains(item.getKey()))
        .forEach(
            item ->
                sql.VALUES(
                    this.wrapIdentifier(ConverterUtils.getCamelCaseToSnakeCase(item.getKey())),
                    this.getFormattedValue(item.getValue())));

    final Set<String> fieldNames = this.getEntityFields(entity);

    if (fieldNames.contains(VARIABLE_NAME_CREATED)) {
      sql.VALUES(this.wrapIdentifier(TABLE_COLUMN_NAME_CREATED), SYSDATE);
    }
    if (fieldNames.contains(VARIABLE_NAME_UPDATED)) {
      sql.VALUES(this.wrapIdentifier(TABLE_COLUMN_NAME_UPDATED), SYSDATE);
    }
    if (fieldNames.contains(VARIABLE_NAME_CREATED_BY)) {
      // FIXME: getUserID()
      sql.VALUES(this.wrapIdentifier(TABLE_COLUMN_NAME_CREATED_BY), "1004");
    }
    if (fieldNames.contains(VARIABLE_NAME_UPDATED_BY)) {
      // FIXME: getUserID()
      sql.VALUES(this.wrapIdentifier(TABLE_COLUMN_NAME_UPDATED_BY), "1004");
    }

    log.debug(sql.toString());
    return sql.toString();
  }

  public <T> String insertBatch(@NonNull final List<T> entities) {
    if (entities.size() < 1) {
      log.warn("entities empty");
      throw new RuntimeException("entities empty");
    }
    final SQL sql = new SQL();
    sql.INSERT_INTO(this.wrapIdentifier(getTableName(entities.get(0).getClass().getSimpleName())));
    final Set<String> columns = this.getEntityFields(entities.get(0));

    sql.INTO_COLUMNS(
        columns.stream()
            .filter(item -> !EXCLUDE_FIELD_SET.contains(item))
            .map(str -> this.wrapIdentifier(ConverterUtils.getCamelCaseToSnakeCase(str)))
            .collect(Collectors.joining(", ")));

    final List<ArrayList<String>> valuesList =
        entities.stream()
            .map(ConverterUtils::toMap)
            .map(
                entity ->
                    new ArrayList<>(
                        columns.stream()
                            .map(
                                column -> {
                                  if (StringUtils.equalsAny(
                                      column, VARIABLE_NAME_CREATED, VARIABLE_NAME_UPDATED)) {
                                    return SYSDATE;
                                  } else if (StringUtils.equalsAny(
                                      column, VARIABLE_NAME_CREATED_BY, VARIABLE_NAME_UPDATED_BY)) {
                                    // FIXME: getUserID()
                                    return "1004";
                                  } else {
                                    return this.getFormattedValue(entity.get(column));
                                  }
                                })
                            .collect(Collectors.toList())))
            .collect(Collectors.toList());
    sql.INTO_VALUES(
        valuesList.stream()
            .map(value -> StringUtils.join(value, ", "))
            .collect(Collectors.joining("), (")));
    log.debug(sql.toString());
    return sql.toString();
  }

  public String updateMapByMap(
      final Map<String, Object> updateMap, final Map<String, Object> whereConditions) {
    this.verifyWhereKey(whereConditions);

    final SQL sql = new SQL();
    sql.UPDATE(this.getTableName());
    updateMap.forEach(
        (javaFieldName, value) -> {
          if (!StringUtils.equalsAny(
                  javaFieldName,
                  VARIABLE_NAME_CREATED_BY,
                  VARIABLE_NAME_CREATED,
                  VARIABLE_NAME_UPDATED,
                  VARIABLE_NAME_UPDATED_BY)
              && !EXCLUDE_FIELD_SET.contains(javaFieldName)) {
            sql.SET(this.getEqualSql(ConverterUtils.getCamelCaseToSnakeCase(javaFieldName), value));
          }
        });

    this.getWhereSql(sql, whereConditions);
    this.getUpdateSetSql(sql, this.getEntityFields());
    if (!StringUtils.containsIgnoreCase(sql.toString(), "WHERE ")) {
      log.warn("whereConditions is empty");
      throw new RuntimeException("whereConditions is empty");
    }
    log.debug(sql.toString());
    return sql.toString();
  }

  private void getUpdateSetSql(final SQL sql, final Set<String> fieldNames) {
    if (fieldNames.contains(VARIABLE_NAME_UPDATED)) {
      sql.SET(this.wrapIdentifier(TABLE_COLUMN_NAME_UPDATED) + " = " + SYSDATE);
    }
    if (fieldNames.contains(VARIABLE_NAME_UPDATED_BY)) {
      sql.SET(
          // FIXME: getUserID()
          MessageFormat.format("`{0}` = ''{1}''", TABLE_COLUMN_NAME_UPDATED_BY, "1004"));
    }
  }

  public String deleteByMap(final Map<String, Object> whereConditions) {
    this.verifyWhereKey(whereConditions);
    final SQL sql = new SQL();
    sql.DELETE_FROM(this.getTableName());
    this.getWhereSql(sql, whereConditions);
    this.requiredWhereConditions(sql);
    log.debug(sql.toString());
    return sql.toString();
  }

  private void requiredWhereConditions(final SQL sql) {
    if (!StringUtils.containsIgnoreCase(sql.toString(), "WHERE ")) {
      log.warn("whereConditions is empty");
      throw new RuntimeException("whereConditions is empty");
    }
  }

  private String getWhereString(
      final String conditionType, final String dbColumnName, final Object value) {
    switch (conditionType) {
      case "eq":
      default:
        return this.getEqualSql(dbColumnName, value);
      case "ne":
        return MessageFormat.format("`{0}` <> {1}", dbColumnName, this.getFormattedValue(value));
      case "in":
        {
          if (value instanceof List) {
            log.warn("conditionType 'in' is require Set");
            throw new RuntimeException(
                "conditionType 'in' is require Set, yours : " + value.getClass());
          }
          final Set<?> values = (Set<?>) value;
          if (values.isEmpty()) {
            log.warn("WHERE - empty in cause : {}", dbColumnName);
            throw new RuntimeException("WHERE - empty in cause : " + dbColumnName);
          }
          return MessageFormat.format(
              "`{0}` IN ({1})",
              dbColumnName,
              values.stream().map(this::getFormattedValue).collect(Collectors.joining(", ")));
        }
      case "notIn":
        if (value instanceof List) {
          log.warn("conditionType 'notIn' is require Set");
          throw new RuntimeException(
              "conditionType 'notIn' is require Set, yours: " + value.getClass());
        }
        final Set<?> values = (Set<?>) value;
        if (values.isEmpty()) {
          log.warn("WHERE - empty in cause : {}", dbColumnName);
          throw new RuntimeException("WHERE - empty in cause : " + dbColumnName);
        }
        return MessageFormat.format(
            "`{0}` NOT IN ({1})",
            dbColumnName,
            values.stream().map(this::getFormattedValue).collect(Collectors.joining(", ")));
      case "null":
        return MessageFormat.format("`{0}` IS NULL", dbColumnName);
      case "notNull":
        return MessageFormat.format("`{0}` IS NOT NULL", dbColumnName);
      case "contains":
        return MessageFormat.format(
            "INSTR(`{0}`, {1}) > 0", dbColumnName, this.getFormattedValue(value));
      case "notContains":
        return MessageFormat.format(
            "INSTR(`{0}`, {1}) = 0", dbColumnName, this.getFormattedValue(value));
      case "startsWith":
        return MessageFormat.format(
            "INSTR(`{0}`, {1}) = 1", dbColumnName, this.getFormattedValue(value));
      case "endsWith":
        return MessageFormat.format(
            "RIGHT(`{0}`, CHAR_LENGTH({1})) = {1}", dbColumnName, this.getFormattedValue(value));
      case "lt":
        return MessageFormat.format("`{0}` < {1}", dbColumnName, this.getFormattedValue(value));
      case "lte":
        return MessageFormat.format("`{0}` <= {1}", dbColumnName, this.getFormattedValue(value));
      case "gt":
        return MessageFormat.format("`{0}` > {1}", dbColumnName, this.getFormattedValue(value));
      case "gte":
        return MessageFormat.format("`{0}` >= {1}", dbColumnName, this.getFormattedValue(value));
    }
  }

  private String getEqualSql(final String dbColumnName, final Object value) {
    return MessageFormat.format("`{0}` = {1}", dbColumnName, this.getFormattedValue(value));
  }

  private String getFormattedValue(final Object value) {
    if (value == null) {
      return "null";
    } else if (value instanceof String) {
      final String str = (String) value;
      if (this.isISO8601String(str)) {
        return "'"
            + ConverterUtils.converterInstantToString(Instant.parse(str), "yyyy-MM-dd HH:mm:ss.SSS")
            + "'";
        // FIXME: MYSQL 사용시 주석을 풀어 위에 코드를 대체해주세요.
        //        return MessageFormat.format(
        //            "FROM_UNIXTIME({0,number,#})",
        //            Integer.parseInt(String.valueOf(Instant.parse(str).toEpochMilli() / 1000)));
      } else {
        return "'" + str.replaceAll("'", "''") + "'";
      }
    } else if (value instanceof Instant) {
      final Instant instant = (Instant) value;
      return "'"
          + ConverterUtils.converterInstantToString(instant, "yyyy-MM-dd HH:mm:ss.SSS")
          + "'";
      // FIXME: MYSQL 사용시 주석을 풀어 위에 코드를 대체해주세요.
      //      MYSQL
      //      return MessageFormat.format(
      //          "FROM_UNIXTIME({0,number,#})",
      //          Integer.parseInt(String.valueOf((instant).toEpochMilli() / 1000)));
    } else {
      return value.toString().replaceAll("'", "''");
    }
  }

  private void getWhereSql(final SQL sql, final Map<String, Object> whereConditions) {
    whereConditions.forEach(
        (key, value) -> {
          final String columnName = StringUtils.substringBefore(key, ":");
          final String conditionType =
              StringUtils.defaultString(StringUtils.substringAfter(key, ":"), "eq");
          sql.WHERE(
              this.getWhereString(
                  conditionType, ConverterUtils.getCamelCaseToSnakeCase(columnName), value));
        });
  }

  private String wrapIdentifier(final String identifier) {
    return "`" + identifier + "`";
  }

  private boolean isISO8601String(final String value) {
    return StringUtils.countMatches(value, '-') == 2
        && StringUtils.countMatches(value, ':') == 2
        && StringUtils.countMatches(value, 'T') == 1
        && (StringUtils.endsWith(value, "Z") || StringUtils.countMatches(value, '+') == 1);
  }
}
