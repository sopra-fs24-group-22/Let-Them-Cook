package com.letthemcook.session;

public class ChecklistStep {
  private Integer stepIndex;
  private Boolean isChecked;

  public Boolean getIsChecked() {
    return isChecked;
  }

  public void setIsChecked(Boolean checked) {
    isChecked = checked;
  }

  public Integer getStepIndex() {
    return stepIndex;
  }

  public void setStepIndex(Integer stepIndex) {
    this.stepIndex = stepIndex;
  }
}
