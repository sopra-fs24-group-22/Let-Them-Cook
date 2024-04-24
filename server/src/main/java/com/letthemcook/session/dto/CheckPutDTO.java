package com.letthemcook.session.dto;

public class CheckPutDTO {
  private Long stepIndex;
  private Boolean isChecked;

  public Long getStepIndex() {
    return stepIndex;
  }

  public void setStepIndex(Long stepIndex) {
    this.stepIndex = stepIndex;
  }

  public Boolean getIsChecked() {
    return isChecked;
  }

  public void setIsChecked(Boolean isChecked) {
    this.isChecked = isChecked;
  }
}
