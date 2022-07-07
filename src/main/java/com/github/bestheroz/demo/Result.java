package com.github.bestheroz.demo;

import org.springframework.http.ResponseEntity;

public class Result {

  private Result() {}

  public static ResponseEntity<?> created() {
    return ResponseEntity.status(201).build();
  }

  public static <T> ResponseEntity<T> created(final T data) {
    return ResponseEntity.status(201).body(data);
  }

  public static ResponseEntity<?> ok() {
    return ResponseEntity.status(200).build();
  }

  public static <T> ResponseEntity<T> ok(final T data) {
    return ResponseEntity.status(200).body(data);
  }
}
