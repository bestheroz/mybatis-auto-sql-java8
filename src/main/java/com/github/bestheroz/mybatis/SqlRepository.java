package com.github.bestheroz.mybatis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface SqlRepository<T extends Serializable> {
  default List<T> getItems() {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), ImmutableSet.of(), ImmutableMap.of(), ImmutableList.of());
  }

  default List<T> getItemsOrderBy(final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), ImmutableSet.of(), ImmutableMap.of(), orderByConditions);
  }

  default List<T> getItemsByMap(final Map<String, Object> whereConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), ImmutableSet.of(), whereConditions, ImmutableList.of());
  }

  default List<T> getItemsByMapOrderBy(
      final Map<String, Object> whereConditions, final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), ImmutableSet.of(), whereConditions, orderByConditions);
  }

  default List<T> getDistinctItems(final Set<String> distinctColumns) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        distinctColumns, ImmutableSet.of(), ImmutableMap.of(), ImmutableList.of());
  }

  default List<T> getDistinctItemsOrderBy(
      final Set<String> distinctColumns, final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        distinctColumns, ImmutableSet.of(), ImmutableMap.of(), orderByConditions);
  }

  default List<T> getDistinctItemsByMap(
      final Set<String> distinctColumns, final Map<String, Object> whereConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        distinctColumns, ImmutableSet.of(), whereConditions, ImmutableList.of());
  }

  default List<T> getDistinctItemsByMapOrderBy(
      final Set<String> distinctColumns,
      final Map<String, Object> whereConditions,
      final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        distinctColumns, ImmutableSet.of(), whereConditions, orderByConditions);
  }

  // Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
  default List<T> getTargetItems(final Set<String> targetColumns) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), targetColumns, ImmutableMap.of(), ImmutableList.of());
  }

  // Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
  default List<T> getTargetItemsOrderBy(
      final Set<String> targetColumns, final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), targetColumns, ImmutableMap.of(), orderByConditions);
  }

  // Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
  default List<T> getTargetItemsByMap(
      final Set<String> targetColumns, final Map<String, Object> whereConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), targetColumns, whereConditions, ImmutableList.of());
  }

  // Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
  default List<T> getTargetItemsByMapOrderBy(
      final Set<String> targetColumns,
      final Map<String, Object> whereConditions,
      final List<String> orderByConditions) {
    return this.getDistinctAndTargetItemsByMapOrderBy(
        ImmutableSet.of(), targetColumns, whereConditions, orderByConditions);
  }

  @SelectProvider(type = SqlCommand.class, method = SqlCommand.SELECT_ITEMS)
  List<T> getDistinctAndTargetItemsByMapOrderBy(
      final Set<String> distinctColumns,
      final Set<String> targetColumns,
      final Map<String, Object> whereConditions,
      final List<String> orderByConditions);

  @SelectProvider(type = SqlCommand.class, method = SqlCommand.SELECT_ITEM_BY_MAP)
  Optional<T> getItemByMap(final Map<String, Object> whereConditions);

  default Optional<T> getItemById(final Long id) {
    return this.getItemByMap(ImmutableMap.of("id", id));
  }

  default int countAll() {
    return this.countByMap(ImmutableMap.of());
  }

  @SelectProvider(type = SqlCommand.class, method = SqlCommand.COUNT_BY_MAP)
  int countByMap(final Map<String, Object> whereConditions);

  @InsertProvider(type = SqlCommand.class, method = SqlCommand.INSERT)
  // @SelectKey(statement = "SELECT SEQSEQSEQSEQ.NEXTVAL FROM DUAL", keyProperty = "seq", before =
  // true, resultType = Long.class)
  // insert 가 되고 나서 pk 값을 동기화하여 저장한다. pk 값이 다르다면 아래 id 를 수정
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(final T entity);

  @InsertProvider(type = SqlCommand.class, method = SqlCommand.INSERT_BATCH)
  // insert 가 되고 나서 pk 값을 동기화하여 저장한다. pk 값이 다르다면 아래 id 를 수정
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertBatch(final List<T> entities);

  default void updateById(final T entity, final Long id) {
    this.updateMapByMap(ConverterUtils.toMap(entity), ImmutableMap.of("id", id));
  }

  default void updateByMap(final T entity, final Map<String, Object> whereConditions) {
    this.updateMapByMap(ConverterUtils.toMap(entity), whereConditions);
  }

  @UpdateProvider(type = SqlCommand.class, method = SqlCommand.UPDATE_MAP_BY_MAP)
  void updateMapByMap(
      final Map<String, Object> updateMap, final Map<String, Object> whereConditions);

  default void updateMapById(final Map<String, Object> updateMap, final Long id) {
    this.updateMapByMap(updateMap, ImmutableMap.of("id", id));
  }

  @DeleteProvider(type = SqlCommand.class, method = SqlCommand.DELETE_BY_MAP)
  void deleteByMap(final Map<String, Object> whereConditions);

  default void deleteById(final Long id) {
    this.deleteByMap(ImmutableMap.of("id", id));
  }
}
