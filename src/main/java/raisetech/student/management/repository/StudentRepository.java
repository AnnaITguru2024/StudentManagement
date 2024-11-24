package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

/**
 *受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Repository
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return 受講生一覧（全件）
   */
  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  Student searchStudent(int id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報（全件）
   */
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId　受講生ID
   * @return 受講生IDに紐づく受講生コース情報のリスト
   */
  List<StudentCourse> searchStudentCourse(int studentId);

  /**
   * 指定したコースIDに紐づくコース申込状況を検索します。
   *
   * @param courseId 受講生コースID
   * @return 受講生コースIDに紐づく受講生コース申込状況
   */
  CourseStatus searchCourseStatus(int courseId);

  /**
   * 条件に基づいて受講生を検索します。
   * @param name 名前
   * @param furigana フリガナ
   * @param city 居住地域
   * @param age 年齢
   * @param gender 性別
   * @return 受講生のリスト
   */
  List<Student> findStudentsByConditions(
      @Param("name") String name,
      @Param("furigana") String furigana,
      @Param("city") String city,
      @Param("age") Integer age,
      @Param("gender") String gender
  );

  /**
   * 条件に基づいて受講生コースを検索します。
   * @param courseName コース名
   * @return 受講生コースのリスト
   */
  List<StudentCourse> findCoursesByConditions(@Param("courseName") String courseName);

  /**
   * 条件に基づいて申込状況を検索します。
   * @param status コースステータス
   * @return コースステータスのリスト
   */
  List<CourseStatus> findCourseStatusByConditions(@Param("status") CourseStatus.Status status);

  /**
   * 受講生とそのコースの詳細およびステータス情報を検索します。
   *
   * @param studentId 受講生ID（任意）
   * @param courseId コースID（任意）
   * @param status ステータス（任意）
   * @return 条件に一致する受講生情報
   */
  List<StudentCourse> searchStudentsWithStatus(int studentId, int courseId, CourseStatus.Status status);

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生コース情報のリスト
   */
  List<StudentCourse> searchStudentCoursesByStudentId(int studentId);

  /**
   * 指定した受講生コースIDに紐づく受講生コース情報を取得します。
   *
   * @param courseId 受講生コースID
   * @return 受講生コースIDに紐づく受講生コース情報
   */
  StudentCourse searchStudentCourseByCourseId(int courseId);

  /**
   * 受講生を新規登録します。IDに関しては自動採番を行う。
   *
   * @param student 受講生
   */
  void registerStudent(Student student);

  /**
   * 受講生コース情報を新規登録します。IDに関しては自動採番を行う。
   *
   * @param studentCourse　受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * コース申込状況を新規登録
   *
   * @param courseStatus コース申込情報
   */
  void registerCourseStatus(CourseStatus courseStatus);

  /**
   * 受講生を更新します。
   *
   * @param student 受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * @param studentCourse 受講生コース情報
   */
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生のステータスを更新します。
   *
   * @param courseStatus 受講生のステータス更新情報
   */
  void updateCourseStatus(CourseStatus courseStatus);

  /**
   * コース申込状況の全件を検索し、一覧を取得します。
   *
   * <p>このメソッドは、データベース内の全てのコース申込状況を
   * {@link CourseStatus} のリストとして返します。申込状況には、
   * 例えば「受講中」、「仮申込」、「本申込」などのステータスが含まれます。
   *
   * @return 全てのコース申込状況を格納した {@link List}。コース申込状況が存在しない場合は空のリストを返します。
   */
  List<CourseStatus> searchCourseStatusList();
}
