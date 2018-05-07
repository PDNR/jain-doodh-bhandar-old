package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        int i2;
        ConstraintWidget[] constraintWidgetArr;
        int i3;
        int i4 = 0;
        if (i == 0) {
            i2 = constraintWidgetContainer.mHorizontalChainsSize;
            constraintWidgetArr = constraintWidgetContainer.mHorizontalChainsArray;
            i3 = i2;
            i2 = 0;
        } else {
            i2 = 2;
            int i5 = constraintWidgetContainer.mVerticalChainsSize;
            i3 = i5;
            constraintWidgetArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        while (i4 < i3) {
            ConstraintWidget constraintWidget = constraintWidgetArr[i4];
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, constraintWidget);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, i, i2, constraintWidget)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, constraintWidget);
            }
            i4++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ConstraintWidget constraintWidget) {
        ConstraintWidget constraintWidget2;
        Object obj;
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget3;
        Object obj2;
        Object obj3;
        Object obj4;
        float f;
        Object obj5;
        Object obj6;
        ConstraintWidget constraintWidget4;
        ConstraintWidget constraintWidget5;
        ConstraintWidget constraintWidget6;
        ConstraintWidget constraintWidget7;
        ConstraintWidget constraintWidget8;
        int i3;
        ConstraintAnchor constraintAnchor2;
        int margin;
        Object obj7;
        int i4;
        ConstraintWidget constraintWidget9;
        ConstraintWidget constraintWidget10;
        ConstraintWidget constraintWidget11;
        ConstraintAnchor constraintAnchor3;
        ConstraintWidget constraintWidget12;
        int i5;
        int i6;
        ConstraintWidget constraintWidget13;
        float f2;
        float f3;
        SolverVariable solverVariable;
        SolverVariable solverVariable2;
        SolverVariable solverVariable3;
        SolverVariable solverVariable4;
        int i7;
        Object obj8;
        ArrayRow createRow;
        ConstraintAnchor constraintAnchor4;
        ConstraintAnchor constraintAnchor5;
        SolverVariable solverVariable5;
        ConstraintWidget constraintWidget14;
        ConstraintWidget constraintWidget15;
        ConstraintAnchor constraintAnchor6;
        ConstraintAnchor constraintAnchor7;
        int i8;
        ConstraintWidget constraintWidget16;
        int i9;
        ConstraintWidgetContainer constraintWidgetContainer2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        ConstraintWidget constraintWidget17 = constraintWidget;
        Object obj9 = constraintWidgetContainer2.mListDimensionBehaviors[i] == DimensionBehaviour.WRAP_CONTENT ? 1 : null;
        ConstraintWidget constraintWidget18 = null;
        if (i == 0 && constraintWidgetContainer.isRtl()) {
            constraintWidget2 = constraintWidget17;
            obj = null;
            while (obj == null) {
                constraintAnchor = constraintWidget2.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor != null) {
                    constraintWidget3 = constraintAnchor.mOwner;
                    if (constraintWidget3.mListAnchors[i2].mTarget != null) {
                    }
                }
                constraintWidget3 = null;
                if (constraintWidget3 != null) {
                    constraintWidget2 = constraintWidget3;
                } else {
                    obj = 1;
                }
            }
        } else {
            constraintWidget2 = constraintWidget17;
        }
        if (i == 0) {
            obj2 = constraintWidget2.mHorizontalChainStyle == 0 ? 1 : null;
            obj3 = constraintWidget2.mHorizontalChainStyle == 1 ? 1 : null;
        } else {
            obj2 = constraintWidget2.mVerticalChainStyle == 0 ? 1 : null;
            obj3 = constraintWidget2.mVerticalChainStyle == 1 ? 1 : null;
            if (constraintWidget2.mVerticalChainStyle == 2) {
            }
            obj = null;
            obj4 = obj;
            f = 0.0f;
            obj5 = obj2;
            obj6 = obj3;
            constraintWidget4 = constraintWidget17;
            constraintWidget5 = null;
            constraintWidget6 = constraintWidget5;
            constraintWidget7 = constraintWidget6;
            constraintWidget8 = constraintWidget7;
            obj = null;
            i3 = 0;
            while (obj == null) {
                constraintWidget4.mListNextVisibleWidget[i] = constraintWidget18;
                if (constraintWidget4.getVisibility() != 8) {
                    if (constraintWidget8 != null) {
                        constraintWidget8.mListNextVisibleWidget[i] = constraintWidget4;
                    }
                    if (constraintWidget7 == null) {
                        constraintWidget7 = constraintWidget4;
                    }
                    constraintWidget8 = constraintWidget4;
                }
                constraintAnchor2 = constraintWidget4.mListAnchors[i2];
                margin = constraintAnchor2.getMargin();
                if (constraintAnchor2.mTarget != null || constraintWidget4 == constraintWidget17) {
                    obj7 = obj;
                } else {
                    obj7 = obj;
                    if (constraintWidget4.getVisibility() != 8) {
                        margin += constraintAnchor2.mTarget.getMargin();
                    }
                }
                i4 = margin;
                int i10 = (obj4 != null || constraintWidget4 == constraintWidget17 || constraintWidget4 == constraintWidget7) ? 1 : 6;
                if (constraintWidget4 != constraintWidget7) {
                    constraintWidget9 = constraintWidget7;
                    constraintWidget10 = constraintWidget8;
                    constraintWidget11 = constraintWidget2;
                    linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, 5);
                } else {
                    constraintWidget11 = constraintWidget2;
                    constraintWidget9 = constraintWidget7;
                    constraintWidget10 = constraintWidget8;
                    linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, 6);
                }
                linearSystem2.addEquality(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, i10);
                constraintWidget18 = null;
                constraintWidget4.mListNextMatchConstraintsWidget[i] = null;
                if (constraintWidget4.getVisibility() != 8 && constraintWidget4.mListDimensionBehaviors[i] == DimensionBehaviour.MATCH_CONSTRAINT) {
                    i3++;
                    f += constraintWidget4.mWeight[i];
                    if (constraintWidget6 != null) {
                        constraintWidget6 = constraintWidget4;
                    } else {
                        constraintWidget5.mListNextMatchConstraintsWidget[i] = constraintWidget4;
                    }
                    if (obj9 != null) {
                        linearSystem2.addGreaterThan(constraintWidget4.mListAnchors[i2 + 1].mSolverVariable, constraintWidget4.mListAnchors[i2].mSolverVariable, 0, 6);
                    }
                    constraintWidget5 = constraintWidget4;
                }
                if (obj9 != null) {
                    linearSystem2.addGreaterThan(constraintWidget4.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, 0, 6);
                }
                constraintAnchor3 = constraintWidget4.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor3 != null) {
                    constraintWidget12 = constraintAnchor3.mOwner;
                    if (constraintWidget12.mListAnchors[i2].mTarget != null) {
                    }
                }
                constraintWidget12 = null;
                if (constraintWidget12 == null) {
                    constraintWidget4 = constraintWidget12;
                    obj = obj7;
                } else {
                    obj = 1;
                }
                constraintWidget7 = constraintWidget9;
                constraintWidget8 = constraintWidget10;
                constraintWidget2 = constraintWidget11;
            }
            constraintWidget11 = constraintWidget2;
            if (constraintWidget8 != null) {
                i5 = i2 + 1;
                if (constraintWidget4.mListAnchors[i5].mTarget != null) {
                    constraintAnchor3 = constraintWidget8.mListAnchors[i5];
                    linearSystem2.addLowerThan(constraintAnchor3.mSolverVariable, constraintWidget4.mListAnchors[i5].mTarget.mSolverVariable, -constraintAnchor3.getMargin(), 5);
                    if (obj9 != null) {
                        i6 = i2 + 1;
                        linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].getMargin(), 6);
                    }
                    if (i3 > 0) {
                        while (constraintWidget6 != null) {
                            constraintWidget13 = constraintWidget6.mListNextMatchConstraintsWidget[i];
                            if (constraintWidget13 == null) {
                                f2 = constraintWidget6.mWeight[i];
                                f3 = constraintWidget13.mWeight[i];
                                solverVariable = constraintWidget6.mListAnchors[i2].mSolverVariable;
                                i5 = i2 + 1;
                                solverVariable2 = constraintWidget6.mListAnchors[i5].mSolverVariable;
                                solverVariable3 = constraintWidget13.mListAnchors[i2].mSolverVariable;
                                solverVariable4 = constraintWidget13.mListAnchors[i5].mSolverVariable;
                                if (i != 0) {
                                    i3 = constraintWidget6.mMatchConstraintDefaultWidth;
                                    i7 = constraintWidget13.mMatchConstraintDefaultWidth;
                                } else {
                                    i3 = constraintWidget6.mMatchConstraintDefaultHeight;
                                    i7 = constraintWidget13.mMatchConstraintDefaultHeight;
                                }
                                if (i3 == 0 || i3 == 3) {
                                    if (i7 != 0) {
                                        if (i7 == 3) {
                                        }
                                    }
                                    obj8 = 1;
                                    if (obj8 == null) {
                                        createRow = linearSystem.createRow();
                                        createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                        linearSystem2.addConstraint(createRow);
                                    }
                                }
                                obj8 = null;
                                if (obj8 == null) {
                                    createRow = linearSystem.createRow();
                                    createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                    linearSystem2.addConstraint(createRow);
                                }
                            }
                            constraintWidget6 = constraintWidget13;
                        }
                    }
                    SolverVariable solverVariable6;
                    if (constraintWidget7 == null && (constraintWidget7 == constraintWidget8 || obj4 != null)) {
                        ConstraintWidget constraintWidget19;
                        constraintAnchor4 = constraintWidget17.mListAnchors[i2];
                        i4 = i2 + 1;
                        constraintAnchor5 = constraintWidget4.mListAnchors[i4];
                        solverVariable4 = constraintWidget17.mListAnchors[i2].mTarget != null ? constraintWidget17.mListAnchors[i2].mTarget.mSolverVariable : constraintWidget18;
                        solverVariable5 = constraintWidget4.mListAnchors[i4].mTarget != null ? constraintWidget4.mListAnchors[i4].mTarget.mSolverVariable : constraintWidget18;
                        if (constraintWidget7 == constraintWidget8) {
                            constraintAnchor4 = constraintWidget7.mListAnchors[i2];
                            constraintAnchor5 = constraintWidget7.mListAnchors[i4];
                        }
                        if (solverVariable4 == null || solverVariable5 == null) {
                            constraintWidget19 = constraintWidget7;
                        } else {
                            float f4;
                            if (i == 0) {
                                f4 = constraintWidget11.mHorizontalBiasPercent;
                            } else {
                                f4 = constraintWidget11.mVerticalBiasPercent;
                            }
                            i3 = constraintAnchor4.getMargin();
                            if (constraintWidget8 == null) {
                                constraintWidget8 = constraintWidget4;
                            }
                            i7 = constraintWidget8.mListAnchors[i4].getMargin();
                            solverVariable2 = constraintAnchor4.mSolverVariable;
                            SolverVariable solverVariable7 = constraintAnchor5.mSolverVariable;
                            solverVariable = solverVariable2;
                            solverVariable2 = solverVariable4;
                            i5 = i3;
                            solverVariable6 = solverVariable7;
                            constraintWidget19 = constraintWidget7;
                            linearSystem2.addCentering(solverVariable, solverVariable2, i5, f4, solverVariable5, solverVariable6, i7, 5);
                        }
                        constraintWidget18 = constraintWidget19;
                    } else if (obj5 != null || constraintWidget7 == null) {
                        constraintWidget18 = constraintWidget7;
                        if (!(obj6 == null || constraintWidget18 == null)) {
                            constraintWidget13 = constraintWidget18;
                            constraintWidget7 = constraintWidget13;
                            while (constraintWidget7 != null) {
                                constraintWidget14 = constraintWidget7.mListNextVisibleWidget[i];
                                if (constraintWidget7 != constraintWidget18 || constraintWidget7 == constraintWidget8 || constraintWidget14 == null) {
                                    constraintWidget15 = constraintWidget7;
                                    constraintWidget7 = constraintWidget14;
                                } else {
                                    ConstraintWidget constraintWidget20;
                                    SolverVariable solverVariable8;
                                    SolverVariable solverVariable9;
                                    constraintWidget6 = constraintWidget14 == constraintWidget8 ? null : constraintWidget14;
                                    constraintAnchor5 = constraintWidget7.mListAnchors[i2];
                                    solverVariable2 = constraintAnchor5.mSolverVariable;
                                    if (constraintAnchor5.mTarget != null) {
                                        solverVariable4 = constraintAnchor5.mTarget.mSolverVariable;
                                    }
                                    r4 = i2 + 1;
                                    solverVariable4 = constraintWidget13.mListAnchors[r4].mSolverVariable;
                                    i6 = constraintAnchor5.getMargin();
                                    r5 = constraintWidget7.mListAnchors[r4].getMargin();
                                    if (constraintWidget6 != null) {
                                        constraintAnchor = constraintWidget6.mListAnchors[i2];
                                        constraintWidget20 = constraintWidget6;
                                        solverVariable8 = constraintAnchor.mSolverVariable;
                                        solverVariable9 = constraintAnchor.mTarget != null ? constraintAnchor.mTarget.mSolverVariable : null;
                                    } else {
                                        ConstraintAnchor constraintAnchor8;
                                        constraintWidget20 = constraintWidget6;
                                        constraintAnchor = constraintWidget7.mListAnchors[r4].mTarget;
                                        if (constraintAnchor != null) {
                                            solverVariable9 = constraintAnchor.mSolverVariable;
                                            constraintAnchor8 = constraintAnchor;
                                        } else {
                                            constraintAnchor8 = constraintAnchor;
                                            solverVariable9 = null;
                                        }
                                        solverVariable8 = solverVariable9;
                                        solverVariable9 = constraintWidget7.mListAnchors[r4].mSolverVariable;
                                        constraintAnchor = constraintAnchor8;
                                    }
                                    if (constraintAnchor != null) {
                                        r5 += constraintAnchor.getMargin();
                                    }
                                    int i11 = r5;
                                    if (constraintWidget13 != null) {
                                        i6 += constraintWidget13.mListAnchors[r4].getMargin();
                                    }
                                    r4 = i6;
                                    if (solverVariable2 == null || solverVariable4 == null || solverVariable8 == null || solverVariable9 == null) {
                                        constraintWidget15 = constraintWidget7;
                                        r19 = constraintWidget20;
                                    } else {
                                        solverVariable6 = solverVariable9;
                                        r19 = constraintWidget20;
                                        i7 = i11;
                                        constraintWidget15 = constraintWidget7;
                                        linearSystem2.addCentering(solverVariable2, solverVariable4, r4, 0.5f, solverVariable8, solverVariable6, i7, 4);
                                    }
                                    constraintWidget7 = r19;
                                }
                                constraintWidget13 = constraintWidget15;
                            }
                            constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                            constraintAnchor5 = constraintWidget17.mListAnchors[i2].mTarget;
                            i5 = i2 + 1;
                            constraintAnchor6 = constraintWidget8.mListAnchors[i5];
                            constraintAnchor7 = constraintWidget4.mListAnchors[i5].mTarget;
                            if (constraintAnchor5 == null) {
                                if (constraintWidget18 == constraintWidget8) {
                                    i8 = 5;
                                    linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 5);
                                } else {
                                    i8 = 5;
                                    if (constraintAnchor7 != null) {
                                        constraintWidget16 = constraintWidget4;
                                        i9 = 5;
                                        linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, constraintAnchor6.getMargin(), 5);
                                    }
                                }
                                constraintWidget16 = constraintWidget4;
                                i9 = i8;
                            } else {
                                constraintWidget16 = constraintWidget4;
                                i9 = 5;
                            }
                            if (!(constraintAnchor7 == null || constraintWidget18 == constraintWidget8)) {
                                linearSystem2.addEquality(constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, -constraintAnchor6.getMargin(), i9);
                            }
                            constraintWidget4 = constraintWidget8;
                            if ((obj5 == null || obj6 != null) && constraintWidget18 != null) {
                                constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                                i4 = i2 + 1;
                                constraintAnchor5 = constraintWidget4.mListAnchors[i4];
                                solverVariable4 = constraintAnchor4.mTarget != null ? constraintAnchor4.mTarget.mSolverVariable : null;
                                solverVariable5 = constraintAnchor5.mTarget != null ? constraintAnchor5.mTarget.mSolverVariable : null;
                                if (constraintWidget18 == constraintWidget4) {
                                    constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                                    constraintAnchor5 = constraintWidget18.mListAnchors[i4];
                                }
                                if (solverVariable4 != null && solverVariable5 != null) {
                                    i3 = constraintAnchor4.getMargin();
                                    if (constraintWidget4 == null) {
                                        constraintWidget4 = constraintWidget16;
                                    }
                                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, solverVariable4, i3, 0.5f, solverVariable5, constraintAnchor5.mSolverVariable, constraintWidget4.mListAnchors[i4].getMargin(), 5);
                                    return;
                                }
                            }
                            return;
                        }
                    } else {
                        constraintWidget13 = constraintWidget7;
                        constraintWidget6 = constraintWidget13;
                        while (constraintWidget6 != null) {
                            ConstraintWidget constraintWidget21;
                            ConstraintAnchor constraintAnchor9;
                            ConstraintWidget constraintWidget22;
                            SolverVariable solverVariable10;
                            constraintWidget3 = constraintWidget6.mListNextVisibleWidget[i];
                            if (constraintWidget3 == null) {
                                if (constraintWidget6 != constraintWidget8) {
                                    constraintWidget21 = constraintWidget3;
                                    r19 = constraintWidget6;
                                    constraintWidget18 = constraintWidget7;
                                    constraintWidget7 = constraintWidget18;
                                    constraintWidget13 = r19;
                                    constraintWidget6 = constraintWidget21;
                                    constraintWidget18 = null;
                                }
                            }
                            constraintAnchor5 = constraintWidget6.mListAnchors[i2];
                            solverVariable2 = constraintAnchor5.mSolverVariable;
                            solverVariable4 = constraintAnchor5.mTarget != null ? constraintAnchor5.mTarget.mSolverVariable : constraintWidget18;
                            if (constraintWidget13 != constraintWidget6) {
                                solverVariable4 = constraintWidget13.mListAnchors[i2 + 1].mSolverVariable;
                            } else if (constraintWidget6 == constraintWidget7 && constraintWidget13 == constraintWidget6) {
                                solverVariable4 = constraintWidget17.mListAnchors[i2].mTarget != null ? constraintWidget17.mListAnchors[i2].mTarget.mSolverVariable : constraintWidget18;
                            }
                            i6 = constraintAnchor5.getMargin();
                            r5 = i2 + 1;
                            r4 = constraintWidget6.mListAnchors[r5].getMargin();
                            if (constraintWidget3 != null) {
                                constraintAnchor9 = constraintWidget3.mListAnchors[i2];
                                constraintWidget22 = constraintWidget3;
                                solverVariable10 = constraintAnchor9.mSolverVariable;
                                solverVariable6 = constraintAnchor9.mTarget != null ? constraintAnchor9.mTarget.mSolverVariable : null;
                            } else {
                                constraintWidget22 = constraintWidget3;
                                constraintAnchor9 = constraintWidget4.mListAnchors[r5].mTarget;
                                SolverVariable solverVariable11 = constraintAnchor9 != null ? constraintAnchor9.mSolverVariable : null;
                                solverVariable6 = constraintWidget6.mListAnchors[r5].mSolverVariable;
                                solverVariable10 = solverVariable11;
                            }
                            if (constraintAnchor9 != null) {
                                r4 += constraintAnchor9.getMargin();
                            }
                            if (constraintWidget13 != null) {
                                i6 += constraintWidget13.mListAnchors[r5].getMargin();
                            }
                            if (solverVariable2 == null || solverVariable4 == null || solverVariable10 == null || solverVariable6 == null) {
                                r19 = constraintWidget6;
                                constraintWidget18 = constraintWidget7;
                                constraintWidget21 = constraintWidget22;
                                constraintWidget7 = constraintWidget18;
                                constraintWidget13 = r19;
                                constraintWidget6 = constraintWidget21;
                                constraintWidget18 = null;
                            } else {
                                r19 = constraintWidget6;
                                constraintWidget21 = constraintWidget22;
                                constraintWidget18 = constraintWidget7;
                                linearSystem2.addCentering(solverVariable2, solverVariable4, constraintWidget6 == constraintWidget7 ? constraintWidget7.mListAnchors[i2].getMargin() : i6, 0.5f, solverVariable10, solverVariable6, constraintWidget6 == constraintWidget8 ? constraintWidget8.mListAnchors[r5].getMargin() : r4, 4);
                                constraintWidget7 = constraintWidget18;
                                constraintWidget13 = r19;
                                constraintWidget6 = constraintWidget21;
                                constraintWidget18 = null;
                            }
                        }
                        constraintWidget18 = constraintWidget7;
                    }
                    constraintWidget16 = constraintWidget4;
                    constraintWidget4 = constraintWidget8;
                    if (obj5 == null) {
                    }
                    constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                    i4 = i2 + 1;
                    constraintAnchor5 = constraintWidget4.mListAnchors[i4];
                    if (constraintAnchor4.mTarget != null) {
                    }
                    if (constraintAnchor5.mTarget != null) {
                    }
                    if (constraintWidget18 == constraintWidget4) {
                        constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                        constraintAnchor5 = constraintWidget18.mListAnchors[i4];
                    }
                    if (solverVariable4 != null) {
                    }
                }
            }
            if (obj9 != null) {
                i6 = i2 + 1;
                linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].getMargin(), 6);
            }
            if (i3 > 0) {
                while (constraintWidget6 != null) {
                    constraintWidget13 = constraintWidget6.mListNextMatchConstraintsWidget[i];
                    if (constraintWidget13 == null) {
                        f2 = constraintWidget6.mWeight[i];
                        f3 = constraintWidget13.mWeight[i];
                        solverVariable = constraintWidget6.mListAnchors[i2].mSolverVariable;
                        i5 = i2 + 1;
                        solverVariable2 = constraintWidget6.mListAnchors[i5].mSolverVariable;
                        solverVariable3 = constraintWidget13.mListAnchors[i2].mSolverVariable;
                        solverVariable4 = constraintWidget13.mListAnchors[i5].mSolverVariable;
                        if (i != 0) {
                            i3 = constraintWidget6.mMatchConstraintDefaultHeight;
                            i7 = constraintWidget13.mMatchConstraintDefaultHeight;
                        } else {
                            i3 = constraintWidget6.mMatchConstraintDefaultWidth;
                            i7 = constraintWidget13.mMatchConstraintDefaultWidth;
                        }
                        if (i7 != 0) {
                            if (i7 == 3) {
                            }
                            obj8 = null;
                            if (obj8 == null) {
                                createRow = linearSystem.createRow();
                                createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                linearSystem2.addConstraint(createRow);
                            }
                        }
                        obj8 = 1;
                        if (obj8 == null) {
                            createRow = linearSystem.createRow();
                            createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                            linearSystem2.addConstraint(createRow);
                        }
                    }
                    constraintWidget6 = constraintWidget13;
                }
            }
            if (constraintWidget7 == null) {
            }
            if (obj5 != null) {
            }
            constraintWidget18 = constraintWidget7;
            constraintWidget13 = constraintWidget18;
            constraintWidget7 = constraintWidget13;
            while (constraintWidget7 != null) {
                constraintWidget14 = constraintWidget7.mListNextVisibleWidget[i];
                if (constraintWidget7 != constraintWidget18) {
                }
                constraintWidget15 = constraintWidget7;
                constraintWidget7 = constraintWidget14;
                constraintWidget13 = constraintWidget15;
            }
            constraintAnchor4 = constraintWidget18.mListAnchors[i2];
            constraintAnchor5 = constraintWidget17.mListAnchors[i2].mTarget;
            i5 = i2 + 1;
            constraintAnchor6 = constraintWidget8.mListAnchors[i5];
            constraintAnchor7 = constraintWidget4.mListAnchors[i5].mTarget;
            if (constraintAnchor5 == null) {
                constraintWidget16 = constraintWidget4;
                i9 = 5;
            } else {
                if (constraintWidget18 == constraintWidget8) {
                    i8 = 5;
                    if (constraintAnchor7 != null) {
                        constraintWidget16 = constraintWidget4;
                        i9 = 5;
                        linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, constraintAnchor6.getMargin(), 5);
                    }
                } else {
                    i8 = 5;
                    linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 5);
                }
                constraintWidget16 = constraintWidget4;
                i9 = i8;
            }
            linearSystem2.addEquality(constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, -constraintAnchor6.getMargin(), i9);
            constraintWidget4 = constraintWidget8;
            if (obj5 == null) {
            }
            constraintAnchor4 = constraintWidget18.mListAnchors[i2];
            i4 = i2 + 1;
            constraintAnchor5 = constraintWidget4.mListAnchors[i4];
            if (constraintAnchor4.mTarget != null) {
            }
            if (constraintAnchor5.mTarget != null) {
            }
            if (constraintWidget18 == constraintWidget4) {
                constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                constraintAnchor5 = constraintWidget18.mListAnchors[i4];
            }
            if (solverVariable4 != null) {
            }
        }
        obj = 1;
        obj4 = obj;
        f = 0.0f;
        obj5 = obj2;
        obj6 = obj3;
        constraintWidget4 = constraintWidget17;
        constraintWidget5 = null;
        constraintWidget6 = constraintWidget5;
        constraintWidget7 = constraintWidget6;
        constraintWidget8 = constraintWidget7;
        obj = null;
        i3 = 0;
        while (obj == null) {
            constraintWidget4.mListNextVisibleWidget[i] = constraintWidget18;
            if (constraintWidget4.getVisibility() != 8) {
                if (constraintWidget8 != null) {
                    constraintWidget8.mListNextVisibleWidget[i] = constraintWidget4;
                }
                if (constraintWidget7 == null) {
                    constraintWidget7 = constraintWidget4;
                }
                constraintWidget8 = constraintWidget4;
            }
            constraintAnchor2 = constraintWidget4.mListAnchors[i2];
            margin = constraintAnchor2.getMargin();
            if (constraintAnchor2.mTarget != null) {
            }
            obj7 = obj;
            i4 = margin;
            if (obj4 != null) {
            }
            if (constraintWidget4 != constraintWidget7) {
                constraintWidget11 = constraintWidget2;
                constraintWidget9 = constraintWidget7;
                constraintWidget10 = constraintWidget8;
                linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, 6);
            } else {
                constraintWidget9 = constraintWidget7;
                constraintWidget10 = constraintWidget8;
                constraintWidget11 = constraintWidget2;
                linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, 5);
            }
            linearSystem2.addEquality(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, i4, i10);
            constraintWidget18 = null;
            constraintWidget4.mListNextMatchConstraintsWidget[i] = null;
            i3++;
            f += constraintWidget4.mWeight[i];
            if (constraintWidget6 != null) {
                constraintWidget5.mListNextMatchConstraintsWidget[i] = constraintWidget4;
            } else {
                constraintWidget6 = constraintWidget4;
            }
            if (obj9 != null) {
                linearSystem2.addGreaterThan(constraintWidget4.mListAnchors[i2 + 1].mSolverVariable, constraintWidget4.mListAnchors[i2].mSolverVariable, 0, 6);
            }
            constraintWidget5 = constraintWidget4;
            if (obj9 != null) {
                linearSystem2.addGreaterThan(constraintWidget4.mListAnchors[i2].mSolverVariable, constraintWidgetContainer2.mListAnchors[i2].mSolverVariable, 0, 6);
            }
            constraintAnchor3 = constraintWidget4.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor3 != null) {
                constraintWidget12 = constraintAnchor3.mOwner;
                if (constraintWidget12.mListAnchors[i2].mTarget != null) {
                }
            }
            constraintWidget12 = null;
            if (constraintWidget12 == null) {
                obj = 1;
            } else {
                constraintWidget4 = constraintWidget12;
                obj = obj7;
            }
            constraintWidget7 = constraintWidget9;
            constraintWidget8 = constraintWidget10;
            constraintWidget2 = constraintWidget11;
        }
        constraintWidget11 = constraintWidget2;
        if (constraintWidget8 != null) {
            i5 = i2 + 1;
            if (constraintWidget4.mListAnchors[i5].mTarget != null) {
                constraintAnchor3 = constraintWidget8.mListAnchors[i5];
                linearSystem2.addLowerThan(constraintAnchor3.mSolverVariable, constraintWidget4.mListAnchors[i5].mTarget.mSolverVariable, -constraintAnchor3.getMargin(), 5);
                if (obj9 != null) {
                    i6 = i2 + 1;
                    linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].getMargin(), 6);
                }
                if (i3 > 0) {
                    while (constraintWidget6 != null) {
                        constraintWidget13 = constraintWidget6.mListNextMatchConstraintsWidget[i];
                        if (constraintWidget13 == null) {
                            f2 = constraintWidget6.mWeight[i];
                            f3 = constraintWidget13.mWeight[i];
                            solverVariable = constraintWidget6.mListAnchors[i2].mSolverVariable;
                            i5 = i2 + 1;
                            solverVariable2 = constraintWidget6.mListAnchors[i5].mSolverVariable;
                            solverVariable3 = constraintWidget13.mListAnchors[i2].mSolverVariable;
                            solverVariable4 = constraintWidget13.mListAnchors[i5].mSolverVariable;
                            if (i != 0) {
                                i3 = constraintWidget6.mMatchConstraintDefaultWidth;
                                i7 = constraintWidget13.mMatchConstraintDefaultWidth;
                            } else {
                                i3 = constraintWidget6.mMatchConstraintDefaultHeight;
                                i7 = constraintWidget13.mMatchConstraintDefaultHeight;
                            }
                            if (i7 != 0) {
                                if (i7 == 3) {
                                }
                                obj8 = null;
                                if (obj8 == null) {
                                    createRow = linearSystem.createRow();
                                    createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                    linearSystem2.addConstraint(createRow);
                                }
                            }
                            obj8 = 1;
                            if (obj8 == null) {
                                createRow = linearSystem.createRow();
                                createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                                linearSystem2.addConstraint(createRow);
                            }
                        }
                        constraintWidget6 = constraintWidget13;
                    }
                }
                if (constraintWidget7 == null) {
                }
                if (obj5 != null) {
                }
                constraintWidget18 = constraintWidget7;
                constraintWidget13 = constraintWidget18;
                constraintWidget7 = constraintWidget13;
                while (constraintWidget7 != null) {
                    constraintWidget14 = constraintWidget7.mListNextVisibleWidget[i];
                    if (constraintWidget7 != constraintWidget18) {
                    }
                    constraintWidget15 = constraintWidget7;
                    constraintWidget7 = constraintWidget14;
                    constraintWidget13 = constraintWidget15;
                }
                constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                constraintAnchor5 = constraintWidget17.mListAnchors[i2].mTarget;
                i5 = i2 + 1;
                constraintAnchor6 = constraintWidget8.mListAnchors[i5];
                constraintAnchor7 = constraintWidget4.mListAnchors[i5].mTarget;
                if (constraintAnchor5 == null) {
                    if (constraintWidget18 == constraintWidget8) {
                        i8 = 5;
                        linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 5);
                    } else {
                        i8 = 5;
                        if (constraintAnchor7 != null) {
                            constraintWidget16 = constraintWidget4;
                            i9 = 5;
                            linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, constraintAnchor6.getMargin(), 5);
                        }
                    }
                    constraintWidget16 = constraintWidget4;
                    i9 = i8;
                } else {
                    constraintWidget16 = constraintWidget4;
                    i9 = 5;
                }
                linearSystem2.addEquality(constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, -constraintAnchor6.getMargin(), i9);
                constraintWidget4 = constraintWidget8;
                if (obj5 == null) {
                }
                constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                i4 = i2 + 1;
                constraintAnchor5 = constraintWidget4.mListAnchors[i4];
                if (constraintAnchor4.mTarget != null) {
                }
                if (constraintAnchor5.mTarget != null) {
                }
                if (constraintWidget18 == constraintWidget4) {
                    constraintAnchor4 = constraintWidget18.mListAnchors[i2];
                    constraintAnchor5 = constraintWidget18.mListAnchors[i4];
                }
                if (solverVariable4 != null) {
                }
            }
        }
        if (obj9 != null) {
            i6 = i2 + 1;
            linearSystem2.addGreaterThan(constraintWidgetContainer2.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].mSolverVariable, constraintWidget4.mListAnchors[i6].getMargin(), 6);
        }
        if (i3 > 0) {
            while (constraintWidget6 != null) {
                constraintWidget13 = constraintWidget6.mListNextMatchConstraintsWidget[i];
                if (constraintWidget13 == null) {
                    f2 = constraintWidget6.mWeight[i];
                    f3 = constraintWidget13.mWeight[i];
                    solverVariable = constraintWidget6.mListAnchors[i2].mSolverVariable;
                    i5 = i2 + 1;
                    solverVariable2 = constraintWidget6.mListAnchors[i5].mSolverVariable;
                    solverVariable3 = constraintWidget13.mListAnchors[i2].mSolverVariable;
                    solverVariable4 = constraintWidget13.mListAnchors[i5].mSolverVariable;
                    if (i != 0) {
                        i3 = constraintWidget6.mMatchConstraintDefaultHeight;
                        i7 = constraintWidget13.mMatchConstraintDefaultHeight;
                    } else {
                        i3 = constraintWidget6.mMatchConstraintDefaultWidth;
                        i7 = constraintWidget13.mMatchConstraintDefaultWidth;
                    }
                    if (i7 != 0) {
                        if (i7 == 3) {
                        }
                        obj8 = null;
                        if (obj8 == null) {
                            createRow = linearSystem.createRow();
                            createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                            linearSystem2.addConstraint(createRow);
                        }
                    }
                    obj8 = 1;
                    if (obj8 == null) {
                        createRow = linearSystem.createRow();
                        createRow.createRowEqualMatchDimensions(f2, f, f3, solverVariable, solverVariable2, solverVariable3, solverVariable4);
                        linearSystem2.addConstraint(createRow);
                    }
                }
                constraintWidget6 = constraintWidget13;
            }
        }
        if (constraintWidget7 == null) {
        }
        if (obj5 != null) {
        }
        constraintWidget18 = constraintWidget7;
        constraintWidget13 = constraintWidget18;
        constraintWidget7 = constraintWidget13;
        while (constraintWidget7 != null) {
            constraintWidget14 = constraintWidget7.mListNextVisibleWidget[i];
            if (constraintWidget7 != constraintWidget18) {
            }
            constraintWidget15 = constraintWidget7;
            constraintWidget7 = constraintWidget14;
            constraintWidget13 = constraintWidget15;
        }
        constraintAnchor4 = constraintWidget18.mListAnchors[i2];
        constraintAnchor5 = constraintWidget17.mListAnchors[i2].mTarget;
        i5 = i2 + 1;
        constraintAnchor6 = constraintWidget8.mListAnchors[i5];
        constraintAnchor7 = constraintWidget4.mListAnchors[i5].mTarget;
        if (constraintAnchor5 == null) {
            constraintWidget16 = constraintWidget4;
            i9 = 5;
        } else {
            if (constraintWidget18 == constraintWidget8) {
                i8 = 5;
                if (constraintAnchor7 != null) {
                    constraintWidget16 = constraintWidget4;
                    i9 = 5;
                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 0.5f, constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, constraintAnchor6.getMargin(), 5);
                }
            } else {
                i8 = 5;
                linearSystem2.addEquality(constraintAnchor4.mSolverVariable, constraintAnchor5.mSolverVariable, constraintAnchor4.getMargin(), 5);
            }
            constraintWidget16 = constraintWidget4;
            i9 = i8;
        }
        linearSystem2.addEquality(constraintAnchor6.mSolverVariable, constraintAnchor7.mSolverVariable, -constraintAnchor6.getMargin(), i9);
        constraintWidget4 = constraintWidget8;
        if (obj5 == null) {
        }
        constraintAnchor4 = constraintWidget18.mListAnchors[i2];
        i4 = i2 + 1;
        constraintAnchor5 = constraintWidget4.mListAnchors[i4];
        if (constraintAnchor4.mTarget != null) {
        }
        if (constraintAnchor5.mTarget != null) {
        }
        if (constraintWidget18 == constraintWidget4) {
            constraintAnchor4 = constraintWidget18.mListAnchors[i2];
            constraintAnchor5 = constraintWidget18.mListAnchors[i4];
        }
        if (solverVariable4 != null) {
        }
    }
}
