package com.intellij.codeInspection.dataFlow.instructions;

import com.intellij.codeInspection.dataFlow.DataFlowRunner;
import com.intellij.codeInspection.dataFlow.DfaInstructionState;
import com.intellij.codeInspection.dataFlow.DfaMemoryState;
import com.intellij.codeInspection.dataFlow.value.DfaConstValue;
import com.intellij.codeInspection.dataFlow.value.DfaRelationValue;
import com.intellij.codeInspection.dataFlow.value.DfaValueFactory;
import com.intellij.codeInspection.dataFlow.value.DfaVariableValue;
import com.intellij.psi.PsiArrayAccessExpression;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;

/**
 * @author max
 */
public class FieldReferenceInstruction extends Instruction {
  private PsiExpression myExpression;
  private DfaRelationValue myDfaNotNull;

  public FieldReferenceInstruction(PsiReferenceExpression expression, DfaValueFactory factory) {
    this(expression.getQualifierExpression(), factory);
    myExpression = expression;
  }

  public FieldReferenceInstruction(PsiArrayAccessExpression expression, DfaValueFactory factory) {
    this(expression.getArrayExpression(), factory);
    myExpression = expression;
  }

  private FieldReferenceInstruction(PsiExpression varReferenceExpression, DfaValueFactory factory) {
    myDfaNotNull = null;
    if (varReferenceExpression instanceof PsiReferenceExpression) {
      PsiVariable psiVariable = DfaValueFactory.resolveVariable((PsiReferenceExpression)varReferenceExpression);
      if (psiVariable != null) {
        DfaVariableValue dfaVariable = factory.getVarFactory().create(psiVariable, false);
        DfaConstValue dfaNull = factory.getConstFactory().getNull();
        myDfaNotNull = factory.getRelationFactory().create(dfaVariable, dfaNull, "==", true);
      }
    }
  }

  public DfaInstructionState[] apply(DataFlowRunner runner, DfaMemoryState memState) {
    if (myDfaNotNull != null && !memState.applyCondition(myDfaNotNull)) {
      runner.onInstructionProducesNPE(this);
      return new DfaInstructionState[0];
    }

    return new DfaInstructionState[]{new DfaInstructionState(runner.getInstruction(getIndex() + 1), memState)};
  }

  public String toString() {
    return "FIELD_REFERENCE: " + myExpression.getText();
  }

  public PsiExpression getExpression() { return myExpression; }
}
