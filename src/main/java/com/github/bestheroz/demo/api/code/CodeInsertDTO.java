package com.github.bestheroz.demo.api.code;

import com.github.bestheroz.demo.entity.Code;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeInsertDTO {
  @Schema(example = "EXAMPLE")
  @NotEmpty
  private String type;

  @Schema(example = "E3")
  @NotEmpty
  private String value;

  @Schema(example = "예제3")
  @NotEmpty
  private String text;

  @Schema(example = "false")
  @NotNull
  private Boolean available;

  @Schema(example = "20")
  @NotNull
  private Integer displayOrder;

  public Code toCode() {
    return Code.builder()
        .type(this.type)
        .value(this.value)
        .text(this.text)
        .available(this.available)
        .displayOrder(this.displayOrder)
        .build();
  }
}
