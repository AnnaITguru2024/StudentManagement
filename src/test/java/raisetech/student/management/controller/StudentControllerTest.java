package raisetech.student.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.data.Student;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の検索が実行できて空で返ってくること() throws Exception {
    String id = "999";
    mockMvc.perform(get("/Student/{id}", id))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生詳細の登録が実装できて空で返ってくること() throws Exception {
    //リクエストデータは最初に構築して入力チェックの検証も重ねている。
    //本来であれば返りは登録されたデータが入るが、モック化すると意味がないため、レスポンスは作らない。
    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                        "student" : {
                            "name": "長井　アンナ",
                            "furigana": "ナガイ　アンナ",
                            "nickname": "あんちゃん",
                            "email": "example1113@jp.com",
                            "city": "大阪",
                            "age": 34,
                            "gender": "female",
                            "remark": "大阪大好き"
                        },
                        "studentCourseList" : [
                            {
                                "courseName": "Javaコース"
                            }
                        ]
                    }
                    """))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudent(any());
  }

  @Test
  void 受講生詳細の更新が実行できて空で返ってくること() throws Exception {
    mockMvc.perform(put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON).content(
                """
                    {
                        "student": {
                            "id": "30",
                            "name": "長井　アンナ",
                            "furigana": "ナガイ　アンナ",
                            "nickname": "あんちゃん",
                            "email": "example1113@jp.com",
                            "city": "大阪",
                            "age": 34,
                            "gender": "female",
                            "remark": "大阪大好き"
                        },
                        "studentCourseList": [
                            {
                                "id": 15,
                                "studentId": "30",
                                "courseName": "Javaコース",
                                "startDate": "2024-10-16T10:34:23.4255004",
                                "endDate": "2025-10-16T10:34:23.4255004"
                            }
                        ]
                    }
                    """)).andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で帰ってくること() throws Exception {
    mockMvc.perform(get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています。"));
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックに非常が発生しないこと() {
    Student student = new Student();
    student.setId("1");
    student.setName("長井　アンナ");
    student.setFurigana("ナガイ　アンナ");
    student.setNickname("あんちゃん");
    student.setEmail("example@test.com");
    student.setCity("大阪");
    student.setGender("female");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに掛かること() {
    Student student = new Student();
    student.setId("テストです");
    student.setName("長井　アンナ");
    student.setFurigana("ナガイ　アンナ");
    student.setNickname("あんちゃん");
    student.setEmail("example@test.com");
    student.setCity("大阪");
    student.setGender("female");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("数字のみ入力するようにしてください。");
  }
}