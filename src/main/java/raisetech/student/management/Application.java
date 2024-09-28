package raisetech.student.management;

import java.util.List;
import javax.print.DocFlavor.STRING;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@SpringBootApplication
@RestController
public class Application {

  @Autowired
  private StudentRepository repository;

  @Autowired
  private StudentCourseRepository studentCourseRepository;


  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping("/studentList")
  public List<Student> getStudentList() {
    return repository.search();
  }

  @GetMapping("/studentCourseList")
  public ResponseEntity<List<CourseStudentCount>> getCourseStudentCounts() {
    List<CourseStudentCount> courseStudentCounts = studentCourseRepository.countStudentsByCourse();
    if (courseStudentCounts.isEmpty()) {
      return ResponseEntity.noContent().build(); // データがない場合は 204 No Content を返す
    }
    return ResponseEntity.ok(courseStudentCounts); // 正常にリストを返す
  }

}