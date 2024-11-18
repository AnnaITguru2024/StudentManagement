package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして受け付けるControllerです。
 */
@Validated
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {

    this.service = service;
  }

  /**
   * 受講生詳細の一覧検索です。
   * リクエストパラメーターでdeletedの値を指定することにより、現在の受講生または過去の受講生に絞って検索できます。
   *
   * @return 受講生詳細一覧
   */
  @Operation(
      summary = "一覧検索",
      description = "受講生の一覧を取得します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "検索成功",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))),
          @ApiResponse(responseCode = "204", description = "データなし"),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)}
  )
  @GetMapping("/studentList")
  public ResponseEntity<List<StudentDetail>> getStudentList(
      @RequestParam(value = "deleted", required = false) Boolean deleted) {

    List<StudentDetail> students = service.searchStudentList(deleted);

    if (students.isEmpty()) {
      // データが存在しない場合、204 No Contentを返却
      return ResponseEntity.noContent().build();
    } else {
      // データがある場合、200 OKでリストを返却
      return ResponseEntity.ok(students);
    }
  }

  /**
   * 受講生詳細の検索です。IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id　受講生ID
   * @return 受講生
   */
  @Operation(
      summary = "受講生詳細検索",
      description = "指定したIDに基づいて、特定の受講生情報を取得します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "検索成功",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))),
          @ApiResponse(responseCode = "404", description = "受講生が見つかりませんでした",
              content = @Content),
          @ApiResponse(responseCode = "400", description = "無効なIDフォーマット",
              content = @Content)})
  @GetMapping("/Student/{id}")
  public StudentDetail getStudent(@PathVariable Integer id) {
    return service.searchStudent(id);
  }

  /**
   * コース情報および申込状況の全件検索
   *
   * @return 全てのコースと申込状況
   */
  @Operation(
      summary = "コースと申込状況の全件検索",
      description = "全ての受講生のコース情報および申込状況を取得します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "検索成功",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = IntegratedDetail.class))),
          @ApiResponse(responseCode = "204", description = "データなし"),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)}
  )
  @GetMapping("/studentList/courses")
  public ResponseEntity<List<CourseDetail>> getAllCourses() {
    List<CourseDetail> courses = service.getAllCourses();
    if (courses.isEmpty()) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.ok(courses);
    }
  }

  /**
   * 受講生詳細の登録を行います。
   *
   * @param studentDetail　受講生詳細
   * @return 実行結果
   */
  @Operation(
      summary = "受講生登録",
      description = "受講生を登録します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "登録成功",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))),
          @ApiResponse(responseCode = "400", description = "入力エラー",
              content = @Content),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)})
  @PostMapping("/registerStudent")
  public ResponseEntity<IntegratedDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    IntegratedDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。キャンセルフラグの更新もここで行います（論理削除）
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(
      summary = "受講生更新",
      description = "既存の受講生詳細を更新します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "更新成功",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "入力エラー",
              content = @Content),
          @ApiResponse(responseCode = "404", description = "受講生が見つかりませんでした",
              content = @Content),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)})
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  @Operation(
      summary = "受講生コース申込状況更新",
      description = "受講生コースの申込状況の更新を行います。",
      responses = {
          @ApiResponse(responseCode = "200", description = "更新成功",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "400", description = "入力エラー",
              content = @Content),
          @ApiResponse(responseCode = "404", description = "受講生コースが見つかりませんでした",
              content = @Content),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)})
  @PutMapping("/studentList/courses/statuses/update")
  public ResponseEntity<String> updateStudentCourse(@RequestBody @Valid CourseStatus courseStatus) {
    try {
      service.updateCourseStatus(courseStatus);
      return ResponseEntity.ok("申込状況が更新されました。");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("申込状況の更新に失敗しました: " + e.getMessage());
    }
  }

  @Operation(
      summary = "例外発生テスト",
      description = "このAPIは例外を強制的にスローします。現在は利用できません。",
      responses = {
          @ApiResponse(responseCode = "404", description = "指定されたリソースが見つかりません",
              content = @Content),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)})
  @GetMapping("/exception")
  public ResponseEntity<String> throwException() throws NotFoundException {
    throw new NotFoundException("このAPIは現在利用できません。古いURLとなっています。");
  }
}
