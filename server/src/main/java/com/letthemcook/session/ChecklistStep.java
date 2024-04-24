package com.letthemcook.session;

public class ChecklistStep {
  private Long stepIndex;
  private Boolean isChecked;

  public Boolean getIsChecked() {
    return isChecked;
  }

  public void setIsChecked(Boolean checked) {
    isChecked = checked;
  }

  public Long getStepIndex() {
    return stepIndex;
  }

  public void setStepIndex(Long stepIndex) {
    this.stepIndex = stepIndex;
  }
}
