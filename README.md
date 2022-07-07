# mybatis-auto-sql-java8

**This project that help you not write simple sql when using mybatis.**

## Major Feature

- 메소드 호출만으로 기본적으로 자주 사용하는 쿼리를 생성하여 수행시켜줍니다.

- 조건문의 경우 `Equals(=)` 를 기본으로 제공하여 아래에 기능들도 제공
  - `equals(=)`
  - `not equals(!=)`
  - `in`
  - `not in`
  - `is null`
  - `is not null`
  - `contains(INSTR() > 0)`
  - `not contains(INSTR() = 0)`
  - `startsWith(INSTR() = 1)`
  - `endsWith(RIGHT())`
  - `lt(<)`
  - `lte (<=)`
  - `gt(>)`
  - `gte(>=)`
- Order by 기능 제공

- `created`, `createdBy`, `updated`, `updatedBy` 컬럼 값 자동 설정(Audit 지원)

## Usage

### Required

[Prodject for Java 17 (Click)](https://github.com/bestheroz/mybatis-auto-sql)

[Prodject for Java 11 (Click)](https://github.com/bestheroz/mybatis-auto-sql-java11)

[Prodject for Java 8 (Click)](https://github.com/bestheroz/mybatis-auto-sql-java8)

`mybatis`

`jackson` ( no gson, gson 이 필요하다면 구성가능 )

`commons-lang3`

`guava` (only for java 8)

#### 1. `com.github.bestheroz.mybatis` 하위의 3개의 클래스를 타겟 프로젝트에 원하는 위치에 복사하면 됩니다.

- `SqlRepository` - methods Interface
- `SqlCommand` - generate sql
- `ConverterUtils` - includes necessary utility methods of `SqlCommand`

#### 2. SqlCommand.java 내용 중 프로젝트에 맞게 변경

```java
private static final String TABLE_COLUMN_NAME_CREATED_BY="CREATED_BY"; // no use "-"
private static final String TABLE_COLUMN_NAME_CREATED="CREATED"; // no use "-"
private static final String TABLE_COLUMN_NAME_UPDATED_BY="UPDATED_BY"; // no use "-"
private static final String TABLE_COLUMN_NAME_UPDATED="UPDATED"; // no use "-"
private static final String VARIABLE_NAME_CREATED_BY="createdBy"; // no use "-"
private static final String VARIABLE_NAME_CREATED="created"; // no use "-"
private static final String VARIABLE_NAME_UPDATED_BY="updatedBy"; // no use "-"
private static final String VARIABLE_NAME_UPDATED="updated"; // no use "-"
private static final String SYSDATE="NOW()";
```

#### 3. DB Table 과 동일한 스펙을 가진 Entity 생성

##### DB Spec

```sql
CREATE TABLE code
(
  id            BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  type          VARCHAR(100)          NOT NULL,
  `value`       VARCHAR(100)          NOT NULL,
  text          VARCHAR(1000)         NOT NULL,
  available     BOOLEAN DEFAULT FALSE NOT NULL,
  display_order INT(3) NOT NULL,
  created_by    BIGINT(20) NOT NULL,
  created       DATETIME(6) NOT NULL,
  updated_by    BIGINT(20) NOT NULL,
  updated       DATETIME(6) NOT NULL
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;
```

##### Class spec

```java

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Code implements Serializable {

  @Serial
  private static final long serialVersionUID = -6076508411557466173L;

  private Long id;
  private String type;
  private String value;
  private String text;
  private Boolean available;
  private Integer displayOrder;

  protected Long createdBy;
  protected Instant created;
  protected Long updatedBy;
  protected Instant updated;
}
```

***테이블명과 클래스명도 동일하게 작성**

#### 4. Repository 작성

```java

@Mapper
@Repository
public interface CodeRepository extends SqlRepository<Code> {

}
```

#### 4-1. 추가적인 Repository 작성(필요시)

```java

@Mapper
@Repository
public interface CodeRepository extends SqlRepository<Code> {

  @InsertProvider(type = SqlCommand.class, method = SqlCommand.INSERT)
  // insert 가 되고 나서 pk 값을 동기화하여 저장한다. pk 값이 다르다면 아래 id 를 수정
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(final T entity);

  @InsertProvider(type = SqlCommand.class, method = SqlCommand.INSERT_BATCH)
  // insert 가 되고 나서 pk 값을 동기화하여 저장한다. pk 값이 다르다면 아래 id 를 수정
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insertBatch(final List<T> entities);

  @InsertProvider(type = SqlCommand.class, method = SqlCommand.INSERT)
  // Oracle 예제와 같이 sequence 에서 값을 가져와 id 를 우선 setId() 할때 사용
  @SelectKey(statement = "SELECT SEQ_CODE.NEXTVAL FROM DUAL", keyProperty = "id", before = true, resultType = Long.class)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(final T entity);
}
```

#### 5. Repository 메소드 호출

```java
List<Code> codes=this.codeRepository.getItemsByMap(Map.of("type","EXAMPLE"));

        List<Code> codes=this.codeRepository.getItemsByMapOrderBy(Map.of("type","EXAMPLE"),orderByConditions);

        List<Code> codes=this.codeRepository.getItemByMap(Map.of("type","EXAMPLE","value","E2"));

        List<Code> codes=this.codeRepository.getTargetItemsByMap(Set.of("type","value","text"),Map.of("type","EXAMPLE"));

        int count=this.codeRepository.countByMap(Map.of("type","EXAMPLE"));

        List<Code> codes=this.codeRepository.getDistinctItemsByMap(Set.of("type"),Map.of("type","EXAMPLE"));;

        this.codeRepository.insert(code);

        this.codeRepository.insertBatch(codes);

        this.codeRepository.updateByMap(payload.toCode(),Map.of("type","EXAMPLE","value","E2"));

        this.codeRepository.updateMapByMap(Map.of("text","예제2-1"),Map.of("type","EXAMPLE","value","E2"));

        this.codeRepository.deleteByMap(Map.of("type","EXAMPLE","value","E2"));

        this.codeRepository.deleteById(5L);
```

## Demo

Spring boot Run and enter http://localhost:8080/

## Supported methods

SqlRepository.java

```java
List<T> getItems();

        List<T> getItemsOrderBy(final List<String> orderByConditions);

        List<T> getItemsByMap(final Map<String, Object> whereConditions);

        List<T> getItemsByMapOrderBy(final Map<String, Object> whereConditions,final List<String> orderByConditions);

        List<T> getDistinctItems(final Set<String> distinctColumns);

        List<T> getDistinctItemsOrderBy(final Set<String> distinctColumns,final List<String> orderByConditions);

        List<T> getDistinctItemsByMap(final Set<String> distinctColumns,final Map<String, Object> whereConditions);

        List<T> getDistinctItemsByMapOrderBy(final Set<String> distinctColumns,final Map<String, Object> whereConditions,final List<String> orderByConditions);

// Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
        List<T> getTargetItems(final Set<String> targetColumns);

// Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
        List<T> getTargetItemsOrderBy(final Set<String> targetColumns,final List<String> orderByConditions);

// Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
        List<T> getTargetItemsByMap(final Set<String> targetColumns,final Map<String, Object> whereConditions)

// Target 시리즈를 사용하기 위해서는 Entity에 반드시 @NoArgsConstructor 가 필요하다
        List<T> getTargetItemsByMapOrderBy(
final Set<String> targetColumns,
final Map<String, Object> whereConditions,
final List<String> orderByConditions);

        List<T> getDistinctAndTargetItemsByMapOrderBy(
final Set<String> distinctColumns,
final Set<String> targetColumns,
final Map<String, Object> whereConditions,
final List<String> orderByConditions);

        Optional<T> getItemByMap(final Map<String, Object> whereConditions);

        Optional<T> getItemById(final Long id);

        int countAll();

        int countByMap(final Map<String, Object> whereConditions);

        void insert(final T entity);

        void insertBatch(final List<T> entities);

        void updateById(final T entity,final Long id);

        void updateByMap(final T entity,final Map<String, Object> whereConditions);

        void updateMapByMap(final Map<String, Object> updateMap,final Map<String, Object> whereConditions);

        void updateMapById(final Map<String, Object> updateMap,final Long id);

        void deleteByMap(final Map<String, Object> whereConditions);

        void deleteById(final Long id);
```

## Where conditions

- **default** `equals(=)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type", "EXAMPLE"));
this.codeRepository.getItemsByMap
(Map.of("type:eq", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` = 'EXAMPLE'
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder", 3));
this.codeRepository.updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:eq", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` = 3
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder", 3));
this.codeRepository.deleteByMap
(Map.of("displayOrder:eq", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` = 3
        )
```

- `not equals(!=)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:ne", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` <> 'EXAMPLE'
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:ne", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` <> 3
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:ne", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` <> 3
        )
```

- `in`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:in", Set.of("EXAMPLE", "SAMPLE")));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` IN ('EXAMPLE', 'SAMPLE')
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:in", Set.of(1, 3)));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` IN (1, 3)
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:in", Set.of(1, 3)));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` IN (1, 3)
        )
```

- `not in`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:notIn", Set.of("EXAMPLE", "SAMPLE")));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` NOT IN ('EXAMPLE', 'SAMPLE')
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:notIn", Set.of(1, 3)));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` NOT IN (1, 3)
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:notIn", Set.of(1, 3)));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` NOT IN (1, 3)
        )
```

- `is null`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:null", "anyValue"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` IS NULL
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:null", "anyValue"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` IS NULL
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:null", "anyValue"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` IS NULL
        )
```

- `is not null`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:notNull", "anyValue"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` IS NOT NULL
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:notNull", "anyValue"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` IS NOT NULL
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:notNull", "anyValue"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` IS NOT NULL
        )
