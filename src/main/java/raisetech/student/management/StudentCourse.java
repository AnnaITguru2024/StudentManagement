package raisetech.student.management;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StudentCourse {
  private int courseId;
  private String studentId;
  private String courseName;
  private String startDate;
  private String endDate;

}
