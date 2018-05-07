package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;
import java.io.PrintStream;
import java.util.Arrays;

public class ArrayVariables {
    private static final boolean DEBUG = false;
    private int ROW_SIZE = 8;
    int currentSize = 0;
    private int[] mArrayIndices = new int[this.ROW_SIZE];
    private boolean[] mArrayValid = new boolean[this.ROW_SIZE];
    private float[] mArrayValues = new float[this.ROW_SIZE];
    private final Cache mCache;
    private final ArrayRow mRow;

    ArrayVariables(ArrayRow arrayRow, Cache cache) {
        this.mRow = arrayRow;
        this.mCache = cache;
    }

    public final void put(SolverVariable solverVariable, float f) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                this.mArrayValues[i] = f;
                if (f == 0.0f) {
                    this.mArrayValid[i] = null;
                    solverVariable.removeFromRow(this.mRow);
                }
                return;
            }
        }
        if (this.currentSize >= this.mArrayIndices.length) {
            this.ROW_SIZE *= 2;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
        }
        this.mArrayIndices[this.currentSize] = solverVariable.id;
        this.mArrayValues[this.currentSize] = f;
        this.mArrayValid[this.currentSize] = true;
        if (f == 0.0f) {
            solverVariable.removeFromRow(this.mRow);
            this.mArrayValid[this.currentSize] = null;
        }
        solverVariable.usageInRowCount += Float.MIN_VALUE;
        solverVariable.addToRow(this.mRow);
        this.currentSize += 1;
    }

    final void add(SolverVariable solverVariable, float f, boolean z) {
        if (f != 0.0f) {
            for (int i = 0; i < this.currentSize; i++) {
                if (this.mArrayIndices[i] == solverVariable.id) {
                    solverVariable = this.mArrayValues;
                    solverVariable[i] = solverVariable[i] + f;
                    return;
                }
            }
            if (this.currentSize >= this.mArrayIndices.length) {
                this.ROW_SIZE *= 2;
                this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
                this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
                this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
            }
            this.mArrayIndices[this.currentSize] = solverVariable.id;
            float[] fArr = this.mArrayValues;
            int i2 = this.currentSize;
            fArr[i2] = fArr[i2] + f;
            this.mArrayValid[this.currentSize] = 1;
            solverVariable.usageInRowCount += Float.MIN_VALUE;
            solverVariable.addToRow(this.mRow);
            if (this.mArrayValues[this.currentSize] == 0.0f) {
                solverVariable.usageInRowCount -= Float.MIN_VALUE;
                solverVariable.removeFromRow(this.mRow);
                this.mArrayValid[this.currentSize] = null;
            }
            this.currentSize += 1;
        }
    }

    public final float remove(SolverVariable solverVariable, boolean z) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                float f = this.mArrayValues[i];
                this.mArrayValues[i] = 0.0f;
                this.mArrayValid[i] = false;
                if (z) {
                    solverVariable.usageInRowCount--;
                    solverVariable.removeFromRow(this.mRow);
                }
                return f;
            }
        }
        return 0.0f;
    }

    public final void clear() {
        for (int i = 0; i < this.currentSize; i++) {
            SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
            if (solverVariable != null) {
                solverVariable.removeFromRow(this.mRow);
            }
        }
        this.currentSize = 0;
    }

    final boolean containsKey(SolverVariable solverVariable) {
        int i = 0;
        while (i < this.currentSize) {
            if (this.mArrayValid[i] && this.mArrayIndices[i] == solverVariable.id) {
                return true;
            }
            i++;
        }
        return false;
    }

    boolean hasAtLeastOnePositiveVariable() {
        int i = 0;
        while (i < this.currentSize) {
            if (this.mArrayValid[i] && this.mArrayValues[i] > 0.0f) {
                return true;
            }
            i++;
        }
        return false;
    }

    void invert() {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                float[] fArr = this.mArrayValues;
                fArr[i] = fArr[i] * -1.0f;
            }
        }
    }

    void divideByAmount(float f) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                float[] fArr = this.mArrayValues;
                fArr[i] = fArr[i] / f;
            }
        }
    }

    private boolean isNew(SolverVariable solverVariable, LinearSystem linearSystem) {
        return solverVariable.mClientEquationsCount <= 1 ? true : null;
    }

    SolverVariable chooseSubject(LinearSystem linearSystem) {
        SolverVariable solverVariable = null;
        SolverVariable solverVariable2 = solverVariable;
        int i = 0;
        int i2 = i;
        int i3 = i2;
        float f = 0.0f;
        float f2 = f;
        while (i < this.currentSize) {
            if (this.mArrayValid[i]) {
                boolean isNew;
                boolean z;
                boolean isNew2;
                boolean z2;
                float f3 = this.mArrayValues[i];
                SolverVariable solverVariable3 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (f3 < 0.0f) {
                    if (f3 > -0.001f) {
                        this.mArrayValues[i] = 0.0f;
                        this.mArrayValid[i] = false;
                        solverVariable3.removeFromRow(this.mRow);
                    }
                    if (f3 == 0.0f) {
                        if (solverVariable3.mType == Type.UNRESTRICTED) {
                            if (solverVariable != null) {
                                isNew = isNew(solverVariable3, linearSystem);
                            } else if (f > f3) {
                                isNew = isNew(solverVariable3, linearSystem);
                            } else if (i2 == 0 && isNew(solverVariable3, linearSystem)) {
                                f = f3;
                                z = true;
                                solverVariable = solverVariable3;
                            }
                            i2 = isNew;
                            f = f3;
                            solverVariable = solverVariable3;
                        } else if (solverVariable == null && f3 < 0.0f) {
                            if (solverVariable2 != null) {
                                isNew2 = isNew(solverVariable3, linearSystem);
                            } else if (f2 > f3) {
                                isNew2 = isNew(solverVariable3, linearSystem);
                            } else if (i3 == 0 && isNew(solverVariable3, linearSystem)) {
                                f2 = f3;
                                z2 = true;
                                solverVariable2 = solverVariable3;
                            }
                            i3 = isNew2;
                            f2 = f3;
                            solverVariable2 = solverVariable3;
                        }
                    }
                } else {
                    if (f3 < 0.001f) {
                        this.mArrayValues[i] = 0.0f;
                        this.mArrayValid[i] = false;
                        solverVariable3.removeFromRow(this.mRow);
                    }
                    if (f3 == 0.0f) {
                        if (solverVariable3.mType == Type.UNRESTRICTED) {
                            if (solverVariable2 != null) {
                                isNew2 = isNew(solverVariable3, linearSystem);
                            } else if (f2 > f3) {
                                f2 = f3;
                                z2 = true;
                                solverVariable2 = solverVariable3;
                            } else {
                                isNew2 = isNew(solverVariable3, linearSystem);
                            }
                            i3 = isNew2;
                            f2 = f3;
                            solverVariable2 = solverVariable3;
                        } else {
                            if (solverVariable != null) {
                                isNew = isNew(solverVariable3, linearSystem);
                            } else if (f > f3) {
                                f = f3;
                                z = true;
                                solverVariable = solverVariable3;
                            } else {
                                isNew = isNew(solverVariable3, linearSystem);
                            }
                            i2 = isNew;
                            f = f3;
                            solverVariable = solverVariable3;
                        }
                    }
                }
                f3 = 0.0f;
                if (f3 == 0.0f) {
                    if (solverVariable3.mType == Type.UNRESTRICTED) {
                        if (solverVariable != null) {
                            isNew = isNew(solverVariable3, linearSystem);
                        } else if (f > f3) {
                            isNew = isNew(solverVariable3, linearSystem);
                        } else {
                            f = f3;
                            z = true;
                            solverVariable = solverVariable3;
                        }
                        i2 = isNew;
                        f = f3;
                        solverVariable = solverVariable3;
                    } else {
                        if (solverVariable2 != null) {
                            isNew2 = isNew(solverVariable3, linearSystem);
                        } else if (f2 > f3) {
                            isNew2 = isNew(solverVariable3, linearSystem);
                        } else {
                            f2 = f3;
                            z2 = true;
                            solverVariable2 = solverVariable3;
                        }
                        i3 = isNew2;
                        f2 = f3;
                        solverVariable2 = solverVariable3;
                    }
                }
            }
            i++;
        }
        return solverVariable != null ? solverVariable : solverVariable2;
    }

    final void updateFromRow(ArrayRow arrayRow, ArrayRow arrayRow2, boolean z) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                if (this.mArrayIndices[i] == arrayRow2.variable.id) {
                    float f = this.mArrayValues[i];
                    if (f != 0.0f) {
                        this.mArrayValues[i] = 0.0f;
                        this.mArrayValid[i] = false;
                        if (z) {
                            arrayRow2.variable.removeFromRow(this.mRow);
                        }
                        ArrayVariables arrayVariables = (ArrayVariables) arrayRow2.variables;
                        for (int i2 = 0; i2 < arrayVariables.currentSize; i2++) {
                            add(this.mCache.mIndexedVariables[arrayVariables.mArrayIndices[i2]], arrayVariables.mArrayValues[i2] * f, z);
                        }
                        arrayRow.constantValue += arrayRow2.constantValue * f;
                        if (z) {
                            arrayRow2.variable.removeFromRow(arrayRow);
                        }
                    }
                }
            }
        }
    }

    void updateFromSystem(ArrayRow arrayRow, ArrayRow[] arrayRowArr) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (solverVariable.definitionId != -1) {
                    float f = this.mArrayValues[i];
                    this.mArrayValues[i] = 0.0f;
                    this.mArrayValid[i] = false;
                    solverVariable.removeFromRow(this.mRow);
                    ArrayRow arrayRow2 = arrayRowArr[solverVariable.definitionId];
                    if (!arrayRow2.isSimpleDefinition) {
                        ArrayVariables arrayVariables = (ArrayVariables) arrayRow2.variables;
                        for (int i2 = 0; i2 < arrayVariables.currentSize; i2++) {
                            add(this.mCache.mIndexedVariables[arrayVariables.mArrayIndices[i2]], arrayVariables.mArrayValues[i2] * f, true);
                        }
                    }
                    arrayRow.constantValue += arrayRow2.constantValue * f;
                    arrayRow2.variable.removeFromRow(arrayRow);
                }
            }
        }
    }

    SolverVariable getPivotCandidate() {
        SolverVariable solverVariable = null;
        int i = 0;
        while (i < this.currentSize) {
            if (this.mArrayValid[i] && this.mArrayValues[i] < 0.0f) {
                SolverVariable solverVariable2 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (solverVariable == null || solverVariable.strength < solverVariable2.strength) {
                    solverVariable = solverVariable2;
                }
            }
            i++;
        }
        return solverVariable;
    }

    SolverVariable getPivotCandidate(boolean[] zArr, SolverVariable solverVariable) {
        SolverVariable solverVariable2 = null;
        int i = 0;
        float f = 0.0f;
        while (i < this.currentSize) {
            if (this.mArrayValid[i] && this.mArrayValues[i] < 0.0f) {
                SolverVariable solverVariable3 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if ((zArr == null || !zArr[solverVariable3.id]) && solverVariable3 != solverVariable && (solverVariable3.mType == Type.SLACK || solverVariable3.mType == Type.ERROR)) {
                    float f2 = this.mArrayValues[i];
                    if (f2 < f) {
                        solverVariable2 = solverVariable3;
                        f = f2;
                    }
                }
            }
            i++;
        }
        return solverVariable2;
    }

    final SolverVariable getVariable(int i) {
        return i < this.currentSize ? this.mCache.mIndexedVariables[this.mArrayIndices[i]] : 0;
    }

    final float getVariableValue(int i) {
        return i < this.currentSize ? this.mArrayValues[i] : 0;
    }

    public final float get(SolverVariable solverVariable) {
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                if (this.mArrayIndices[i] == solverVariable.id) {
                    return this.mArrayValues[i];
                }
            }
        }
        return null;
    }

    int sizeInBytes() {
        return (0 + (3 * (this.mArrayIndices.length * 4))) + 36;
    }

    public void display() {
        int i = this.currentSize;
        System.out.print("{ ");
        for (int i2 = 0; i2 < i; i2++) {
            if (this.mArrayValid[i2]) {
                SolverVariable variable = getVariable(i2);
                if (variable != null) {
                    PrintStream printStream = System.out;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(variable);
                    stringBuilder.append(" = ");
                    stringBuilder.append(getVariableValue(i2));
                    stringBuilder.append(" ");
                    printStream.print(stringBuilder.toString());
                }
            }
        }
        System.out.println(" }");
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < this.currentSize; i++) {
            if (this.mArrayValid[i]) {
                if (this.mArrayValues[i] != 0.0f) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(" -> ");
                    str = stringBuilder.toString();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(this.mArrayValues[i]);
                    stringBuilder.append(" : ");
                    str = stringBuilder.toString();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(this.mCache.mIndexedVariables[this.mArrayIndices[i]]);
                    str = stringBuilder.toString();
                }
            }
        }
        return str;
    }
}
