package raisetech.student.management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

/**
 *受講生情報を扱うリポジトリ。
 * 全件検索や単一条件での検索、コース情報の検索が行えるクラスです。
 */
@Repository
@Mapper
public interface StudentRepository {

  /**
   * 新しい受講生情報をデータベースに保存します。
   * @param student 保存する受講生情報
   */
  @Insert("INSERT INTO students (id, name, furigana, nickname, email, city, age, gender, remark, is_deleted) "
      + "VALUES (#{id}, #{name}, #{furigana}, #{nickname}, #{email}, #{city}, #{age}, #{gender}, #{remark}, #{is_deleted})")
  void save(Student student);

  @Select("SELECT * FROM students WHERE is_deleted = 0")
  List<Student> search();
}
