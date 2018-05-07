package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    static final int FLAG_CHAIN_DANGLING = 1;
    static final int FLAG_RECOMPUTE_BOUNDS = 2;
    static final int FLAG_USE_OPTIMIZE = 0;
    public static final int OPTIMIZATION_BARRIER = 2;
    public static final int OPTIMIZATION_CHAIN = 4;
    public static final int OPTIMIZATION_DIMENSIONS = 8;
    public static final int OPTIMIZATION_DIRECT = 1;
    public static final int OPTIMIZATION_NONE = 0;
    public static final int OPTIMIZATION_RATIO = 16;
    public static final int OPTIMIZATION_STANDARD = 3;
    static boolean[] flags = new boolean[3];

    static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mListDimensionBehaviors[0] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_PARENT) {
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(i, width);
        }
        if (constraintWidgetContainer.mListDimensionBehaviors[1] != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_PARENT) {
            i = constraintWidget.mTop.mMargin;
            constraintWidgetContainer = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, constraintWidgetContainer);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(i, constraintWidgetContainer);
        }
    }

    private static boolean optimizableMatchConstraint(ConstraintWidget constraintWidget, int i) {
        if (constraintWidget.mListDimensionBehaviors[i] != DimensionBehaviour.MATCH_CONSTRAINT) {
            return false;
        }
        int i2 = 1;
        if (constraintWidget.mDimensionRatio != 0.0f) {
            constraintWidget = constraintWidget.mListDimensionBehaviors;
            if (i != 0) {
                i2 = 0;
            }
            return constraintWidget[i2] == DimensionBehaviour.MATCH_CONSTRAINT ? false : false;
        } else {
            if (i != 0) {
                if (constraintWidget.mMatchConstraintDefaultHeight == 0 && constraintWidget.mMatchConstraintMinHeight == 0) {
                    if (constraintWidget.mMatchConstraintMaxHeight != null) {
                    }
                }
                return false;
            } else if (constraintWidget.mMatchConstraintDefaultWidth == 0 && constraintWidget.mMatchConstraintMinWidth == 0 && constraintWidget.mMatchConstraintMaxWidth == null) {
                return true;
            } else {
                return false;
            }
            return true;
        }
    }

    static void analyze(int i, ConstraintWidget constraintWidget) {
        constraintWidget.updateResolutionNodes();
        ResolutionAnchor resolutionNode = constraintWidget.mLeft.getResolutionNode();
        ResolutionAnchor resolutionNode2 = constraintWidget.mTop.getResolutionNode();
        ResolutionAnchor resolutionNode3 = constraintWidget.mRight.getResolutionNode();
        ResolutionAnchor resolutionNode4 = constraintWidget.mBottom.getResolutionNode();
        i = (i & 8) == 8 ? 1 : 0;
        if (!(resolutionNode.type == 4 || resolutionNode3.type == 4)) {
            if (constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.FIXED) {
                if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (i != 0) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mRight.mTarget == null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    if (i != 0) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, constraintWidget.getWidth());
                    }
                } else if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget != null) {
                    resolutionNode.setType(1);
                    resolutionNode3.setType(1);
                    resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    if (i != 0) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -constraintWidget.getWidth());
                    }
                } else if (!(constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget == null)) {
                    resolutionNode.setType(2);
                    resolutionNode3.setType(2);
                    if (i != 0) {
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                        resolutionNode.setOpposite(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                        resolutionNode3.setOpposite(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.setOpposite(resolutionNode3, (float) (-constraintWidget.getWidth()));
                        resolutionNode3.setOpposite(resolutionNode, (float) constraintWidget.getWidth());
                    }
                }
            } else if (constraintWidget.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 0)) {
                int width = constraintWidget.getWidth();
                resolutionNode.setType(1);
                resolutionNode3.setType(1);
                if (constraintWidget.mLeft.mTarget == null && constraintWidget.mRight.mTarget == null) {
                    if (i != 0) {
                        resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode3.dependsOn(resolutionNode, width);
                    }
                } else if (constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget != null) {
                    if (constraintWidget.mLeft.mTarget != null || constraintWidget.mRight.mTarget == null) {
                        if (!(constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget == null)) {
                            if (i != 0) {
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode);
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode3);
                            }
                            if (constraintWidget.mDimensionRatio == 0.0f) {
                                resolutionNode.setType(3);
                                resolutionNode3.setType(3);
                                resolutionNode.setOpposite(resolutionNode3, 0.0f);
                                resolutionNode3.setOpposite(resolutionNode, 0.0f);
                            } else {
                                resolutionNode.setType(2);
                                resolutionNode3.setType(2);
                                resolutionNode.setOpposite(resolutionNode3, (float) (-width));
                                resolutionNode3.setOpposite(resolutionNode, (float) width);
                                constraintWidget.setWidth(width);
                            }
                        }
                    } else if (i != 0) {
                        resolutionNode.dependsOn(resolutionNode3, -1, constraintWidget.getResolutionWidth());
                    } else {
                        resolutionNode.dependsOn(resolutionNode3, -width);
                    }
                } else if (i != 0) {
                    resolutionNode3.dependsOn(resolutionNode, 1, constraintWidget.getResolutionWidth());
                } else {
                    resolutionNode3.dependsOn(resolutionNode, width);
                }
            }
        }
        if (resolutionNode2.type != 4 && resolutionNode4.type != 4) {
            if (constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.FIXED) {
                if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (i != 0) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                    }
                    if (constraintWidget.mBaseline.mTarget != 0) {
                        constraintWidget.mBaseline.getResolutionNode().setType(1);
                        resolutionNode2.dependsOn(1, constraintWidget.mBaseline.getResolutionNode(), -constraintWidget.mBaselineDistance);
                    }
                } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget == null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (i != 0) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, constraintWidget.getHeight());
                    }
                    if (constraintWidget.mBaselineDistance > 0) {
                        constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                    }
                } else if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget != null) {
                    resolutionNode2.setType(1);
                    resolutionNode4.setType(1);
                    if (i != 0) {
                        resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode2.dependsOn(resolutionNode4, -constraintWidget.getHeight());
                    }
                    if (constraintWidget.mBaselineDistance > 0) {
                        constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                    }
                } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget != null) {
                    resolutionNode2.setType(2);
                    resolutionNode4.setType(2);
                    if (i != 0) {
                        resolutionNode2.setOpposite(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                        resolutionNode4.setOpposite(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                        constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                        constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                    } else {
                        resolutionNode2.setOpposite(resolutionNode4, (float) (-constraintWidget.getHeight()));
                        resolutionNode4.setOpposite(resolutionNode2, (float) constraintWidget.getHeight());
                    }
                    if (constraintWidget.mBaselineDistance > 0) {
                        constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                    }
                }
            } else if (constraintWidget.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(constraintWidget, 1)) {
                int height = constraintWidget.getHeight();
                resolutionNode2.setType(1);
                resolutionNode4.setType(1);
                if (constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                    if (i != 0) {
                        resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode4.dependsOn(resolutionNode2, height);
                    }
                } else if (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget != null) {
                    if (constraintWidget.mTop.mTarget != null || constraintWidget.mBottom.mTarget == null) {
                        if (constraintWidget.mTop.mTarget != null && constraintWidget.mBottom.mTarget != null) {
                            if (i != 0) {
                                constraintWidget.getResolutionHeight().addDependent(resolutionNode2);
                                constraintWidget.getResolutionWidth().addDependent(resolutionNode4);
                            }
                            if (constraintWidget.mDimensionRatio == 0) {
                                resolutionNode2.setType(3);
                                resolutionNode4.setType(3);
                                resolutionNode2.setOpposite(resolutionNode4, 0.0f);
                                resolutionNode4.setOpposite(resolutionNode2, 0.0f);
                                return;
                            }
                            resolutionNode2.setType(2);
                            resolutionNode4.setType(2);
                            resolutionNode2.setOpposite(resolutionNode4, (float) (-height));
                            resolutionNode4.setOpposite(resolutionNode2, (float) height);
                            constraintWidget.setHeight(height);
                            if (constraintWidget.mBaselineDistance > 0) {
                                constraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionNode2, constraintWidget.mBaselineDistance);
                            }
                        }
                    } else if (i != 0) {
                        resolutionNode2.dependsOn(resolutionNode4, -1, constraintWidget.getResolutionHeight());
                    } else {
                        resolutionNode2.dependsOn(resolutionNode4, -height);
                    }
                } else if (i != 0) {
                    resolutionNode4.dependsOn(resolutionNode2, 1, constraintWidget.getResolutionHeight());
                } else {
                    resolutionNode4.dependsOn(resolutionNode2, height);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean applyChainOptimized(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ConstraintWidget constraintWidget) {
        ConstraintWidget constraintWidget2;
        Object obj;
        Object obj2;
        Object obj3;
        ConstraintWidget constraintWidget3;
        ConstraintWidget constraintWidget4;
        ConstraintWidget constraintWidget5;
        ConstraintWidget constraintWidget6;
        Object obj4;
        int i3;
        int i4;
        float f;
        float f2;
        float f3;
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget7;
        ResolutionAnchor resolutionNode;
        int i5;
        ResolutionAnchor resolutionNode2;
        float f4;
        float f5;
        float f6;
        ResolutionAnchor resolutionAnchor;
        Metrics metrics;
        ConstraintWidget constraintWidget8;
        float width;
        float f7;
        Metrics metrics2;
        LinearSystem linearSystem2 = linearSystem;
        DimensionBehaviour dimensionBehaviour = constraintWidgetContainer.mListDimensionBehaviors[i];
        DimensionBehaviour dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
        if (i == 0 && constraintWidgetContainer.isRtl()) {
            constraintWidget2 = constraintWidget;
            obj = null;
            while (obj == null) {
                ConstraintWidget constraintWidget9;
                ConstraintAnchor constraintAnchor2 = constraintWidget2.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor2 != null) {
                    constraintWidget9 = constraintAnchor2.mOwner;
                    if (constraintWidget9.mListAnchors[i2].mTarget != null) {
                    }
                }
                constraintWidget9 = null;
                if (constraintWidget9 != null) {
                    constraintWidget2 = constraintWidget9;
                } else {
                    obj = 1;
                }
            }
        } else {
            constraintWidget2 = constraintWidget;
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
            constraintWidget3 = constraintWidget;
            constraintWidget2 = null;
            constraintWidget4 = constraintWidget2;
            constraintWidget5 = constraintWidget4;
            constraintWidget6 = constraintWidget5;
            obj4 = null;
            i3 = 0;
            i4 = 0;
            f = 0.0f;
            f2 = 0.0f;
            f3 = 0.0f;
            while (obj4 == null) {
                constraintWidget3.mListNextVisibleWidget[i] = null;
                if (constraintWidget3.getVisibility() != 8) {
                    if (constraintWidget4 != null) {
                        constraintWidget4.mListNextVisibleWidget[i] = constraintWidget3;
                    }
                    if (constraintWidget5 == null) {
                        constraintWidget5 = constraintWidget3;
                    }
                    i3++;
                    if (i != 0) {
                        f += (float) constraintWidget3.getWidth();
                    } else {
                        f += (float) constraintWidget3.getHeight();
                    }
                    if (constraintWidget3 != constraintWidget5) {
                        f += (float) constraintWidget3.mListAnchors[i2].getMargin();
                    }
                    f2 = (f2 + ((float) constraintWidget3.mListAnchors[i2].getMargin())) + ((float) constraintWidget3.mListAnchors[i2 + 1].getMargin());
                    constraintWidget4 = constraintWidget3;
                }
                ConstraintAnchor constraintAnchor3 = constraintWidget3.mListAnchors[i2];
                constraintWidget3.mListNextMatchConstraintsWidget[i] = null;
                if (constraintWidget3.getVisibility() != 8 && constraintWidget3.mListDimensionBehaviors[i] == DimensionBehaviour.MATCH_CONSTRAINT) {
                    i4++;
                    if (i == 0) {
                        if (constraintWidget3.mMatchConstraintDefaultWidth != 0) {
                            if (!(constraintWidget3.mMatchConstraintMinWidth == 0 && constraintWidget3.mMatchConstraintMaxWidth == 0)) {
                            }
                        }
                    } else if (constraintWidget3.mMatchConstraintDefaultHeight != 0) {
                        return false;
                    } else {
                        if (constraintWidget3.mMatchConstraintMinHeight != 0) {
                            if (constraintWidget3.mMatchConstraintMaxHeight != 0) {
                            }
                        }
                        return false;
                    }
                    f3 += constraintWidget3.mWeight[i];
                    if (constraintWidget6 != null) {
                        constraintWidget6 = constraintWidget3;
                    } else {
                        constraintWidget2.mListNextMatchConstraintsWidget[i] = constraintWidget3;
                    }
                    constraintWidget2 = constraintWidget3;
                }
                constraintAnchor = constraintWidget3.mListAnchors[i2 + 1].mTarget;
                if (constraintAnchor != null) {
                    constraintWidget7 = constraintAnchor.mOwner;
                    if (constraintWidget7.mListAnchors[i2].mTarget != null) {
                    }
                }
                constraintWidget7 = null;
                if (constraintWidget7 == null) {
                    constraintWidget3 = constraintWidget7;
                } else {
                    obj4 = 1;
                }
            }
            resolutionNode = constraintWidget.mListAnchors[i2].getResolutionNode();
            i5 = i2 + 1;
            resolutionNode2 = constraintWidget3.mListAnchors[i5].getResolutionNode();
            if (resolutionNode.target != null) {
                if (resolutionNode2.target == null) {
                    if (resolutionNode.target.state == 1 && resolutionNode2.target.state != 1) {
                        return false;
                    }
                    if (i4 <= 0 && i4 != i3) {
                        return false;
                    }
                    if (obj == null && obj2 == null) {
                        if (obj3 != null) {
                            f4 = 0.0f;
                            f5 = resolutionNode.target.resolvedOffset;
                            f6 = resolutionNode2.target.resolvedOffset;
                            f6 = f5 >= f6 ? (f6 - f5) - f : (f5 - f6) - f;
                            if (i4 > 0 || i4 != i3) {
                                resolutionAnchor = resolutionNode;
                                if (f6 >= f) {
                                    return false;
                                }
                                if (obj == null) {
                                    f5 += (f6 - f4) * constraintWidget.getHorizontalBiasPercent();
                                    while (constraintWidget5 != null) {
                                        if (LinearSystem.sMetrics != null) {
                                            metrics = LinearSystem.sMetrics;
                                            metrics.nonresolvedWidgets--;
                                            metrics = LinearSystem.sMetrics;
                                            metrics.resolvedWidgets++;
                                            metrics = LinearSystem.sMetrics;
                                            metrics.chainConnectionResolved++;
                                        }
                                        constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                                        if (constraintWidget8 == null || constraintWidget5 == constraintWidget4) {
                                            if (i != 0) {
                                                width = (float) constraintWidget5.getWidth();
                                            } else {
                                                width = (float) constraintWidget5.getHeight();
                                            }
                                            f5 += (float) constraintWidget5.mListAnchors[i2].getMargin();
                                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                                            f5 += width;
                                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                                            f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                                        }
                                        constraintWidget5 = constraintWidget8;
                                    }
                                } else if (!(obj2 == null && obj3 == null)) {
                                    if (obj2 == null) {
                                        f6 -= f4;
                                    } else if (obj3 != null) {
                                        f6 -= f4;
                                    }
                                    f7 = f6 / ((float) (i3 + 1));
                                    if (obj3 != null) {
                                        f7 = i3 <= 1 ? f6 / ((float) (i3 - 1)) : f6 / 2.0f;
                                    }
                                    width = f5 + f7;
                                    if (obj3 != null && i3 > 1) {
                                        width = ((float) constraintWidget5.mListAnchors[i2].getMargin()) + f5;
                                    }
                                    if (!(obj2 == null || constraintWidget5 == null)) {
                                        width += (float) constraintWidget5.mListAnchors[i2].getMargin();
                                    }
                                    while (constraintWidget5 != null) {
                                        if (LinearSystem.sMetrics != null) {
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.nonresolvedWidgets--;
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.resolvedWidgets++;
                                            metrics2 = LinearSystem.sMetrics;
                                            metrics2.chainConnectionResolved++;
                                        }
                                        constraintWidget7 = constraintWidget5.mListNextVisibleWidget[i];
                                        if (constraintWidget7 == null || constraintWidget5 == constraintWidget4) {
                                            if (i != 0) {
                                                f6 = (float) constraintWidget5.getWidth();
                                            } else {
                                                f6 = (float) constraintWidget5.getHeight();
                                            }
                                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width);
                                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width + f6);
                                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                                            width += f6 + f7;
                                        }
                                        constraintWidget5 = constraintWidget7;
                                    }
                                }
                                return true;
                            } else if (constraintWidget3.getParent() != null && constraintWidget3.getParent().mListDimensionBehaviors[i] == DimensionBehaviour.WRAP_CONTENT) {
                                return false;
                            } else {
                                f6 = (f6 + f) - f2;
                                if (obj2 != null) {
                                    f6 -= f2 - f4;
                                }
                                if (obj2 != null) {
                                    f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                                    constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                                    if (constraintWidget8 != null) {
                                        f5 += (float) constraintWidget8.mListAnchors[i2].getMargin();
                                    }
                                }
                                while (constraintWidget5 != null) {
                                    ResolutionAnchor resolutionAnchor2;
                                    if (LinearSystem.sMetrics != null) {
                                        metrics = LinearSystem.sMetrics;
                                        resolutionAnchor2 = resolutionNode;
                                        metrics.nonresolvedWidgets--;
                                        metrics = LinearSystem.sMetrics;
                                        metrics.resolvedWidgets++;
                                        metrics = LinearSystem.sMetrics;
                                        metrics.chainConnectionResolved++;
                                    } else {
                                        resolutionAnchor2 = resolutionNode;
                                    }
                                    constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                                    if (constraintWidget8 == null) {
                                        if (constraintWidget5 != constraintWidget4) {
                                            resolutionAnchor = resolutionAnchor2;
                                            constraintWidget5 = constraintWidget8;
                                            resolutionNode = resolutionAnchor;
                                        }
                                    }
                                    width = f6 / ((float) i4);
                                    if (f3 > 0.0f) {
                                        width = (constraintWidget5.mWeight[i] * f6) / f3;
                                    }
                                    f5 += (float) constraintWidget5.mListAnchors[i2].getMargin();
                                    resolutionAnchor = resolutionAnchor2;
                                    constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                                    f5 += width;
                                    constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                                    constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                                    constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                                    f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                                    constraintWidget5 = constraintWidget8;
                                    resolutionNode = resolutionAnchor;
                                }
                                return true;
                            }
                        }
                    }
                    f4 = constraintWidget5 == null ? (float) constraintWidget5.mListAnchors[i2].getMargin() : 0.0f;
                    if (constraintWidget4 != null) {
                        f4 += (float) constraintWidget4.mListAnchors[i5].getMargin();
                    }
                    f5 = resolutionNode.target.resolvedOffset;
                    f6 = resolutionNode2.target.resolvedOffset;
                    if (f5 >= f6) {
                    }
                    if (i4 > 0) {
                    }
                    resolutionAnchor = resolutionNode;
                    if (f6 >= f) {
                        return false;
                    }
                    if (obj == null) {
                        if (obj2 == null) {
                            f6 -= f4;
                        } else if (obj3 != null) {
                            f6 -= f4;
                        }
                        f7 = f6 / ((float) (i3 + 1));
                        if (obj3 != null) {
                            if (i3 <= 1) {
                            }
                        }
                        width = f5 + f7;
                        width = ((float) constraintWidget5.mListAnchors[i2].getMargin()) + f5;
                        width += (float) constraintWidget5.mListAnchors[i2].getMargin();
                        while (constraintWidget5 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.nonresolvedWidgets--;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.resolvedWidgets++;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.chainConnectionResolved++;
                            }
                            constraintWidget7 = constraintWidget5.mListNextVisibleWidget[i];
                            if (constraintWidget7 == null) {
                            }
                            if (i != 0) {
                                f6 = (float) constraintWidget5.getHeight();
                            } else {
                                f6 = (float) constraintWidget5.getWidth();
                            }
                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width + f6);
                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                            width += f6 + f7;
                            constraintWidget5 = constraintWidget7;
                        }
                    } else {
                        f5 += (f6 - f4) * constraintWidget.getHorizontalBiasPercent();
                        while (constraintWidget5 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics = LinearSystem.sMetrics;
                                metrics.nonresolvedWidgets--;
                                metrics = LinearSystem.sMetrics;
                                metrics.resolvedWidgets++;
                                metrics = LinearSystem.sMetrics;
                                metrics.chainConnectionResolved++;
                            }
                            constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                            if (constraintWidget8 == null) {
                            }
                            if (i != 0) {
                                width = (float) constraintWidget5.getHeight();
                            } else {
                                width = (float) constraintWidget5.getWidth();
                            }
                            f5 += (float) constraintWidget5.mListAnchors[i2].getMargin();
                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                            f5 += width;
                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                            f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                            constraintWidget5 = constraintWidget8;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        obj = 1;
        constraintWidget3 = constraintWidget;
        constraintWidget2 = null;
        constraintWidget4 = constraintWidget2;
        constraintWidget5 = constraintWidget4;
        constraintWidget6 = constraintWidget5;
        obj4 = null;
        i3 = 0;
        i4 = 0;
        f = 0.0f;
        f2 = 0.0f;
        f3 = 0.0f;
        while (obj4 == null) {
            constraintWidget3.mListNextVisibleWidget[i] = null;
            if (constraintWidget3.getVisibility() != 8) {
                if (constraintWidget4 != null) {
                    constraintWidget4.mListNextVisibleWidget[i] = constraintWidget3;
                }
                if (constraintWidget5 == null) {
                    constraintWidget5 = constraintWidget3;
                }
                i3++;
                if (i != 0) {
                    f += (float) constraintWidget3.getHeight();
                } else {
                    f += (float) constraintWidget3.getWidth();
                }
                if (constraintWidget3 != constraintWidget5) {
                    f += (float) constraintWidget3.mListAnchors[i2].getMargin();
                }
                f2 = (f2 + ((float) constraintWidget3.mListAnchors[i2].getMargin())) + ((float) constraintWidget3.mListAnchors[i2 + 1].getMargin());
                constraintWidget4 = constraintWidget3;
            }
            ConstraintAnchor constraintAnchor32 = constraintWidget3.mListAnchors[i2];
            constraintWidget3.mListNextMatchConstraintsWidget[i] = null;
            i4++;
            if (i == 0) {
                return constraintWidget3.mMatchConstraintDefaultWidth != 0 ? false : false;
            } else {
                if (constraintWidget3.mMatchConstraintDefaultHeight != 0) {
                    return false;
                }
                if (constraintWidget3.mMatchConstraintMinHeight != 0) {
                    if (constraintWidget3.mMatchConstraintMaxHeight != 0) {
                    }
                }
                return false;
            }
            f3 += constraintWidget3.mWeight[i];
            if (constraintWidget6 != null) {
                constraintWidget2.mListNextMatchConstraintsWidget[i] = constraintWidget3;
            } else {
                constraintWidget6 = constraintWidget3;
            }
            constraintWidget2 = constraintWidget3;
            constraintAnchor = constraintWidget3.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor != null) {
                constraintWidget7 = constraintAnchor.mOwner;
                if (constraintWidget7.mListAnchors[i2].mTarget != null) {
                }
            }
            constraintWidget7 = null;
            if (constraintWidget7 == null) {
                obj4 = 1;
            } else {
                constraintWidget3 = constraintWidget7;
            }
        }
        resolutionNode = constraintWidget.mListAnchors[i2].getResolutionNode();
        i5 = i2 + 1;
        resolutionNode2 = constraintWidget3.mListAnchors[i5].getResolutionNode();
        if (resolutionNode.target != null) {
            if (resolutionNode2.target == null) {
                if (resolutionNode.target.state == 1) {
                }
                if (i4 <= 0) {
                }
                if (obj3 != null) {
                    f4 = 0.0f;
                    f5 = resolutionNode.target.resolvedOffset;
                    f6 = resolutionNode2.target.resolvedOffset;
                    if (f5 >= f6) {
                    }
                    if (i4 > 0) {
                    }
                    resolutionAnchor = resolutionNode;
                    if (f6 >= f) {
                        return false;
                    }
                    if (obj == null) {
                        f5 += (f6 - f4) * constraintWidget.getHorizontalBiasPercent();
                        while (constraintWidget5 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics = LinearSystem.sMetrics;
                                metrics.nonresolvedWidgets--;
                                metrics = LinearSystem.sMetrics;
                                metrics.resolvedWidgets++;
                                metrics = LinearSystem.sMetrics;
                                metrics.chainConnectionResolved++;
                            }
                            constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                            if (constraintWidget8 == null) {
                            }
                            if (i != 0) {
                                width = (float) constraintWidget5.getWidth();
                            } else {
                                width = (float) constraintWidget5.getHeight();
                            }
                            f5 += (float) constraintWidget5.mListAnchors[i2].getMargin();
                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                            f5 += width;
                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                            f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                            constraintWidget5 = constraintWidget8;
                        }
                    } else {
                        if (obj2 == null) {
                            f6 -= f4;
                        } else if (obj3 != null) {
                            f6 -= f4;
                        }
                        f7 = f6 / ((float) (i3 + 1));
                        if (obj3 != null) {
                            if (i3 <= 1) {
                            }
                        }
                        width = f5 + f7;
                        width = ((float) constraintWidget5.mListAnchors[i2].getMargin()) + f5;
                        width += (float) constraintWidget5.mListAnchors[i2].getMargin();
                        while (constraintWidget5 != null) {
                            if (LinearSystem.sMetrics != null) {
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.nonresolvedWidgets--;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.resolvedWidgets++;
                                metrics2 = LinearSystem.sMetrics;
                                metrics2.chainConnectionResolved++;
                            }
                            constraintWidget7 = constraintWidget5.mListNextVisibleWidget[i];
                            if (constraintWidget7 == null) {
                            }
                            if (i != 0) {
                                f6 = (float) constraintWidget5.getWidth();
                            } else {
                                f6 = (float) constraintWidget5.getHeight();
                            }
                            constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width + f6);
                            constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                            constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                            width += f6 + f7;
                            constraintWidget5 = constraintWidget7;
                        }
                    }
                    return true;
                }
                if (constraintWidget5 == null) {
                }
                if (constraintWidget4 != null) {
                    f4 += (float) constraintWidget4.mListAnchors[i5].getMargin();
                }
                f5 = resolutionNode.target.resolvedOffset;
                f6 = resolutionNode2.target.resolvedOffset;
                if (f5 >= f6) {
                }
                if (i4 > 0) {
                }
                resolutionAnchor = resolutionNode;
                if (f6 >= f) {
                    return false;
                }
                if (obj == null) {
                    if (obj2 == null) {
                        f6 -= f4;
                    } else if (obj3 != null) {
                        f6 -= f4;
                    }
                    f7 = f6 / ((float) (i3 + 1));
                    if (obj3 != null) {
                        if (i3 <= 1) {
                        }
                    }
                    width = f5 + f7;
                    width = ((float) constraintWidget5.mListAnchors[i2].getMargin()) + f5;
                    width += (float) constraintWidget5.mListAnchors[i2].getMargin();
                    while (constraintWidget5 != null) {
                        if (LinearSystem.sMetrics != null) {
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.nonresolvedWidgets--;
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.resolvedWidgets++;
                            metrics2 = LinearSystem.sMetrics;
                            metrics2.chainConnectionResolved++;
                        }
                        constraintWidget7 = constraintWidget5.mListNextVisibleWidget[i];
                        if (constraintWidget7 == null) {
                        }
                        if (i != 0) {
                            f6 = (float) constraintWidget5.getHeight();
                        } else {
                            f6 = (float) constraintWidget5.getWidth();
                        }
                        constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width);
                        constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, width + f6);
                        constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                        constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                        width += f6 + f7;
                        constraintWidget5 = constraintWidget7;
                    }
                } else {
                    f5 += (f6 - f4) * constraintWidget.getHorizontalBiasPercent();
                    while (constraintWidget5 != null) {
                        if (LinearSystem.sMetrics != null) {
                            metrics = LinearSystem.sMetrics;
                            metrics.nonresolvedWidgets--;
                            metrics = LinearSystem.sMetrics;
                            metrics.resolvedWidgets++;
                            metrics = LinearSystem.sMetrics;
                            metrics.chainConnectionResolved++;
                        }
                        constraintWidget8 = constraintWidget5.mListNextVisibleWidget[i];
                        if (constraintWidget8 == null) {
                        }
                        if (i != 0) {
                            width = (float) constraintWidget5.getHeight();
                        } else {
                            width = (float) constraintWidget5.getWidth();
                        }
                        f5 += (float) constraintWidget5.mListAnchors[i2].getMargin();
                        constraintWidget5.mListAnchors[i2].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                        f5 += width;
                        constraintWidget5.mListAnchors[i5].getResolutionNode().resolve(resolutionAnchor.resolvedTarget, f5);
                        constraintWidget5.mListAnchors[i2].getResolutionNode().addResolvedValue(linearSystem2);
                        constraintWidget5.mListAnchors[i5].getResolutionNode().addResolvedValue(linearSystem2);
                        f5 += (float) constraintWidget5.mListAnchors[i5].getMargin();
                        constraintWidget5 = constraintWidget8;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
