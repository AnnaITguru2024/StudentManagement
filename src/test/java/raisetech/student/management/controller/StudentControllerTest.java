package raisetech.student.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    // モックサービスの設定で空リストを返す
    Boolean deleted = false;
    List<StudentDetail> emptyList = Collections.emptyList();  // 空のリスト

    // モックサービスの設定
    when(service.searchStudentList(deleted)).thenReturn(emptyList);

    // GETリクエストを実行して空のリストが返ってくることを確認
    mockMvc.perform(MockMvcRequestBuilders.get("/studentList")
            .param("deleted", String.valueOf(deleted)))
        .andExpect(status().isNoContent());  // 空のリストの場合、204 No Content が返ることを期待

    // サービスメソッドが1回呼ばれたことを確認
    verify(service, times(1)).searchStudentList(deleted);
  }

  @Test
  void 受講生詳細のID検索が実行できて空で返ってくること() throws Exception {
    int id = 999;
    mockMvc.perform(get("/Student/{id}", id))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void コースと申込状況の全件検索が実行できて空のリストが返ってくること() throws Exception {
    // モックサービスの設定で空リストを返す
    List<CourseDetail> emptyCourseList = Collections.emptyList();  // 空のリスト

    // モックサービスの設定
    when(service.getAllCourses()).thenReturn(emptyCourseList);

    // GETリクエストを実行して空のリストが返ってくることを確認
    mockMvc.perform(MockMvcRequestBuilders.get("/studentList/courses"))
        .andExpect(status().isNoContent());  // 空のリストの場合、204 No Content が返ることを期待

    // サービスメソッドが1回呼ばれたことを確認
    verify(service, times(1)).getAllCourses();
  }


  @Test
  void 条件に基づいて受講生詳細を検索し正しいデータが返ってくること() throws Exception {
    // モックデータを直接フィールドに設定
    IntegratedDetail detail = new IntegratedDetail() {
      public String name = "テスト花子";
      public String city = "東京";
      public int age = 25;
      public String gender = "女性";
      public String courseName = "Java";
      public CourseStatus.Status status = CourseStatus.Status.仮申込;
    };

    // モックサービスの設定
    when(service.searchIntegratedDetails(
        "テスト花子", "テストハナコ", "東京", 25, "女性", "Java", null))
        .thenReturn(List.of(detail));

    // テストの実行
    mockMvc.perform(get("/students/search")
            .param("name", "テスト花子")
            .param("furigana", "テストハナコ")
            .param("city", "東京")
            .param("age", "25")
            .param("gender", "女性")
            .param("courseName", "Java"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("テスト花子"))
        .andExpect(jsonPath("$[0].city").value("東京"))
        .andExpect(jsonPath("$[0].age").value(25))
        .andExpect(jsonPath("$[0].gender").value("女性"))
        .andExpect(jsonPath("$.length()").value(1))  // Corrected the path here
        .andExpect(jsonPath("$[0].courseName").value("Java"));
  }

  @Test
  void 受講生コース詳細の一覧検索が実行できてコースが返ってくること() throws Exception {
    // サンプルのStudentCourseとCourseStatusを作成
    StudentCourse studentCourse1 = new StudentCourse(1, 101, "Javaプログラミング", LocalDateTime.now(), LocalDateTime.now().plusDays(30));
    StudentCourse studentCourse2 = new StudentCourse(2, 102, "Pythonプログラミング", LocalDateTime.now(), LocalDateTime.now().plusDays(60));

    CourseStatus courseStatus1 = new CourseStatus(1, 1, CourseStatus.Status.仮申込);
    CourseStatus courseStatus2 = new CourseStatus(2, 2, CourseStatus.Status.受講中);

    CourseDetail courseDetail1 = new CourseDetail(studentCourse1, courseStatus1);
    CourseDetail courseDetail2 = new CourseDetail(studentCourse2, courseStatus2);

    // コース情報のリストを作成
    List<CourseDetail> courseDetails = Arrays.asList(courseDetail1, courseDetail2);

    // サービスがコース情報を返すようにモックする
    when(service.getAllCourses()).thenReturn(courseDetails);

    // エンドポイントにアクセスし、200 OK と コース情報が返ることを確認
    mockMvc.perform(get("/studentList/courses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].studentCourse.id").value(1))
        .andExpect(jsonPath("$[0].studentCourse.courseName").value("Javaプログラミング"))
        .andExpect(jsonPath("$[1].studentCourse.id").value(2))
        .andExpect(jsonPath("$[1].studentCourse.courseName").value("Pythonプログラミング"))
        .andExpect(jsonPath("$[0].courseStatus.status").value("仮申込"))
        .andExpect(jsonPath("$[1].courseStatus.status").value("受講中"));

    // サービスのメソッドが1回呼ばれていることを確認
    verify(service, times(1)).getAllCourses();
  }

  @Test
  void 受講生コース詳細の一覧検索が実行できて空リストが返ってくること() throws Exception {
    // サービスが空リストを返すようにモックする
    when(service.getAllCourses()).thenReturn(Collections.emptyList());

    // エンドポイントにアクセスし、204 No Content が返ることを確認
    mockMvc.perform(get("/studentList/courses"))
        .andExpect(status().isNoContent());

    // サービスのメソッドが1回呼ばれていることを確認
    verify(service, times(1)).getAllCourses();
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
    student.setId(1);
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
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに掛かること() throws Exception {
    String invalidId = "テストです";

    mockMvc.perform(get("/Student/{id}", invalidId))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("無効なIDフォーマット")));
  }
}