```

- `contains(INSTR() > 0)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:contains", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') > 0
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("type:contains", "EXAMPLE"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') > 0
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("type:contains", "EXAMPLE"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') > 0
        )
```

- `not contains(INSTR() = 0)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:notContains", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 0
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("type:notContains", "EXAMPLE"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 0
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("type:notContains", "EXAMPLE"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 0
        )
```

- `startsWith(INSTR() = 1)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:startsWith", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 1
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("type:startsWith", "EXAMPLE"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 1
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("type:startsWith", "EXAMPLE"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `type` INSTR(`type`, 'EXAMPLE') = 1
        )
```

- `endsWith(RIGHT())`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("type:endsWith", "EXAMPLE"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        RIGHT(`type`, CHAR_LENGTH ('EXAMPLE')) = 'EXAMPLE'
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("type:endsWith", "EXAMPLE"));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        RIGHT(`type`, CHAR_LENGTH ('EXAMPLE')) = 'EXAMPLE'
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("type:endsWith", "EXAMPLE"));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        RIGHT(`type`, CHAR_LENGTH ('EXAMPLE')) = 'EXAMPLE'
        )
```

- `lt(<)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("displayOrder:lt", 3));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `display_order` < 3
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:lt", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` < '3'
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:lt", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` < '3'
        )
```

- `lte (<=)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("displayOrder:lte", 3));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `display_order` <= 3
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:lte", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` <= '3'
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:lte", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` <= '3'
        )
