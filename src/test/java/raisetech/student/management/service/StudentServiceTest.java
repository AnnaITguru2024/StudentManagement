package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    // 事前準備
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    // 実行
    sut.searchStudentList();

    // 検証
    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細の検索_リポジトリの処理を適切に呼び出して受講生IDに紐づく受講生情報と受講生コース情報が返ってくること() {
    // 事前準備
    String id = "1";
    Student student = new Student();
    student.setId(id);

    // モックのStudentCourseオブジェクトのリストを準備
    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    studentCourse1.setStudentId(student.getId());
    studentCourses.add(studentCourse1);

    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse2.setStudentId(student.getId());
    studentCourses.add(studentCourse2);

    // リポジトリのモック設定
    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(student.getId())).thenReturn(studentCourses);

    // 実行
    StudentDetail result = sut.searchStudent(id);

    // 検証
    assertNotNull(result);
    assertEquals(id, result.getStudent().getId());
    assertEquals(2, result.getStudentCourseList().size());
    assertEquals(studentCourse1.getStudentId(), result.getStudentCourseList().get(0).getStudentId());
    assertEquals(studentCourse2.getStudentId(), result.getStudentCourseList().get(1).getStudentId());

    // モックのメソッドが適切な回数呼び出されたことを検証
    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(student.getId());
  }

  @Test
  void 受講生詳細の登録_受講生とコースが正しく登録されること() {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();
    Student student = new Student();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse course1 = new StudentCourse();
    StudentCourse course2 = new StudentCourse();
    studentCourses.add(course1);
    studentCourses.add(course2);
    studentDetail.setStudentCourseList(studentCourses);

    // モックの動作設定
    doNothing().when(repository).registerStudent(student);
    doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));

    // 実行
    StudentDetail result = sut.registerStudent(studentDetail);

    // 検証
    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(2)).registerStudentCourse(any(StudentCourse.class)); // 2つのコースが登録されることを検証
    assertEquals(studentDetail, result);
  }

  @Test
  void 受講生コースの初期化_受講生IDと日付が正しく設定されること() {
    // 事前準備
    String id = "1";
    Student student = new Student();
    student.setId(id);
    StudentDetail studentDetail = new StudentDetail();

    studentDetail.setStudent(student);
    StudentCourse studentCourse = new StudentCourse();
    studentDetail.setStudentCourseList(List.of(studentCourse));

    // モックの動作設定
    doNothing().when(repository).registerStudent(student);
    doNothing().when(repository).registerStudentCourse(any(StudentCourse.class));

    // 実行
    sut.registerStudent(studentDetail);

    // 検証
    assertEquals(student.getId(), studentCourse.getStudentId());
    assertNotNull(studentCourse.getStartDate());
    assertNotNull(studentCourse.getEndDate());
  }

  @Test
  void 受講生詳細の更新_受講生とコースが正しく更新されること() {
    // 事前準備
    StudentDetail studentDetail = new StudentDetail();
    Student student = new Student();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourses = new ArrayList<>();
    StudentCourse course1 = new StudentCourse();
    StudentCourse course2 = new StudentCourse();
    studentCourses.add(course1);
    studentCourses.add(course2);
    studentDetail.setStudentCourseList(studentCourses);

    // モックの動作設定
    doNothing().when(repository).updateStudent(student);
    doNothing().when(repository).updateStudentCourse(any(StudentCourse.class));

    // 実行
    sut.updateStudent(studentDetail);

    // 検証
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(2)).updateStudentCourse(any(StudentCourse.class)); // 2つのコースが更新されることを検証
  }
}