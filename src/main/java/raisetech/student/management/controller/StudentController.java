package raisetech.student.management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.data.CourseStudentCount;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.service.StudentService;

@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  @GetMapping("/studentList")
  public List<Student> getStudentList() {
    return service.searchStudentList();
  }

  @GetMapping("/studentsCourseList")
  public List<StudentCourse> getStudentCourseList() {
    return service.searchStudentCourseList();
  }

  // 年齢が25歳以下の学生を取得
  @GetMapping("/students/under25")
  public List<Student> getStudentsUnder25() {
    return service.searchStudentList();
  }

  // "Javaコース"の学生コース情報を取得
  @GetMapping("/students/courses/java")
  public List<StudentCourse> getJavaCourseStudents() {
    return service.searchStudentCourseList();
  }
}
