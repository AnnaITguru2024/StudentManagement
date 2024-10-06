package raisetech.student.management.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Getter
@Setter
public class Student {
  private String id;
  private String name;
  private String furigana;
  private String nickname;
  private String email;
  private String city;
  private int age;
  private String gender;
  private String remark;
  private boolean is_deleted;

  public boolean isDeleted() {
    return is_deleted;
  }

  public void setDeleted(boolean is_deleted) {
    this.is_deleted = is_deleted;
  }
}
