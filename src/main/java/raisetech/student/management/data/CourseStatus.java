package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema (description = "コースに対する申込情報")
@Getter
@Setter
public class CourseStatus {

  private int id;

  private int courseId;

  private Status status;

  public enum Status {
    仮申込, 本申込, 受講中, 受講終了
  }


  public CourseStatus(int id, int courseId, Status status) {
    this.id = id;
    this.courseId = courseId;
    this.status = status;
  }
}
