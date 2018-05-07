package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import android.support.v4.widget.AutoScrollHelper;
import java.util.ArrayList;

public class Barrier extends Helper {
    public static final int BOTTOM = 3;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    private boolean mAllowsGoneWidget = true;
    private int mBarrierType = 0;
    private ArrayList<ResolutionAnchor> mNodes = new ArrayList(4);

    public boolean allowedInBarrier() {
        return true;
    }

    public void setBarrierType(int i) {
        this.mBarrierType = i;
    }

    public void setAllowsGoneWidget(boolean z) {
        this.mAllowsGoneWidget = z;
    }

    public void resetResolutionNodes() {
        super.resetResolutionNodes();
        this.mNodes.clear();
    }

    public void analyze(int i) {
        if (this.mParent != 0 && ((ConstraintWidgetContainer) this.mParent).optimizeFor(2) != 0) {
            int i2;
            ConstraintWidget constraintWidget;
            ResolutionAnchor resolutionNode;
            switch (this.mBarrierType) {
                case 0:
                    i = this.mLeft.getResolutionNode();
                    break;
                case 1:
                    i = this.mRight.getResolutionNode();
                    break;
                case 2:
                    i = this.mTop.getResolutionNode();
                    break;
                case 3:
                    i = this.mBottom.getResolutionNode();
                    break;
                default:
                    return;
            }
            i.setType(5);
            if (this.mBarrierType != 0) {
                if (this.mBarrierType != 1) {
                    this.mLeft.getResolutionNode().resolve(null, 0.0f);
                    this.mRight.getResolutionNode().resolve(null, 0.0f);
                    this.mNodes.clear();
                    for (i2 = 0; i2 < this.mWidgetsCount; i2++) {
                        constraintWidget = this.mWidgets[i2];
                        if (!this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) {
                            switch (this.mBarrierType) {
                                case 0:
                                    resolutionNode = constraintWidget.mLeft.getResolutionNode();
                                    break;
                                case 1:
                                    resolutionNode = constraintWidget.mRight.getResolutionNode();
                                    break;
                                case 2:
                                    resolutionNode = constraintWidget.mTop.getResolutionNode();
                                    break;
                                case 3:
                                    resolutionNode = constraintWidget.mBottom.getResolutionNode();
                                    break;
                                default:
                                    resolutionNode = null;
                                    break;
                            }
                            if (resolutionNode != null) {
                                this.mNodes.add(resolutionNode);
                                resolutionNode.addDependent(i);
                            }
                        }
                    }
                }
            }
            this.mTop.getResolutionNode().resolve(null, 0.0f);
            this.mBottom.getResolutionNode().resolve(null, 0.0f);
            this.mNodes.clear();
            for (i2 = 0; i2 < this.mWidgetsCount; i2++) {
                constraintWidget = this.mWidgets[i2];
                if (this.mAllowsGoneWidget) {
                }
                switch (this.mBarrierType) {
                    case 0:
                        resolutionNode = constraintWidget.mLeft.getResolutionNode();
                        break;
                    case 1:
                        resolutionNode = constraintWidget.mRight.getResolutionNode();
                        break;
                    case 2:
                        resolutionNode = constraintWidget.mTop.getResolutionNode();
                        break;
                    case 3:
                        resolutionNode = constraintWidget.mBottom.getResolutionNode();
                        break;
                    default:
                        resolutionNode = null;
                        break;
                }
                if (resolutionNode != null) {
                    this.mNodes.add(resolutionNode);
                    resolutionNode.addDependent(i);
                }
            }
        }
    }

