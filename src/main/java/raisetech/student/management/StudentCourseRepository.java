package raisetech.student.management;


import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentCourseRepository {


  // students_courses テーブルからすべてのデータを取得
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> findAll();

  // 特定のコースに何人が登録しているかをカウント
  @Select("SELECT course_name AS courseName, COUNT(student_id) AS studentCount " +
      "FROM students_courses " + "GROUP BY course_name")
  List<CourseStudentCount> countStudentsByCourse();

}