```

- `gt(>)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("displayOrder:gt", 3));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `display_order` < 3
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:gt", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` < '3'
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:gt", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` < '3'
        )
```

- `gte(>=)`

```sql
this
.
codeRepository
.
getItemsByMap
(Map.of("displayOrder:gte", 3));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
WHERE (
        `display_order` >= 3
        )
```

```sql
this
.
codeRepository
.
updateMapByMap
(Map.of("text", "예제3"), Map.of("displayOrder:gte", 3));
===
Executed SQL
===
UPDATE
  `code`
SET `text`       = '예제3',
    `UPDATED`    = NOW(),
    `UPDATED_BY` = '1004'
WHERE (
        `display_order` >= '3'
        )
```

```sql
this
.
codeRepository
.
deleteByMap
(Map.of("displayOrder:gte", 3));
===
Executed SQL
===
DELETE
FROM `code`
WHERE (
        `display_order` >= '3'
        )
```

## Order by

> Order by 단독으로도 사용가능하고 Where conditions 와 조합하여 사용할 수 있습니다.

```sql
this
.
codeRepository
.
getItemsOrderBy
(List.of("-displayOrder", "value"));
===
Executed SQL
===
SELECT `updated_by`,
       `created_by`,
       `created`,
       `available`,
       `display_order`,
       `id`,
       `text`,
       `type`,
       `value`,
       `updated`
FROM `code`
ORDER BY `display_order` desc, `value` asc
```

## 여담

코드가 깔끔하지 않음을 알고 있으며 계속 개선하겠습니다.

그리고 Swagger UI 를 위한 Contoller 는 예제를 돌리기 위한 코드용도로만 작성되었으며 코드상에 이상한 부분(뻘짓)이 있어도 수정하지 않겠습니다.
