package raisetech.student.management.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@Controller
public class StudentController {

  private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
  private final StudentService service;
  private final StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/studentList")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentCourses = service.searchStudentCourseList();

    model.addAttribute("studentList", converter.convertStudentDetails(students, studentCourses));
    return "studentList";
  }

  @GetMapping("/studentsCourseList")
  public List<StudentCourse> getStudentCourseList() {
    return service.searchStudentCourseList();
  }

  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    model.addAttribute("studentDetail", new StudentDetail());
    return "registerStudent";
  }

  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
    if(result.hasErrors()) {
      return "registerStudent";
    }
    // 1. 新規受講生情報を先に登録
    Student student = studentDetail.getStudent();
    service.registerStudent(student);

    // 2. 学生がデータベースに保存された後に、IDを取得
    String studentId = student.getId();

    if (studentId == null) {
      // エラー処理またはログ
      logger.error("Student ID is null after saving student.");
      return "error";
    }

    // 3. コース情報を登録
    List<StudentCourse> courses = studentDetail.getStudentCourse();
    for (StudentCourse course : courses) {
      course.setStudentId(studentId);  // データベースで生成された受講生IDをセット
      service.registerStudentCourse(course);  // コースを登録
    }
    // ログメッセージを使用して受講生登録完了を通知
    return "redirect:/studentList";
  }
}
