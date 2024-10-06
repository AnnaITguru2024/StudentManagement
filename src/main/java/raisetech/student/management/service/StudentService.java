package raisetech.student.management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.repository.StudentCourseRepository;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

  private final StudentRepository studentRepository;
  private final StudentCourseRepository studentCourseRepository;

  @Autowired
  public StudentService(StudentRepository studentRepository,
      StudentCourseRepository studentCourseRepository) {
    this.studentRepository = studentRepository;
    this.studentCourseRepository = studentCourseRepository;
  }

  public List<Student> searchStudentList() {
    return studentRepository.search();
  }

  public List<StudentCourse> searchStudentCourseList() {
    return studentCourseRepository.searchStudentsCourses();
  }

  // 新規受講生を登録
  public void registerStudent(Student student) {
    studentRepository.save(student);  // データベースに保存
  }

  // 新規コースを登録
  public void registerStudentCourse(String studentId, String courseName, LocalDateTime startDate,
      LocalDateTime endDate) {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(studentId); // studentIdを設定
    studentCourse.setCourseName(courseName);
    studentCourse.setStartDate(startDate);
    studentCourse.setEndDate(endDate);
    studentCourseRepository.save(studentCourse);  // データベースに保存
  }

  public void registerStudentCourse(StudentCourse course) {
    if (course.getStudentId() == null) {
      throw new IllegalArgumentException("Student ID cannot be null.");
    }
    studentCourseRepository.save(course);
  }
}
