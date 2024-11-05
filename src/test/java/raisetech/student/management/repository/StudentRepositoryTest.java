package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private  StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること() {
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生の登録が行えること() {
    Student student = new Student();
    student.setName("長井　アンナ");
    student.setFurigana("ナガイ　アンナ");
    student.setNickname("あんちゃん");
    student.setEmail("test@example.com");
    student.setCity("大阪");
    student.setAge(34);
    student.setGender("femail");
    student.setRemark("大阪大好き");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生の検索が行えること() {
    Student actual = sut.searchStudent("1"); // 1はテスト用の既存IDに変更
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isEqualTo("田中太郎"); // 既存の名前に合わせて変更
  }

  @Test
  void 受講生のコース情報全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10); // 初期データの件数に合わせて変更
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourse("1"); // 1はテスト用の既存受講生IDに変更
    assertThat(actual).isNotEmpty();
    assertThat(actual.get(0).getCourseName()).isEqualTo("プログラミング基礎"); // 既存のコース名に合わせて変更
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId("1");
    studentCourse.setCourseName("新しいコース");
    studentCourse.setStartDate(LocalDateTime.parse("2024-10-16T10:34:23.4255004"));
    studentCourse.setEndDate(LocalDateTime.parse("2025-10-16T10:34:23.4255004"));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(11); // 追加後の件数に合わせて変更
  }

  @Test
  void 受講生の更新が行えること() {
    Student student = sut.searchStudent("1"); // 1はテスト用の既存IDに変更
    student.setCity("東京");

    sut.updateStudent(student);

    Student updatedStudent = sut.searchStudent("1");
    assertThat(updatedStudent.getCity()).isEqualTo("東京");
  }

  @Test
  void 受講生コース情報のコース名更新が行えること() {
    List<StudentCourse> courses = sut.searchStudentCourse("1"); // 1はテスト用の既存受講生IDに変更
    StudentCourse course = courses.get(0);
    course.setCourseName("更新されたコース名");

    sut.updateStudentCourse(course);

    List<StudentCourse> updatedCourses = sut.searchStudentCourse("1");
    assertThat(updatedCourses.get(0).getCourseName()).isEqualTo("更新されたコース名");
  }
}