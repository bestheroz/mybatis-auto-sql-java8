package com.github.bestheroz.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
@UtilityClass
public class ConverterUtils {
  private final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper()
          .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public String toString(final Object source) {
    try {
      return OBJECT_MAPPER.writeValueAsString(source);
    } catch (final JsonProcessingException e) {
      log.warn(ExceptionUtils.getStackTrace(e));
      throw new RuntimeException(e);
    }
  }

  public Map<String, Object> toMap(final Object source) {
    final Map<String, Object> map = new HashMap<>();
    final Field[] fields =
        ArrayUtils.addAll(
            source.getClass().getDeclaredFields(),
            source.getClass().getSuperclass().getDeclaredFields());
    for (final Field field : fields) {
      field.setAccessible(true);
      try {
        map.put(field.getName(), field.get(source));
      } catch (final Exception e) {
        log.warn(ExceptionUtils.getStackTrace(e));
      }
    }
    return map;
  }

  public String converterInstantToString(final Instant instant, final String pattern) {
    return OffsetDateTime.ofInstant(instant, ZoneId.of("UTC"))
        .format(DateTimeFormatter.ofPattern(pattern));
  }

  public String getCamelCaseToSnakeCase(final String str) {
    final StringBuilder result = new StringBuilder(str.length() * 2);
    result.append(Character.toLowerCase(str.charAt(0)));
    for (int i = 1; i < str.length(); i++) {
      final char ch = str.charAt(i);
      if (Character.isUpperCase(ch)) {
        result.append('_').append(Character.toLowerCase(ch));
      } else {
        result.append(ch);
      }
    }
    return result.toString();
  }
}
