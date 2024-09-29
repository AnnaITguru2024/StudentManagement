package raisetech.student.management.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.CourseStudentCount;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.repository.StudentCourseRepository;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;


  @Autowired
  public StudentService(StudentRepository repository) {
    this.repository = repository;
  }

  public List<Student> searchStudentList() {
    List<Student> studentList = repository.search(); // すべての学生情報を取得

    List<Student> under25Students = studentList.stream()
        .filter(student -> student.getAge() <= 25) // 年齢が25歳以下の学生をフィルタリング
        .toList();

    return under25Students;
  }

  public List<StudentCourse> searchStudentCourseList() {
    List<StudentCourse> studentCourseList = repository.searchStudentsCourses(); // すべてのコース情報を取得

    List<StudentCourse> javaCourseStudents = studentCourseList.stream()
        .filter(studentCourse -> studentCourse.getCourseName().equals("Javaコース")) // コース名が"Javaコース"の学生コースをフィルタリング
        .toList();

    return javaCourseStudents;
  }

}
