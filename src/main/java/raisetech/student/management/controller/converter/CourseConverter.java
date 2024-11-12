package raisetech.student.management.controller.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;

@Component
public class CourseConverter {

  /**
   * List<StudentCourse> と List<CourseStatus> を List<CourseDetail> に変換します。
   *
   * @param studentCourses 学生のコースリスト
   * @param courseStatuses コースステータスリスト
   * @return CourseDetail のリスト
   */
  public List<CourseDetail> convertToCourseDetails(List<StudentCourse> studentCourses, List<CourseStatus> courseStatuses) {
    return studentCourses.stream()
        .map(studentCourse -> {
          CourseStatus courseStatus = courseStatuses.stream()
              .filter(status -> status.getCourseId() == studentCourse.getId())
              .findFirst()
              .orElse(null);
          return convertToCourseDetail(studentCourse, courseStatus);
        })
        .collect(Collectors.toList());
  }

  /**
   * StudentCourse と CourseStatus を受け取り、CourseDetail に変換します。
   *
   * @param studentCourse 学生のコース情報
   * @param courseStatus コースステータス
   * @return CourseDetail
   */
  public CourseDetail convertToCourseDetail(StudentCourse studentCourse, CourseStatus courseStatus) {
    CourseDetail courseDetail = new CourseDetail();
    courseDetail.setStudentCourse(studentCourse);
    courseDetail.setCourseStatus(courseStatus);
    return courseDetail;
  }
}

