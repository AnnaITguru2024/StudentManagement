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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
   * 全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生詳細一覧（全件）
   */
  @Operation(
      summary = "一覧検索",
      description = "受講生の一覧検索します。全件取得します。",
      responses = {
          @ApiResponse(responseCode = "200", description = "検索成功",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = StudentDetail.class))),
          @ApiResponse(responseCode = "500", description = "サーバーエラー",
              content = @Content)})
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
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
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    return service.searchStudent(id);
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
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
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
