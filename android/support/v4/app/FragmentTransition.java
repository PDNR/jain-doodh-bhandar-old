package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;

class FragmentTransition {
    private static final int[] INVERSE_OPS = new int[]{0, 3, 0, 1, 5, 4, 7, 6, 9, 8};

    static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;

        FragmentContainerTransition() {
        }
    }

    FragmentTransition() {
    }

    static void startTransitions(FragmentManagerImpl fragmentManagerImpl, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, boolean z) {
        if (fragmentManagerImpl.mCurState >= 1 && VERSION.SDK_INT >= 21) {
            SparseArray sparseArray = new SparseArray();
            for (int i3 = i; i3 < i2; i3++) {
                BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
                if (((Boolean) arrayList2.get(i3)).booleanValue()) {
                    calculatePopFragments(backStackRecord, sparseArray, z);
                } else {
                    calculateFragments(backStackRecord, sparseArray, z);
                }
            }
            if (sparseArray.size() != 0) {
                View view = new View(fragmentManagerImpl.mHost.getContext());
                int size = sparseArray.size();
                for (int i4 = 0; i4 < size; i4++) {
                    int keyAt = sparseArray.keyAt(i4);
                    ArrayMap calculateNameOverrides = calculateNameOverrides(keyAt, arrayList, arrayList2, i, i2);
                    FragmentContainerTransition fragmentContainerTransition = (FragmentContainerTransition) sparseArray.valueAt(i4);
                    if (z) {
                        configureTransitionsReordered(fragmentManagerImpl, keyAt, fragmentContainerTransition, view, calculateNameOverrides);
                    } else {
                        configureTransitionsOrdered(fragmentManagerImpl, keyAt, fragmentContainerTransition, view, calculateNameOverrides);
                    }
                }
            }
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int i, ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        ArrayMap<String, String> arrayMap = new ArrayMap();
        for (i3--; i3 >= i2; i3--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
            if (backStackRecord.interactsWith(i)) {
                boolean booleanValue = ((Boolean) arrayList2.get(i3)).booleanValue();
                if (backStackRecord.mSharedElementSourceNames != null) {
                    ArrayList arrayList3;
                    ArrayList arrayList4;
                    int size = backStackRecord.mSharedElementSourceNames.size();
                    if (booleanValue) {
                        arrayList3 = backStackRecord.mSharedElementSourceNames;
                        arrayList4 = backStackRecord.mSharedElementTargetNames;
                    } else {
                        ArrayList arrayList5 = backStackRecord.mSharedElementSourceNames;
                        arrayList3 = backStackRecord.mSharedElementTargetNames;
                        arrayList4 = arrayList5;
                    }
                    for (int i4 = 0; i4 < size; i4++) {
                        String str = (String) arrayList4.get(i4);
                        String str2 = (String) arrayList3.get(i4);
                        String str3 = (String) arrayMap.remove(str2);
                        if (str3 != null) {
                            arrayMap.put(str, str3);
                        } else {
                            arrayMap.put(str, str2);
                        }
                    }
                }
            }
        }
        return arrayMap;
    }

    @RequiresApi(21)
    private static void configureTransitionsReordered(FragmentManagerImpl fragmentManagerImpl, int i, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap) {
        FragmentManagerImpl fragmentManagerImpl2 = fragmentManagerImpl;
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        View view2 = view;
        View view3 = fragmentManagerImpl2.mContainer.onHasView() ? (ViewGroup) fragmentManagerImpl2.mContainer.onFindViewById(i) : null;
        if (view3 != null) {
            Object obj;
            Fragment fragment = fragmentContainerTransition2.lastIn;
            Fragment fragment2 = fragmentContainerTransition2.firstOut;
            boolean z = fragmentContainerTransition2.lastInIsPop;
            boolean z2 = fragmentContainerTransition2.firstOutIsPop;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Object enterTransition = getEnterTransition(fragment, z);
            Object exitTransition = getExitTransition(fragment2, z2);
            Object obj2 = exitTransition;
            Object configureSharedElementsReordered = configureSharedElementsReordered(view3, view2, arrayMap, fragmentContainerTransition2, arrayList2, arrayList, enterTransition, exitTransition);
            if (enterTransition == null && configureSharedElementsReordered == null) {
                obj = obj2;
                if (obj == null) {
                    return;
                }
            }
            obj = obj2;
            ArrayList configureEnteringExitingViews = configureEnteringExitingViews(obj, fragment2, arrayList2, view2);
            ArrayList configureEnteringExitingViews2 = configureEnteringExitingViews(enterTransition, fragment, arrayList, view2);
            setViewVisibility(configureEnteringExitingViews2, 4);
            Object mergeTransitions = mergeTransitions(enterTransition, obj, configureSharedElementsReordered, fragment, z);
            if (mergeTransitions != null) {
                replaceHide(obj, fragment2, configureEnteringExitingViews);
                ArrayList prepareSetNameOverridesReordered = FragmentTransitionCompat21.prepareSetNameOverridesReordered(arrayList);
                FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition, configureEnteringExitingViews2, obj, configureEnteringExitingViews, configureSharedElementsReordered, arrayList);
                FragmentTransitionCompat21.beginDelayedTransition(view3, mergeTransitions);
                FragmentTransitionCompat21.setNameOverridesReordered(view3, arrayList2, arrayList, prepareSetNameOverridesReordered, arrayMap);
                setViewVisibility(configureEnteringExitingViews2, 0);
                FragmentTransitionCompat21.swapSharedElementTargets(configureSharedElementsReordered, arrayList2, arrayList);
            }
        }
    }

    @RequiresApi(21)
    private static void replaceHide(Object obj, Fragment fragment, final ArrayList<View> arrayList) {
        if (fragment != null && obj != null && fragment.mAdded && fragment.mHidden && fragment.mHiddenChanged) {
            fragment.setHideReplaced(true);
            FragmentTransitionCompat21.scheduleHideFragmentView(obj, fragment.getView(), arrayList);
            OneShotPreDrawListener.add(fragment.mContainer, new Runnable() {
                public void run() {
                    FragmentTransition.setViewVisibility(arrayList, 4);
                }
            });
        }
    }

    @RequiresApi(21)
    private static void configureTransitionsOrdered(FragmentManagerImpl fragmentManagerImpl, int i, FragmentContainerTransition fragmentContainerTransition, View view, ArrayMap<String, String> arrayMap) {
        FragmentManagerImpl fragmentManagerImpl2 = fragmentManagerImpl;
        FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        View view2 = view;
        ArrayMap<String, String> arrayMap2 = arrayMap;
        View view3 = fragmentManagerImpl2.mContainer.onHasView() ? (ViewGroup) fragmentManagerImpl2.mContainer.onFindViewById(i) : null;
        if (view3 != null) {
            Object obj;
            Object obj2;
            Object mergeTransitions;
            ArrayList arrayList;
            Object obj3;
            Fragment fragment = fragmentContainerTransition2.lastIn;
            Fragment fragment2 = fragmentContainerTransition2.firstOut;
            boolean z = fragmentContainerTransition2.lastInIsPop;
            boolean z2 = fragmentContainerTransition2.firstOutIsPop;
            Object enterTransition = getEnterTransition(fragment, z);
            Object exitTransition = getExitTransition(fragment2, z2);
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = arrayList3;
            ArrayList arrayList5 = arrayList2;
            Object obj4 = exitTransition;
            exitTransition = configureSharedElementsOrdered(view3, view2, arrayMap2, fragmentContainerTransition2, arrayList2, arrayList3, enterTransition, exitTransition);
            if (enterTransition == null && exitTransition == null) {
                obj = obj4;
                if (obj == null) {
                    return;
                }
            }
            obj = obj4;
            arrayList5 = configureEnteringExitingViews(obj, fragment2, arrayList5, view2);
            if (arrayList5 != null) {
                if (!arrayList5.isEmpty()) {
                    obj2 = obj;
                    FragmentTransitionCompat21.addTarget(enterTransition, view2);
                    mergeTransitions = mergeTransitions(enterTransition, obj2, exitTransition, fragment, fragmentContainerTransition2.lastInIsPop);
                    if (mergeTransitions != null) {
                        arrayList = new ArrayList();
                        FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition, arrayList, obj2, arrayList5, exitTransition, arrayList4);
                        obj3 = mergeTransitions;
                        scheduleTargetChange(view3, fragment, view2, arrayList4, enterTransition, arrayList, obj2, arrayList5);
                        arrayList = arrayList4;
                        FragmentTransitionCompat21.setNameOverridesOrdered(view3, arrayList, arrayMap2);
                        FragmentTransitionCompat21.beginDelayedTransition(view3, obj3);
                        FragmentTransitionCompat21.scheduleNameReset(view3, arrayList, arrayMap2);
                    }
                }
            }
            obj2 = null;
            FragmentTransitionCompat21.addTarget(enterTransition, view2);
            mergeTransitions = mergeTransitions(enterTransition, obj2, exitTransition, fragment, fragmentContainerTransition2.lastInIsPop);
            if (mergeTransitions != null) {
                arrayList = new ArrayList();
                FragmentTransitionCompat21.scheduleRemoveTargets(mergeTransitions, enterTransition, arrayList, obj2, arrayList5, exitTransition, arrayList4);
                obj3 = mergeTransitions;
                scheduleTargetChange(view3, fragment, view2, arrayList4, enterTransition, arrayList, obj2, arrayList5);
                arrayList = arrayList4;
                FragmentTransitionCompat21.setNameOverridesOrdered(view3, arrayList, arrayMap2);
                FragmentTransitionCompat21.beginDelayedTransition(view3, obj3);
                FragmentTransitionCompat21.scheduleNameReset(view3, arrayList, arrayMap2);
            }
        }
    }

    @RequiresApi(21)
    private static void scheduleTargetChange(ViewGroup viewGroup, Fragment fragment, View view, ArrayList<View> arrayList, Object obj, ArrayList<View> arrayList2, Object obj2, ArrayList<View> arrayList3) {
        final Object obj3 = obj;
        final View view2 = view;
        final Fragment fragment2 = fragment;
        final ArrayList<View> arrayList4 = arrayList;
        final ArrayList<View> arrayList5 = arrayList2;
        final ArrayList<View> arrayList6 = arrayList3;
        final Object obj4 = obj2;
        OneShotPreDrawListener.add(viewGroup, new Runnable() {
            public void run() {
                if (obj3 != null) {
                    FragmentTransitionCompat21.removeTarget(obj3, view2);
                    arrayList5.addAll(FragmentTransition.configureEnteringExitingViews(obj3, fragment2, arrayList4, view2));
                }
                if (arrayList6 != null) {
                    if (obj4 != null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(view2);
                        FragmentTransitionCompat21.replaceTargets(obj4, arrayList6, arrayList);
                    }
                    arrayList6.clear();
                    arrayList6.add(view2);
                }
            }
        });
    }

    @RequiresApi(21)
    private static Object getSharedElementTransition(Fragment fragment, Fragment fragment2, boolean z) {
        if (fragment != null) {
            if (fragment2 != null) {
                if (z) {
                    fragment = fragment2.getSharedElementReturnTransition();
                } else {
                    fragment = fragment.getSharedElementEnterTransition();
                }
                return FragmentTransitionCompat21.wrapTransitionInSet(FragmentTransitionCompat21.cloneTransition(fragment));
            }
        }
        return null;
    }

    @RequiresApi(21)
    private static Object getEnterTransition(Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        if (z) {
            fragment = fragment.getReenterTransition();
        } else {
            fragment = fragment.getEnterTransition();
        }
        return FragmentTransitionCompat21.cloneTransition(fragment);
    }

    @RequiresApi(21)
    private static Object getExitTransition(Fragment fragment, boolean z) {
        if (fragment == null) {
            return null;
        }
        if (z) {
            fragment = fragment.getReturnTransition();
        } else {
            fragment = fragment.getExitTransition();
        }
        return FragmentTransitionCompat21.cloneTransition(fragment);
    }

    @RequiresApi(21)
    private static Object configureSharedElementsReordered(ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        Fragment fragment = fragmentContainerTransition.lastIn;
        Fragment fragment2 = fragmentContainerTransition.firstOut;
        if (fragment != null) {
            fragment.getView().setVisibility(0);
        }
        if (fragment != null) {
            if (fragment2 != null) {
                Object obj3;
                boolean z = fragmentContainerTransition.lastInIsPop;
                if (arrayMap.isEmpty()) {
                    obj3 = null;
                } else {
                    obj3 = getSharedElementTransition(fragment, fragment2, z);
                }
                ArrayMap captureOutSharedElements = captureOutSharedElements(arrayMap, obj3, fragmentContainerTransition);
                ArrayMap captureInSharedElements = captureInSharedElements(arrayMap, obj3, fragmentContainerTransition);
                if (arrayMap.isEmpty()) {
                    if (captureOutSharedElements != null) {
                        captureOutSharedElements.clear();
                    }
                    if (captureInSharedElements != null) {
                        captureInSharedElements.clear();
                    }
                    obj3 = null;
                } else {
                    addSharedElementsWithMatchingNames(arrayList, captureOutSharedElements, arrayMap.keySet());
                    addSharedElementsWithMatchingNames(arrayList2, captureInSharedElements, arrayMap.values());
                }
                if (obj == null && obj2 == null && obj3 == null) {
                    return null;
                }
                callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
                if (obj3 != null) {
                    arrayList2.add(view);
                    FragmentTransitionCompat21.setSharedElementTargets(obj3, view, arrayList);
                    setOutEpicenter(obj3, obj2, captureOutSharedElements, fragmentContainerTransition.firstOutIsPop, fragmentContainerTransition.firstOutTransaction);
                    Rect rect = new Rect();
                    arrayMap = getInEpicenterView(captureInSharedElements, fragmentContainerTransition, obj, z);
                    if (arrayMap != null) {
                        FragmentTransitionCompat21.setEpicenter(obj, rect);
                    }
                    obj2 = rect;
                    obj = arrayMap;
                } else {
                    obj = null;
                    obj2 = obj;
                }
                final Fragment fragment3 = fragment;
                final Fragment fragment4 = fragment2;
                arrayList = z;
                final ArrayMap arrayMap2 = captureInSharedElements;
                OneShotPreDrawListener.add(viewGroup, new Runnable() {
                    public void run() {
                        FragmentTransition.callSharedElementStartEnd(fragment3, fragment4, arrayList, arrayMap2, false);
                        if (obj != null) {
                            FragmentTransitionCompat21.getBoundsOnScreen(obj, obj2);
                        }
                    }
                });
                return obj3;
            }
        }
        return null;
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> arrayList, ArrayMap<String, View> arrayMap, Collection<String> collection) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            View view = (View) arrayMap.valueAt(size);
            if (collection.contains(ViewCompat.getTransitionName(view))) {
                arrayList.add(view);
            }
        }
    }

    @RequiresApi(21)
    private static Object configureSharedElementsOrdered(ViewGroup viewGroup, View view, ArrayMap<String, String> arrayMap, FragmentContainerTransition fragmentContainerTransition, ArrayList<View> arrayList, ArrayList<View> arrayList2, Object obj, Object obj2) {
        final FragmentContainerTransition fragmentContainerTransition2 = fragmentContainerTransition;
        final ArrayList<View> arrayList3 = arrayList;
        final Object obj3 = obj;
        Object obj4 = obj2;
        final Fragment fragment = fragmentContainerTransition2.lastIn;
        final Fragment fragment2 = fragmentContainerTransition2.firstOut;
        Rect rect = null;
        if (fragment != null) {
            if (fragment2 != null) {
                ArrayMap arrayMap2;
                Object obj5;
                Object obj6;
                final boolean z = fragmentContainerTransition2.lastInIsPop;
                if (arrayMap.isEmpty()) {
                    arrayMap2 = arrayMap;
                    obj5 = null;
                } else {
                    obj5 = getSharedElementTransition(fragment, fragment2, z);
                    arrayMap2 = arrayMap;
                }
                ArrayMap captureOutSharedElements = captureOutSharedElements(arrayMap2, obj5, fragmentContainerTransition2);
                if (arrayMap.isEmpty()) {
                    obj6 = null;
                } else {
                    arrayList3.addAll(captureOutSharedElements.values());
                    obj6 = obj5;
                }
                if (obj3 == null && obj4 == null && obj6 == null) {
                    return null;
                }
                View view2;
                callSharedElementStartEnd(fragment, fragment2, z, captureOutSharedElements, true);
                if (obj6 != null) {
                    rect = new Rect();
                    view2 = view;
                    FragmentTransitionCompat21.setSharedElementTargets(obj6, view2, arrayList3);
                    setOutEpicenter(obj6, obj4, captureOutSharedElements, fragmentContainerTransition2.firstOutIsPop, fragmentContainerTransition2.firstOutTransaction);
                    if (obj3 != null) {
                        FragmentTransitionCompat21.setEpicenter(obj3, rect);
                    }
                } else {
                    view2 = view;
                }
                Rect rect2 = rect;
                final ArrayMap arrayMap3 = arrayMap2;
                final Object obj7 = obj6;
                final ArrayList<View> arrayList4 = arrayList2;
                final View view3 = view2;
                final Rect rect3 = rect2;
                OneShotPreDrawListener.add(viewGroup, new Runnable() {
                    public void run() {
                        ArrayMap access$300 = FragmentTransition.captureInSharedElements(arrayMap3, obj7, fragmentContainerTransition2);
                        if (access$300 != null) {
                            arrayList4.addAll(access$300.values());
                            arrayList4.add(view3);
                        }
                        FragmentTransition.callSharedElementStartEnd(fragment, fragment2, z, access$300, false);
                        if (obj7 != null) {
                            FragmentTransitionCompat21.swapSharedElementTargets(obj7, arrayList3, arrayList4);
                            View access$400 = FragmentTransition.getInEpicenterView(access$300, fragmentContainerTransition2, obj3, z);
                            if (access$400 != null) {
                                FragmentTransitionCompat21.getBoundsOnScreen(access$400, rect3);
                            }
                        }
                    }
                });
                return obj6;
            }
        }
        return null;
    }

    @RequiresApi(21)
    private static ArrayMap<String, View> captureOutSharedElements(ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        if (!arrayMap.isEmpty()) {
            if (obj != null) {
                obj = fragmentContainerTransition.firstOut;
                ArrayMap<String, View> arrayMap2 = new ArrayMap();
                FragmentTransitionCompat21.findNamedViews(arrayMap2, obj.getView());
                BackStackRecord backStackRecord = fragmentContainerTransition.firstOutTransaction;
                if (fragmentContainerTransition.firstOutIsPop != null) {
                    obj = obj.getEnterTransitionCallback();
                    fragmentContainerTransition = backStackRecord.mSharedElementTargetNames;
                } else {
                    obj = obj.getExitTransitionCallback();
                    fragmentContainerTransition = backStackRecord.mSharedElementSourceNames;
                }
                arrayMap2.retainAll(fragmentContainerTransition);
                if (obj != null) {
                    obj.onMapSharedElements(fragmentContainerTransition, arrayMap2);
                    for (obj = fragmentContainerTransition.size() - 1; obj >= null; obj--) {
                        String str = (String) fragmentContainerTransition.get(obj);
                        View view = (View) arrayMap2.get(str);
                        if (view == null) {
                            arrayMap.remove(str);
                        } else if (!str.equals(ViewCompat.getTransitionName(view))) {
                            arrayMap.put(ViewCompat.getTransitionName(view), (String) arrayMap.remove(str));
                        }
                    }
                } else {
                    arrayMap.retainAll(arrayMap2.keySet());
                }
                return arrayMap2;
            }
        }
        arrayMap.clear();
        return null;
    }

    @RequiresApi(21)
    private static ArrayMap<String, View> captureInSharedElements(ArrayMap<String, String> arrayMap, Object obj, FragmentContainerTransition fragmentContainerTransition) {
        Fragment fragment = fragmentContainerTransition.lastIn;
        View view = fragment.getView();
        if (!(arrayMap.isEmpty() || obj == null)) {
            if (view != null) {
                ArrayList arrayList;
                obj = new ArrayMap();
                FragmentTransitionCompat21.findNamedViews(obj, view);
                BackStackRecord backStackRecord = fragmentContainerTransition.lastInTransaction;
                if (fragmentContainerTransition.lastInIsPop != null) {
                    fragmentContainerTransition = fragment.getExitTransitionCallback();
                    arrayList = backStackRecord.mSharedElementSourceNames;
                } else {
                    fragmentContainerTransition = fragment.getEnterTransitionCallback();
                    arrayList = backStackRecord.mSharedElementTargetNames;
                }
                if (arrayList != null) {
                    obj.retainAll(arrayList);
                }
                if (fragmentContainerTransition != null) {
                    fragmentContainerTransition.onMapSharedElements(arrayList, obj);
                    for (fragmentContainerTransition = arrayList.size() - 1; fragmentContainerTransition >= null; fragmentContainerTransition--) {
                        String str = (String) arrayList.get(fragmentContainerTransition);
                        View view2 = (View) obj.get(str);
                        if (view2 == null) {
                            str = findKeyForValue(arrayMap, str);
                            if (str != null) {
                                arrayMap.remove(str);
                            }
                        } else if (!str.equals(ViewCompat.getTransitionName(view2))) {
                            str = findKeyForValue(arrayMap, str);
                            if (str != null) {
                                arrayMap.put(str, ViewCompat.getTransitionName(view2));
                            }
                        }
                    }
                } else {
                    retainValues(arrayMap, obj);
                }
                return obj;
            }
        }
        arrayMap.clear();
        return null;
    }

    private static String findKeyForValue(ArrayMap<String, String> arrayMap, String str) {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            if (str.equals(arrayMap.valueAt(i))) {
                return (String) arrayMap.keyAt(i);
            }
        }
        return null;
    }

    private static View getInEpicenterView(ArrayMap<String, View> arrayMap, FragmentContainerTransition fragmentContainerTransition, Object obj, boolean z) {
        fragmentContainerTransition = fragmentContainerTransition.lastInTransaction;
        if (obj == null || arrayMap == null || fragmentContainerTransition.mSharedElementSourceNames == null || fragmentContainerTransition.mSharedElementSourceNames.isEmpty() != null) {
            return null;
        }
        if (z) {
            fragmentContainerTransition = (String) fragmentContainerTransition.mSharedElementSourceNames.get(0);
        } else {
            fragmentContainerTransition = (String) fragmentContainerTransition.mSharedElementTargetNames.get(0);
        }
        return (View) arrayMap.get(fragmentContainerTransition);
    }

    @RequiresApi(21)
    private static void setOutEpicenter(Object obj, Object obj2, ArrayMap<String, View> arrayMap, boolean z, BackStackRecord backStackRecord) {
        if (backStackRecord.mSharedElementSourceNames != null && !backStackRecord.mSharedElementSourceNames.isEmpty()) {
            if (z) {
                z = (String) backStackRecord.mSharedElementTargetNames.get(0);
            } else {
                z = (String) backStackRecord.mSharedElementSourceNames.get(0);
            }
            View view = (View) arrayMap.get(z);
            FragmentTransitionCompat21.setEpicenter(obj, view);
            if (obj2 != null) {
                FragmentTransitionCompat21.setEpicenter(obj2, view);
            }
        }
    }

    private static void retainValues(ArrayMap<String, String> arrayMap, ArrayMap<String, View> arrayMap2) {
        for (int size = arrayMap.size() - 1; size >= 0; size--) {
            if (!arrayMap2.containsKey((String) arrayMap.valueAt(size))) {
                arrayMap.removeAt(size);
            }
        }
    }

    private static void callSharedElementStartEnd(Fragment fragment, Fragment fragment2, boolean z, ArrayMap<String, View> arrayMap, boolean z2) {
        if (z) {
            fragment = fragment2.getEnterTransitionCallback();
        } else {
            fragment = fragment.getEnterTransitionCallback();
        }
        if (fragment != null) {
            int i;
            fragment2 = new ArrayList();
            z = new ArrayList();
            int i2 = 0;
            if (arrayMap == null) {
                i = 0;
            } else {
                i = arrayMap.size();
            }
            while (i2 < i) {
                z.add(arrayMap.keyAt(i2));
                fragment2.add(arrayMap.valueAt(i2));
                i2++;
            }
            if (z2) {
                fragment.onSharedElementStart(z, fragment2, null);
            } else {
                fragment.onSharedElementEnd(z, fragment2, null);
            }
        }
    }

    @RequiresApi(21)
    private static ArrayList<View> configureEnteringExitingViews(Object obj, Fragment fragment, ArrayList<View> arrayList, View view) {
        if (obj == null) {
            return null;
        }
        ArrayList<View> arrayList2 = new ArrayList();
        fragment = fragment.getView();
        if (fragment != null) {
            FragmentTransitionCompat21.captureTransitioningViews(arrayList2, fragment);
        }
        if (arrayList != null) {
            arrayList2.removeAll(arrayList);
        }
        if (arrayList2.isEmpty() != null) {
            return arrayList2;
        }
        arrayList2.add(view);
        FragmentTransitionCompat21.addTargets(obj, arrayList2);
        return arrayList2;
    }

    private static void setViewVisibility(ArrayList<View> arrayList, int i) {
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((View) arrayList.get(size)).setVisibility(i);
            }
        }
    }

    @RequiresApi(21)
    private static Object mergeTransitions(Object obj, Object obj2, Object obj3, Fragment fragment, boolean z) {
        fragment = (obj == null || obj2 == null || fragment == null) ? true : z ? fragment.getAllowReturnTransitionOverlap() : fragment.getAllowEnterTransitionOverlap();
        if (fragment != null) {
            return FragmentTransitionCompat21.mergeTransitionsTogether(obj2, obj, obj3);
        }
        return FragmentTransitionCompat21.mergeTransitionsInSequence(obj2, obj, obj3);
    }

    public static void calculateFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        int size = backStackRecord.mOps.size();
        for (int i = 0; i < size; i++) {
            addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(i), sparseArray, false, z);
        }
    }

    public static void calculatePopFragments(BackStackRecord backStackRecord, SparseArray<FragmentContainerTransition> sparseArray, boolean z) {
        if (backStackRecord.mManager.mContainer.onHasView()) {
            for (int size = backStackRecord.mOps.size() - 1; size >= 0; size--) {
                addToFirstInLastOut(backStackRecord, (Op) backStackRecord.mOps.get(size), sparseArray, true, z);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void addToFirstInLastOut(BackStackRecord backStackRecord, Op op, SparseArray<FragmentContainerTransition> sparseArray, boolean z, boolean z2) {
        BackStackRecord backStackRecord2 = backStackRecord;
        Op op2 = op;
        SparseArray<FragmentContainerTransition> sparseArray2 = sparseArray;
        boolean z3 = z;
        Fragment fragment = op2.fragment;
        if (fragment != null) {
            int i = fragment.mContainerId;
            if (i != 0) {
                boolean z4;
                Object obj;
                Object obj2;
                Object obj3;
                int i2 = z3 ? INVERSE_OPS[op2.cmd] : op2.cmd;
                boolean z5 = false;
                if (i2 != 1) {
                    switch (i2) {
                        case 3:
                        case 6:
                            if (!z2) {
                                if (fragment.mAdded && !fragment.mHidden) {
                                    break;
                                }
                            } else if (!fragment.mAdded) {
                                if (fragment.mView != null) {
                                    if (fragment.mView.getVisibility() == 0) {
                                        break;
                                    }
                                }
                            }
                            break;
                        case 4:
                            if (z2) {
                                if (fragment.mHiddenChanged) {
                                    if (fragment.mAdded) {
                                        break;
                                    }
                                }
                            } else if (fragment.mAdded && !fragment.mHidden) {
                            }
                            break;
                        case 5:
                            if (z2) {
                                if (fragment.mHiddenChanged && !fragment.mHidden && fragment.mAdded) {
                                    break;
                                }
                            } else {
                                z4 = fragment.mHidden;
                                break;
                            }
                        case 7:
                            break;
                        default:
                            obj = null;
                            obj2 = obj;
                            obj3 = obj2;
                            break;
                    }
                }
                FragmentContainerTransition fragmentContainerTransition;
                FragmentContainerTransition fragmentContainerTransition2;
                FragmentManagerImpl fragmentManagerImpl;
                if (z2) {
                    z4 = fragment.mIsNewlyAdded;
                    obj2 = null;
                    obj3 = obj2;
                    z5 = z4;
                    obj = 1;
                    fragmentContainerTransition = (FragmentContainerTransition) sparseArray2.get(i);
                    if (z5) {
                        fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray2, i);
                        fragmentContainerTransition.lastIn = fragment;
                        fragmentContainerTransition.lastInIsPop = z3;
                        fragmentContainerTransition.lastInTransaction = backStackRecord2;
                    }
                    fragmentContainerTransition2 = fragmentContainerTransition;
                    if (!(z2 || r1 == null)) {
                        if (fragmentContainerTransition2 != null && fragmentContainerTransition2.firstOut == fragment) {
                            fragmentContainerTransition2.firstOut = null;
                        }
                        fragmentManagerImpl = backStackRecord2.mManager;
                        if (fragment.mState < 1 && fragmentManagerImpl.mCurState >= 1 && !backStackRecord2.mReorderingAllowed) {
                            fragmentManagerImpl.makeActive(fragment);
                            fragmentManagerImpl.moveToState(fragment, 1, 0, 0, false);
                        }
                    }
                    if (obj3 != null && (fragmentContainerTransition2 == null || fragmentContainerTransition2.firstOut == null)) {
                        fragmentContainerTransition2 = ensureContainer(fragmentContainerTransition2, sparseArray2, i);
                        fragmentContainerTransition2.firstOut = fragment;
                        fragmentContainerTransition2.firstOutIsPop = z3;
                        fragmentContainerTransition2.firstOutTransaction = backStackRecord2;
                    }
                    if (!(z2 || r12 == null || fragmentContainerTransition2 == null || fragmentContainerTransition2.lastIn != fragment)) {
                        fragmentContainerTransition2.lastIn = null;
                    }
                }
                if (!fragment.mAdded) {
                }
                z4 = false;
                obj2 = null;
                obj3 = obj2;
                z5 = z4;
                obj = 1;
                fragmentContainerTransition = (FragmentContainerTransition) sparseArray2.get(i);
                if (z5) {
                    fragmentContainerTransition = ensureContainer(fragmentContainerTransition, sparseArray2, i);
                    fragmentContainerTransition.lastIn = fragment;
                    fragmentContainerTransition.lastInIsPop = z3;
                    fragmentContainerTransition.lastInTransaction = backStackRecord2;
                }
                fragmentContainerTransition2 = fragmentContainerTransition;
                fragmentContainerTransition2.firstOut = null;
                fragmentManagerImpl = backStackRecord2.mManager;
                fragmentManagerImpl.makeActive(fragment);
                fragmentManagerImpl.moveToState(fragment, 1, 0, 0, false);
                fragmentContainerTransition2 = ensureContainer(fragmentContainerTransition2, sparseArray2, i);
                fragmentContainerTransition2.firstOut = fragment;
                fragmentContainerTransition2.firstOutIsPop = z3;
                fragmentContainerTransition2.firstOutTransaction = backStackRecord2;
                fragmentContainerTransition2.lastIn = null;
            }
        }
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition fragmentContainerTransition, SparseArray<FragmentContainerTransition> sparseArray, int i) {
        if (fragmentContainerTransition != null) {
            return fragmentContainerTransition;
        }
        fragmentContainerTransition = new FragmentContainerTransition();
        sparseArray.put(i, fragmentContainerTransition);
        return fragmentContainerTransition;
    }
}
