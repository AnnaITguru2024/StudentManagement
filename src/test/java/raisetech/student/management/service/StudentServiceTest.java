package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.CourseConverter;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter studentConverter;

  @Mock
  private CourseConverter courseConverter;

  private StudentService sut;

  private Student student1;
  private Student student2;
  private Student student3;
  private StudentCourse studentCourse;
  private CourseStatus courseStatus;


  @BeforeEach
  void before() {
    sut = new StudentService(repository, studentConverter, courseConverter);
  }

  private static List<StudentDetail> createTestStudentDetails() {
    Student activeStudent = new Student();
    activeStudent.setDeleted(false);

    // StudentCourse のインスタンスを引数付きコンストラクタで作成
    StudentCourse activeStudentCourse1 = new StudentCourse(1, activeStudent.getId(), "Javaコース", LocalDateTime.of(2024, 11, 7, 14, 0), LocalDateTime.of(2025, 11, 7, 14, 0));
    StudentCourse activeStudentCourse2 = new StudentCourse(2, activeStudent.getId(), "Pythonコース", LocalDateTime.of(2024, 11, 7, 14, 0), LocalDateTime.of(2025, 11, 7, 14, 0));
    List<StudentCourse> activeStudentCourses = new ArrayList<>(List.of(activeStudentCourse1, activeStudentCourse2));
    StudentDetail activeStudentDetail = new StudentDetail(activeStudent, activeStudentCourses);

    Student deletedStudent = new Student();
    deletedStudent.setDeleted(true);

    // DeletedStudentのコースも引数付きコンストラクタを使って作成
    StudentCourse deletedStudentCourse1 = new StudentCourse(1, deletedStudent.getId(), "Javaコース", LocalDateTime.of(2024, 11, 7, 14, 0), LocalDateTime.of(2025, 11, 7, 14, 0));
    StudentCourse deletedStudentCourse2 = new StudentCourse(2, deletedStudent.getId(), "Pythonコース", LocalDateTime.of(2024, 11, 7, 14, 0), LocalDateTime.of(2025, 11, 7, 14, 0));
    List<StudentCourse> deletedStudentCourses = new ArrayList<>(List.of(deletedStudentCourse1, deletedStudentCourse2));
    StudentDetail deletedStudentDetail = new StudentDetail(deletedStudent, deletedStudentCourses);

    return new ArrayList<>(List.of(activeStudentDetail, deletedStudentDetail));
  }

  @Test
  void 受講生詳細一覧検索_削除フラグがnullのときにリポジトリとコンバータが正しく呼び出される() {
    Boolean deleted = null;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertEquals(studentDetails, actualStudentDetails);
    assertEquals(2, actualStudentDetails.size());
  }

  @Test
  void 受講生詳細一覧検索_削除フラグがfalseのときにアクティブな受講生のみが取得される() {
    Boolean deleted = false;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertFalse(studentDetails.get(0).getStudent().isDeleted());
    assertEquals(2, actualStudentDetails.size());
  }

  @Test
  void 受講生詳細一覧検索_削除フラグがtrueのときに削除された受講生のみが取得される() {
    Boolean deleted = true;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertTrue(studentDetails.get(1).getStudent().isDeleted());
    assertEquals(2, actualStudentDetails.size());
  }
}