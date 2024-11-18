package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.CourseStatus.Status;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private  StudentRepository sut;

  private static Student createStudent(){
    Student student = new Student();
    student.setName("江並こうじ");
    student.setFurigana("エナミコウジ");
    student.setNickname("えーちゃん");
    student.setEmail("testXXX@example.com");
    student.setCity("奈良県");
    student.setAge(36);
    student.setGender("男性");
    student.setRemark("");
    student.setDeleted(false);
    return student;
  }

  private static StudentCourse createStudentCourse(Student student) {
    // 引数付きのコンストラクタを使用してStudentCourseのインスタンスを作成
    StudentCourse studentCourse = new StudentCourse(
        1, student.getId(), "Javaコース",
        LocalDateTime.of(2024, 11, 7, 14, 0), // 開始日
        LocalDateTime.of(2025, 11, 7, 14, 0) // 終了日
    );
    return studentCourse;
  }

  @Test
  void 受講生の全件検索が行えること() {
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 指定した受講生IDに紐づいく受講生の検索が行えること() {
    Student actual = sut.searchStudent(1); // 1はテスト用の既存IDに変更
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isEqualTo("田中太郎"); // 既存の名前に合わせて変更
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
    student.setGender("female");
    student.setRemark("大阪大好き");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生の更新が行えること() {
    Student student = sut.searchStudent(1); // 1はテスト用の既存IDに変更
    student.setCity("東京");

    sut.updateStudent(student);

    Student updatedStudent = sut.searchStudent(1);
    assertThat(updatedStudent.getCity()).isEqualTo("東京");
  }

  @Test
  void 受講生のコース情報全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10); // 初期データの件数に合わせて変更
  }

  @Test
  void 受講生IDに紐づく受講生コース情報を検索できること() {
    int studentId = 1; // テストデータとして使用する受講生ID

    // テスト対象メソッドを実行
    List<StudentCourse> actualList = sut.searchStudentCourse(studentId);

    // 検証：受講生IDに紐づくコース情報が正しく取得できること
    assertEquals(3, actualList.size(), "受講生ID 1に紐づく受講生コース情報の件数が3であること");
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    // 引数付きのコンストラクタを使用してインスタンスを作成
    StudentCourse studentCourse = new StudentCourse(
        1, // id
        1, // studentId
        "新しいコース", // courseName
        LocalDateTime.parse("2024-10-16T10:34:23.4255004"), // startDate
        LocalDateTime.parse("2025-10-16T10:34:23.4255004") // endDate
    );

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(11); // 追加後の件数に合わせて変更
  }


  @Test
  void 受講生コース情報のコース名の更新が行えること() {
    int studentId = 5;

    // 複数の StudentCourse を取得するため、List で受け取る
    List<StudentCourse> studentCourses = sut.searchStudentCourse(studentId);

    // リストの最初のコースの名前を変更
    studentCourses.get(0).setCourseName("AWSコース2");

    // 更新処理
    sut.updateStudentCourse(studentCourses.get(0));

    // 再度データを取得し、期待通りに変更が反映されているか確認
    List<StudentCourse> actualCourses = sut.searchStudentCourse(studentId);
    assertEquals("AWSコース2", actualCourses.get(0).getCourseName());
  }

  @Test
  void コース申込状況の登録が行えること() {
    // 既存のメソッドを使用して、学生データと学生コースデータを準備
    Student student = createStudent();
    sut.registerStudent(student);

    StudentCourse studentCourse = createStudentCourse(student);
    sut.registerStudentCourse(studentCourse);

    // コース申込状況の作成と登録
    CourseStatus courseStatus = new CourseStatus(
        studentCourse.getId(),
        student.getId(),
        CourseStatus.Status.仮申込);
    sut.registerCourseStatus(courseStatus);

    // 結果の確認
    List<CourseStatus> actual = sut.searchCourseStatusList();
    assertEquals(11, actual.size(), "登録後のコース申込状況リストのサイズが1であること");
  }



  @Test
  void コース申込状況の全件検索が行えること() {
    // テスト対象メソッドを実行
    List<CourseStatus> actualList = sut.searchCourseStatusList();

    // 検証：コース申込状況テーブルに登録されているデータがすべて取得できていること
    assertEquals(10, actualList.size());
  }

  @Test
  void コース申込状況の更新が行えること() {
    String courseId = "1"; // コースIDはString型で定義されている
    // コースステータスを検索して取得
    CourseStatus courseStatus = sut.searchCourseStatus(Integer.parseInt(courseId));

    // 文字列 "本申込" をStatus enumに変換してセット
    courseStatus.setStatus(Status.valueOf("本申込"));

    // ステータスを更新
    sut.updateCourseStatus(courseStatus);

    // 更新後のコースステータスを再取得
    CourseStatus actual = sut.searchCourseStatus(Integer.parseInt(courseId));

    // 実際のステータスが "本申込" であることを確認
    assertEquals(Status.本申込, actual.getStatus());
  }
}