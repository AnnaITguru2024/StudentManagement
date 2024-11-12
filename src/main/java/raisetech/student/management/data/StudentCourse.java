package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentCourse {

  private int id;

  private int studentId;

  @NotBlank
  private String courseName;
  private LocalDateTime startDate;
  private LocalDateTime endDate;


  public StudentCourse(int id, int studentId, String courseName, LocalDateTime startDate, LocalDateTime endDate) {
    this.id = id;
    this.studentId = studentId;
    this.courseName = courseName;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public void setCourseStatus(CourseStatus courseStatus) {
  }

}
