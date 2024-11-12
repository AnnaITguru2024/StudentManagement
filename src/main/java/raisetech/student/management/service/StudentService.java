package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.CourseConverter;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;


/**
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索や登録、更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter studentConverter;
  private CourseConverter courseConverter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter studentConverter, CourseConverter courseConverter) {
    this.repository = repository;
    this.studentConverter = studentConverter;
    this.courseConverter = courseConverter;
  }

  /**
   * 受講生詳細一覧検索です。全件検索を行い、フィルタリングも行います。
   *
   * @param deleted 削除済みフラグ
   * @return 受講生詳細一覧（全件）
   */
  public List<StudentDetail> searchStudentList(Boolean deleted) {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();

    return studentConverter.convertStudentDetails(studentList, studentCourseList);
  }

  /**
   * 受講生コース詳細一覧検索を行います。
   *
   * @return コース詳細情報一覧
   */
  public List<StudentCourse> searchStudentCourseList() {
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseStatus> courseStatusList = repository.searchCourseStatusList();

    return studentCourseList;
  }

  /**
   * 受講生詳細検索です。
   * IDに紐づく受講生情報を取得したあと、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param id　受講生ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(int id) {
    Student student = repository.searchStudent(id);
    if (student == null) {
      throw new OpenApiResourceNotFoundException("Student with id " + id + " not found");
    }
    List<StudentCourse> studentCourses = repository.searchStudentCoursesByStudentId(student.getId());
    return studentConverter.convertToStudentDetail(student, studentCourses);
  }

  /**
   * 受講生コースを検索する
   *
   * @param studentId 受講生ID
   * @return 受講生コース情報のリスト
   */
  public List<StudentCourse> searchStudentCourse(int studentId) {
    // 受講生IDに関連する全てのコース情報を取得
    List<StudentCourse> studentCourses = repository.searchStudentCoursesByStudentId(studentId);

    // コース情報が無い場合に例外を投げる
    if (studentCourses == null || studentCourses.isEmpty()) {
      throw new OpenApiResourceNotFoundException("No StudentCourses found for student ID " + studentId);
    }

    // 各コースのステータスを取得して関連付ける
    for (StudentCourse studentCourse : studentCourses) {
      int courseId = studentCourse.getId();
      if (courseId == 0) {
        throw new OpenApiResourceNotFoundException("Course ID is missing for StudentCourse with id " + studentCourse.getId());
      }

      CourseStatus courseStatus = repository.searchCourseStatus(courseId);
      if (courseStatus == null) {
        throw new OpenApiResourceNotFoundException("CourseStatus not found for course ID " + courseId);
      }

      studentCourse.setCourseStatus(courseStatus); // コースステータスを設定
    }

    return studentCourses;
  }


  /**
   * 受講生詳細の登録を行います。
   * 受講生と受講生コース情報を個別に登録し、受講生コース情報に受講生情報を紐づける値とコース開始日、コース終了日を設定します。
   * 
   * @param studentDetail　受講生詳細
   * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public IntegratedDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();
    repository.updateStudent(student);

    List<CourseDetail> courseDetails = new ArrayList<>();
    repository.registerStudent(student);

    studentDetail.getStudentCourseList().forEach(studentCourses -> {
      initStudentsCourse(studentCourses, student.getId());
      repository.registerStudentCourse(studentCourses);

      CourseStatus courseStatus = new CourseStatus(0, studentCourses.getId(), CourseStatus.Status.仮申込);

      registerCourseStatus(courseStatus);

      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentCourse(studentCourses);
      courseDetail.setCourseStatus(courseStatus);
      courseDetails.add(courseDetail);
    });

    return new IntegratedDetail(studentDetail, courseDetails);
  }


  /**
   * コース申込状況の新規登録を行います。
   *
   * @param courseStatus コース申込状況
   */
  @Transactional
  public void registerCourseStatus(CourseStatus courseStatus) {
    // StudentRepositoryのregisterCourseStatusメソッドを呼び出してデータベースに登録
    repository.registerCourseStatus(courseStatus);
  }

  /**
   * すべてのコース情報を取得します。
   *
   * @return コース詳細情報一覧
   */
  public List<CourseDetail> getAllCourses() {
    List<StudentCourse> studentCourses = repository.searchStudentCourseList();
    List<CourseStatus> courseStatuses = repository.searchCourseStatusList();

    return courseConverter.convertToCourseDetails(studentCourses, courseStatuses);
  }

  /**
   * 指定されたIDに基づいてコース情報を取得します。
   *
   * @param courseId コースID
   * @return 指定されたIDのコース詳細情報
   * @throws OpenApiResourceNotFoundException IDに基づくコースが見つからない場合に例外をスロー
   */
  // サービスのメソッドを修正
  public CourseDetail getCourseById(int courseId) {
    // コースIDに基づき、StudentCourse情報を取得
    List<StudentCourse> studentCourse = repository.searchStudentCourse(courseId);
    if (studentCourse.isEmpty()) {
      throw new OpenApiResourceNotFoundException("Course with ID " + courseId + " not found");
    }

    // コースステータスの取得
    CourseStatus courseStatus = repository.searchCourseStatus(courseId);
    if (courseStatus == null) {
      throw new OpenApiResourceNotFoundException("CourseStatus not found for course ID " + courseId);
    }

    // StudentCourseとCourseStatusを使用してCourseDetailに変換して返す
    return courseConverter.convertToCourseDetail((StudentCourse) studentCourse, courseStatus);
  }


  /**
   * 受講生コース情報の初期設定を行います。
   *
   * @param studentCourse 受講生コース情報
   * @param studentId 受講生ID
   */
  private void initStudentsCourse(StudentCourse studentCourse, int studentId) {
    LocalDateTime now = LocalDateTime.now();

    studentCourse.setStudentId(studentId);     // 受講生IDの設定
    studentCourse.setStartDate(now);           // 開始日を現在日時に設定
    studentCourse.setEndDate(now.plusYears(1)); // 終了日を1年後に設定
  }

  /**
   * 受講生詳細の更新を行います。受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  // 受講生情報の更新
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentsCourse -> repository.updateStudentCourse(studentsCourse));
  }

  /**
   * コース申込状況の更新
   *
   * @param courseStatus コース申込状況
   */
  @Transactional
  public void updateCourseStatus(CourseStatus courseStatus) throws Exception {
    repository.updateCourseStatus(courseStatus);
  }
}
