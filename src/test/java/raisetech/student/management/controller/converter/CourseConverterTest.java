package raisetech.student.management.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;

class CourseConverterTest {
  private CourseConverter sut;

  @BeforeEach
  void before() {
    sut = new CourseConverter();
  }

  @Test
  void 学生コースリストとコースステータスリストを渡してコース詳細リストが作成できること() {
    StudentCourse studentCourse = createStudentCourse();

    CourseStatus courseStatus = new CourseStatus(1, 1, CourseStatus.Status.受講中);

    List<StudentCourse> studentCourses = List.of(studentCourse);
    List<CourseStatus> courseStatuses = List.of(courseStatus);

    List<CourseDetail> actual = sut.convertToCourseDetails(studentCourses, courseStatuses);

    assertThat(actual.get(0).getStudentCourse()).isEqualTo(studentCourse);
    assertThat(actual.get(0).getCourseStatus()).isEqualTo(courseStatus);
  }

  @Test
  void 学生コースリストとコースステータスリストを渡した時に紐づかないコースステータスは除外されること() {
    StudentCourse studentCourse = createStudentCourse();

    CourseStatus courseStatus = new CourseStatus(1, 2, CourseStatus.Status.受講中);  // 紐づかないIDを指定

    List<StudentCourse> studentCourses = List.of(studentCourse);
    List<CourseStatus> courseStatuses = List.of(courseStatus);

    List<CourseDetail> actual = sut.convertToCourseDetails(studentCourses, courseStatuses);

    assertThat(actual.get(0).getStudentCourse()).isEqualTo(studentCourse);
    assertThat(actual.get(0).getCourseStatus()).isNull();
  }

  private static StudentCourse createStudentCourse() {
    // 引数付きコンストラクタを使用
    return new StudentCourse(1, 1, "Javaコース", LocalDateTime.now(), LocalDateTime.now().plusYears(1));
  }
}