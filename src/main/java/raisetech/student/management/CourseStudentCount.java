package raisetech.student.management;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CourseStudentCount {

  private String courseName;
  private int studentCount;

  public CourseStudentCount(String courseName, int studentCount) {
    this.courseName = courseName;
    this.studentCount = studentCount;
  }

}
