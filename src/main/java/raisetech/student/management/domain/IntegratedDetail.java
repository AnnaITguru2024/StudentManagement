package raisetech.student.management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生詳細と受講生コース詳細の統合")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IntegratedDetail {

  @Valid
  private StudentDetail studentDetail;

  @Valid
  private List<CourseDetail> courseDetailList;
}
