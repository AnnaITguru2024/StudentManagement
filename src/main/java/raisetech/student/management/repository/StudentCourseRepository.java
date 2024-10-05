package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.management.data.StudentCourse;

/**
 * 受講生コース情報を扱うリポジトリ。
 * 受講生のコース情報を保存、検索するクラスです。
 */
@Mapper
public interface StudentCourseRepository {

  /**
   * 受講生のコース一覧を検索します。
   * @return コース情報の一覧
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentsCourses();

  /**
   * 新しい受講生コース情報をデータベースに保存します。
   * @param studentCourse 保存するコース情報
   */
  @Insert("INSERT INTO students_courses (id, student_id, course_name, start_date, end_date) "
      + "VALUES (#{id}, #{studentId}, #{courseName}, #{startDate}, #{endDate})")
  void save(StudentCourse studentCourse);

  List<StudentCourse> search();
}