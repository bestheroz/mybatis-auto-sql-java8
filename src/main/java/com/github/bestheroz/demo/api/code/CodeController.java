package com.github.bestheroz.demo.api.code;

import com.github.bestheroz.demo.Result;
import com.github.bestheroz.demo.entity.Code;
import com.github.bestheroz.demo.repository.CodeRepository;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/")
@RequiredArgsConstructor
public class CodeController {
  private final CodeRepository codeRepository;

  @GetMapping("get-items/")
  @Operation(
      summary = "List<T> getItems()",
      tags = "Get (All-column) items",
      description = CodeApiDescription.GET_ITEMS)
  ResponseEntity<List<CodeDTO>> getItems() {
    return Result.ok(
        this.codeRepository.getItems().stream().map(CodeDTO::new).collect(Collectors.toList()));
  }

  @GetMapping("get-items-order-by/")
  @Operation(
      summary = "List<T> getItemsOrderBy(final List<String> orderByConditions)",
      tags = "Get (All-column) items",
      description = CodeApiDescription.GET_ITEMS_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getItemsOrderBy(
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    return Result.ok(
        this.codeRepository.getItemsOrderBy(orderByConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-items-by-map/")
  @Operation(
      summary = "List<T> getItemsByMap(final Map<String, Object> whereConditions)",
      tags = "Get (All-column) items",
      description = CodeApiDescription.GET_ITEMS_BY_MAP)
  ResponseEntity<List<CodeDTO>> getItemsByMap(
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions) {
    return Result.ok(
        this.codeRepository.getItemsByMap(whereConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-items-by-map-order-by/")
  @Operation(
      summary =
          "List<T> getItemsByMapOrderBy(final Map<String, Object> whereConditions, final List<String> orderByConditions)",
      tags = "Get (All-column) items",
      description = CodeApiDescription.GET_ITEMS_BY_MAP_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getItemsByMapOrderBy(
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions,
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    whereConditions.remove("orderByConditions");
    return Result.ok(
        this.codeRepository.getItemsByMapOrderBy(whereConditions, orderByConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-item-by-id")
  @Operation(
      summary = "Optional<T> getItemById(final Long id)",
      tags = "Get item",
      description = CodeApiDescription.GET_ITEM_BY_ID)
  ResponseEntity<CodeDTO> getItemById(@RequestParam @Schema(example = "7") final Long id) {
    return Result.ok(this.codeRepository.getItemById(id).map(CodeDTO::new).orElse(null));
  }

  @GetMapping("get-item-by-map")
  @Operation(
      summary = "Optional<T> getItemByMap(final Map<String, Object> whereConditions)",
      tags = "Get item",
      description = CodeApiDescription.GET_ITEM_BY_MAP)
  ResponseEntity<CodeDTO> getItemByMap(
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\",\"value\": \"E2\"}")
          final Map<String, Object> whereConditions) {
    return Result.ok(
        this.codeRepository.getItemByMap(whereConditions).map(CodeDTO::new).orElse(null));
  }

  @GetMapping("get-target-items/")
  @Operation(
      summary = "List<T> getTargetItems(final Set<String> targetColumns)",
      tags = "Get (Target-column) items",
      description = CodeApiDescription.GET_TARGET_ITEMS)
  ResponseEntity<List<CodeDTO>> getTargetItems(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(
                      example = "[\"type\", \"value\", \"text\"]",
                      implementation = String.class))
          final Set<String> targetColumns) {
    return Result.ok(
        this.codeRepository.getTargetItems(targetColumns).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-target-items-order-by/")
  @Operation(
      summary =
          "List<T> getTargetItemsOrderBy(final Set<String> targetColumns, final List<String> orderByConditions)",
      tags = "Get (Target-column) items",
      description = CodeApiDescription.GET_TARGET_ITEMS_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getTargetItemsOrderBy(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(
                      example = "[\"type\", \"value\", \"text\", \"displayOrder\"]",
                      implementation = String.class))
          final Set<String> targetColumns,
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    return Result.ok(
        this.codeRepository.getTargetItemsOrderBy(targetColumns, orderByConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-target-items-by-map/")
  @Operation(
      summary =
          "List<T> getTargetItemsByMap(final Set<String> targetColumns, final Map<String, Object> whereConditions)",
      tags = "Get (Target-column) items",
      description = CodeApiDescription.GET_TARGET_ITEMS_BY_MAP)
  ResponseEntity<List<CodeDTO>> getTargetItemsByMap(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(
                      example = "[\"type\", \"value\", \"text\", \"displayOrder\"]",
                      implementation = String.class))
          final Set<String> targetColumns,
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions) {
    whereConditions.remove("targetColumns");
    return Result.ok(
        this.codeRepository.getTargetItemsByMap(targetColumns, whereConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-target-items-by-map-order-by/")
  @Operation(
      summary =
          "List<T> getTargetItemsByMapOrderBy(final Set<String> targetColumns, final Map<String, Object> whereConditions, final List<String> orderByConditions)",
      tags = "Get (Target-column) items",
      description = CodeApiDescription.GET_TARGET_ITEMS_BY_MAP_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getTargetItemsByMapOrderBy(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(
                      example = "[\"type\", \"value\", \"text\", \"displayOrder\"]",
                      implementation = String.class))
          final Set<String> targetColumns,
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions,
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    whereConditions.remove("targetColumns");
    whereConditions.remove("orderByConditions");
    return Result.ok(
        this.codeRepository
            .getTargetItemsByMapOrderBy(targetColumns, whereConditions, orderByConditions)
            .stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("count-all")
  @Operation(summary = "int countAll()", tags = "Count", description = CodeApiDescription.COUNT_ALL)
  ResponseEntity<Integer> countAll() {
    return Result.ok(this.codeRepository.countAll());
  }

  @GetMapping("count-by-map")
  @Operation(
      summary = "int countByMap(final Map<String, Object> whereConditions)",
      tags = "Count",
      description = CodeApiDescription.COUNT_BY_MAP)
  ResponseEntity<Integer> countByMap(
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions) {
    return Result.ok(this.codeRepository.countByMap(whereConditions));
  }

  @GetMapping("get-distinct-items/")
  @Operation(
      summary = "List<T> getDistinctItems(final Set<String> distinctColumns)",
      tags = "Distinct",
      description = CodeApiDescription.GET_DISTINCT_ITEMS)
  ResponseEntity<List<CodeDTO>> getDistinctItems(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(example = "[\"type\", \"available\"]", implementation = String.class))
          final Set<String> distinctColumns) {
    return Result.ok(
        this.codeRepository.getDistinctItems(distinctColumns).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-distinct-items-order-by/")
  @Operation(
      summary =
          "List<T> getDistinctItemsOrderBy(final Set<String> distinctColumns, final List<String> orderByConditions)",
      tags = "Distinct",
      description = CodeApiDescription.GET_DISTINCT_ITEMS_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getDistinctItemsOrderBy(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(example = "[\"type\", \"available\"]", implementation = String.class))
          final Set<String> distinctColumns,
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    return Result.ok(
        this.codeRepository.getDistinctItemsOrderBy(distinctColumns, orderByConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-distinct-items-by-map/")
  @Operation(
      summary =
          "List<T> getDistinctItemsByMap(final Set<String> distinctColumns, final Map<String, Object> whereConditions)",
      tags = "Distinct",
      description = CodeApiDescription.GET_DISTINCT_ITEMS_BY_MAP)
  ResponseEntity<List<CodeDTO>> getDistinctItemsByMap(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(example = "[\"type\", \"available\"]", implementation = String.class))
          final Set<String> distinctColumns,
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions) {
    whereConditions.remove("distinctColumns");
    return Result.ok(
        this.codeRepository.getDistinctItemsByMap(distinctColumns, whereConditions).stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @GetMapping("get-distinct-items-by-map-order-by/")
  @Operation(
      summary =
          "List<T> getDistinctItemsByMapOrderBy(final Set<String> distinctColumns, final Map<String, Object> whereConditions, final List<String> orderByConditions)",
      tags = "Distinct",
      description = CodeApiDescription.GET_DISTINCT_ITEMS_BY_MAP_ORDER_BY)
  ResponseEntity<List<CodeDTO>> getDistinctItemsByMapOrderBy(
      @RequestParam
          @ArraySchema(
              arraySchema =
                  @Schema(example = "[\"type\", \"available\"]", implementation = String.class))
          final Set<String> distinctColumns,
      @RequestParam @Schema(example = "{\"type\": \"EXAMPLE\"}")
          final Map<String, Object> whereConditions,
      @RequestParam
          @ArraySchema(
              arraySchema = @Schema(example = "[\"-displayOrder\"]", implementation = String.class))
          final List<String> orderByConditions) {
    whereConditions.remove("distinctColumns");
    whereConditions.remove("orderByConditions");
    return Result.ok(
        this.codeRepository
            .getDistinctItemsByMapOrderBy(distinctColumns, whereConditions, orderByConditions)
            .stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @PostMapping
  @Operation(
      summary = "void insert(final T entity)",
      tags = "Insert",
      description = CodeApiDescription.INSERT)
  public ResponseEntity<CodeDTO> insert(@RequestBody @Valid final CodeInsertDTO payload) {
    final Code code = payload.toCode();
    this.codeRepository.insert(code);
    return Result.created(
        new CodeDTO(
            this.codeRepository
                .getItemById(code.getId())
                .orElseThrow(() -> new RuntimeException("No data Found"))));
  }

  @PostMapping(value = "insert-batch")
  @Operation(
      summary = "void insertBatch(final List<T> entities)",
      tags = "Insert",
      description = CodeApiDescription.INSERT_BATCH)
  public ResponseEntity<List<CodeDTO>> insertBatch(
      @ArraySchema(
              arraySchema =
                  @Schema(
                      example =
                          "[{\"type\": \"SAMPLE\", \"value\": \"S5\", \"text\": \"샘플5\", \"available\": true, \"displayOrder\": 10},"
                              + "{\"type\": \"SAMPLE\", \"value\": \"S6\", \"text\": \"샘플6\", \"available\": true, \"displayOrder\": 11},"
                              + "{\"type\": \"SAMPLE\", \"value\": \"S7\", \"text\": \"샘플7\", \"available\": true, \"displayOrder\": 12}]",
                      implementation = CodeInsertDTO.class))
          @RequestBody
          @Valid
          final List<CodeInsertDTO> payload) {
    final List<Code> codes =
        payload.stream().map(CodeInsertDTO::toCode).collect(Collectors.toList());
    this.codeRepository.insertBatch(codes);
    return Result.ok(
        this.codeRepository
            .getItemsByMap(
                ImmutableMap.of(
                    "id:in", codes.stream().map(Code::getId).collect(Collectors.toSet())))
            .stream()
            .map(CodeDTO::new)
            .collect(Collectors.toList()));
  }

  @PutMapping(value = "{id}")
  @Operation(
      summary = "void updateById(final T entity, final Long id)",
      tags = "Update",
      description = CodeApiDescription.UPDATE_BY_ID)
  public ResponseEntity<CodeDTO> updateById(
      @PathVariable(value = "id") final Long id, @RequestBody @Valid final CodeInsertDTO payload) {
    this.codeRepository.updateById(payload.toCode(), id);
    return Result.ok(
        this.codeRepository
            .getItemById(id)
            .map(CodeDTO::new)
            .orElseThrow(() -> new RuntimeException("No data Found")));
  }

  @PutMapping(value = "update-by-map")
  @Operation(
      summary = "void updateByMap(final T entity, final Map<String, Object> whereConditions)",
      tags = "Update",
      description = CodeApiDescription.UPDATE_BY_MAP)
  public ResponseEntity<?> updateByMap(
      @RequestBody @Valid final CodeInsertDTO payload,
      @RequestParam @Schema(example = "{\"displayOrder\": 3}")
          final Map<String, Object> whereConditions) {
    this.codeRepository.updateByMap(payload.toCode(), whereConditions);
    return Result.ok();
  }

  @PutMapping(value = "update-map-by-map")
  @Operation(
      summary =
          "updateMapByMap(final Map<String, Object> updateMap, final Map<String, Object> whereConditions)",
      tags = "Update",
      description = CodeApiDescription.UPDATE_MAP_BY_MAP)
  public ResponseEntity<?> updateMapByMap(
      @RequestParam @Schema(example = "{\"text\": \"예제3\"}") final Map<String, Object> updateMap,
      @RequestParam @Schema(example = "{\"displayOrder\": 3}")
          final Map<String, Object> whereConditions) {
    updateMap.remove("displayOrder");
    whereConditions.remove("text");
    this.codeRepository.updateMapByMap(updateMap, whereConditions);
    return Result.ok();
  }

  @PutMapping(value = "{id}/update-map-by-id")
  @Operation(
      summary = "void updateMapById(final Map<String, Object> updateMap, final Long id)",
      tags = "Update",
      description = CodeApiDescription.UPDATE_MAP_BY_ID)
  public ResponseEntity<?> updateMapById(
      @PathVariable(value = "id") final Long id,
      @RequestParam @Schema(example = "{\"text\": \"예제3\"}") final Map<String, Object> updateMap) {
    updateMap.remove("displayOrder");
    this.codeRepository.updateMapById(updateMap, id);
    return Result.ok();
  }

  @DeleteMapping(value = "delete-by-map")
  @Operation(
      summary = "deleteByMap(final Map<String, Object> whereConditions)",
      tags = "Delete",
      description = CodeApiDescription.DELETE_BY_MAP)
  public ResponseEntity<?> deleteByMap(
      @RequestParam @Schema(example = "{\"displayOrder\": 3}")
          final Map<String, Object> whereConditions) {
    this.codeRepository.deleteByMap(whereConditions);
    return Result.ok();
  }

  @DeleteMapping(value = "{id}")
  @Operation(
      summary = "void deleteById(final Long id)",
      tags = "Delete",
      description = CodeApiDescription.DELETE_BY_ID)
  public ResponseEntity<?> deleteById(@PathVariable(value = "id") final Long id) {
    this.codeRepository.deleteById(id);
    return Result.ok();
  }
}