    public void resolve() {
        ResolutionAnchor resolutionNode;
        int i = this.mBarrierType;
        float f = AutoScrollHelper.NO_MAX;
        switch (i) {
            case 0:
                resolutionNode = this.mLeft.getResolutionNode();
                break;
            case 1:
                resolutionNode = this.mRight.getResolutionNode();
                break;
            case 2:
                resolutionNode = this.mTop.getResolutionNode();
                break;
            case 3:
                resolutionNode = this.mBottom.getResolutionNode();
                break;
            default:
                return;
        }
        f = 0.0f;
        int size = this.mNodes.size();
        ResolutionAnchor resolutionAnchor = null;
        int i2 = 0;
        while (i2 < size) {
            ResolutionAnchor resolutionAnchor2 = (ResolutionAnchor) this.mNodes.get(i2);
            if (resolutionAnchor2.state == 1) {
                if (this.mBarrierType != 0) {
                    if (this.mBarrierType != 2) {
                        if (resolutionAnchor2.resolvedOffset > f) {
                            f = resolutionAnchor2.resolvedOffset;
                            resolutionAnchor = resolutionAnchor2.resolvedTarget;
                        }
                        i2++;
                    }
                }
                if (resolutionAnchor2.resolvedOffset < f) {
                    f = resolutionAnchor2.resolvedOffset;
                    resolutionAnchor = resolutionAnchor2.resolvedTarget;
                }
                i2++;
            } else {
                return;
            }
        }
        if (LinearSystem.getMetrics() != null) {
            Metrics metrics = LinearSystem.getMetrics();
            metrics.barrierConnectionResolved++;
        }
        resolutionNode.resolvedTarget = resolutionAnchor;
        resolutionNode.resolvedOffset = f;
        resolutionNode.didResolve();
        switch (this.mBarrierType) {
            case 0:
                this.mRight.getResolutionNode().resolve(resolutionAnchor, f);
                break;
            case 1:
                this.mLeft.getResolutionNode().resolve(resolutionAnchor, f);
                break;
            case 2:
                this.mBottom.getResolutionNode().resolve(resolutionAnchor, f);
                break;
            case 3:
                this.mTop.getResolutionNode().resolve(resolutionAnchor, f);
                break;
            default:
                return;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToSolver(LinearSystem linearSystem) {
        this.mListAnchors[0] = this.mLeft;
        this.mListAnchors[2] = this.mTop;
        this.mListAnchors[1] = this.mRight;
        this.mListAnchors[3] = this.mBottom;
        for (int i = 0; i < this.mListAnchors.length; i++) {
            this.mListAnchors[i].mSolverVariable = linearSystem.createObjectVariable(this.mListAnchors[i]);
        }
        if (this.mBarrierType >= 0 && this.mBarrierType < 4) {
            boolean z;
            ConstraintAnchor constraintAnchor = this.mListAnchors[this.mBarrierType];
            for (int i2 = 0; i2 < this.mWidgetsCount; i2++) {
                ConstraintWidget constraintWidget = this.mWidgets[i2];
                if (this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) {
                    if ((this.mBarrierType != 0 && this.mBarrierType != 1) || constraintWidget.getHorizontalDimensionBehaviour() != DimensionBehaviour.MATCH_CONSTRAINT) {
                        if ((this.mBarrierType == 2 || this.mBarrierType == 3) && constraintWidget.getVerticalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                        }
                    }
                    z = true;
                    break;
                }
            }
            z = false;
            if (this.mBarrierType != 0) {
                if (this.mBarrierType != 1) {
                    if (getParent().getVerticalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT) {
                        z = false;
                    }
                    for (int i3 = 0; i3 < this.mWidgetsCount; i3++) {
                        ConstraintWidget constraintWidget2 = this.mWidgets[i3];
                        if (this.mAllowsGoneWidget || constraintWidget2.allowedInBarrier()) {
                            SolverVariable createObjectVariable = linearSystem.createObjectVariable(constraintWidget2.mListAnchors[this.mBarrierType]);
                            constraintWidget2.mListAnchors[this.mBarrierType].mSolverVariable = createObjectVariable;
                            if (this.mBarrierType != 0) {
                                if (this.mBarrierType != 2) {
                                    linearSystem.addGreaterBarrier(constraintAnchor.mSolverVariable, createObjectVariable, z);
                                }
                            }
                            linearSystem.addLowerBarrier(constraintAnchor.mSolverVariable, createObjectVariable, z);
                        }
                    }
                    if (this.mBarrierType == 0) {
                        linearSystem.addEquality(this.mRight.mSolverVariable, this.mLeft.mSolverVariable, 0, 6);
                        if (!z) {
                            linearSystem.addEquality(this.mLeft.mSolverVariable, this.mParent.mRight.mSolverVariable, 0, 5);
                        }
                    } else if (this.mBarrierType == 1) {
                        linearSystem.addEquality(this.mLeft.mSolverVariable, this.mRight.mSolverVariable, 0, 6);
                        if (!z) {
                            linearSystem.addEquality(this.mLeft.mSolverVariable, this.mParent.mLeft.mSolverVariable, 0, 5);
                        }
                    } else if (this.mBarrierType == 2) {
                        linearSystem.addEquality(this.mBottom.mSolverVariable, this.mTop.mSolverVariable, 0, 6);
                        if (!z) {
                            linearSystem.addEquality(this.mTop.mSolverVariable, this.mParent.mBottom.mSolverVariable, 0, 5);
                        }
                    } else if (this.mBarrierType == 3) {
                        linearSystem.addEquality(this.mTop.mSolverVariable, this.mBottom.mSolverVariable, 0, 6);
                        if (!z) {
                            linearSystem.addEquality(this.mTop.mSolverVariable, this.mParent.mTop.mSolverVariable, 0, 5);
                        }
                    }
                }
            }
        }
    }
}
