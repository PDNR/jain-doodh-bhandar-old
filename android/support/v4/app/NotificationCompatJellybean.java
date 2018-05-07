package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.InboxStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase.Action;
import android.support.v4.app.NotificationCompatBase.Action.Factory;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(16)
class NotificationCompatJellybean {
    static final String EXTRA_ALLOW_GENERATED_REPLIES = "android.support.allowGeneratedReplies";
    static final String EXTRA_DATA_ONLY_REMOTE_INPUTS = "android.support.dataRemoteInputs";
    private static final String KEY_ACTION_INTENT = "actionIntent";
    private static final String KEY_DATA_ONLY_REMOTE_INPUTS = "dataOnlyRemoteInputs";
    private static final String KEY_EXTRAS = "extras";
    private static final String KEY_ICON = "icon";
    private static final String KEY_REMOTE_INPUTS = "remoteInputs";
    private static final String KEY_TITLE = "title";
    public static final String TAG = "NotificationCompat";
    private static Class<?> sActionClass;
    private static Field sActionIconField;
    private static Field sActionIntentField;
    private static Field sActionTitleField;
    private static boolean sActionsAccessFailed;
    private static Field sActionsField;
    private static final Object sActionsLock = new Object();
    private static Field sExtrasField;
    private static boolean sExtrasFieldAccessFailed;
    private static final Object sExtrasLock = new Object();

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private android.app.Notification.Builder b;
        private List<Bundle> mActionExtrasList = new ArrayList();
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private final Bundle mExtras;

        public Builder(Context context, Notification notification, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, RemoteViews remoteViews, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, Bitmap bitmap, int i2, int i3, boolean z, boolean z2, int i4, CharSequence charSequence4, boolean z3, Bundle bundle, String str, boolean z4, String str2, RemoteViews remoteViews2, RemoteViews remoteViews3) {
            PendingIntent pendingIntent3;
            Notification notification2 = notification;
            Bundle bundle2 = bundle;
            String str3 = str;
            String str4 = str2;
            boolean z5 = false;
            android.app.Notification.Builder deleteIntent = new android.app.Notification.Builder(context).setWhen(notification2.when).setSmallIcon(notification2.icon, notification2.iconLevel).setContent(notification2.contentView).setTicker(notification2.tickerText, remoteViews).setSound(notification2.sound, notification2.audioStreamType).setVibrate(notification2.vibrate).setLights(notification2.ledARGB, notification2.ledOnMS, notification2.ledOffMS).setOngoing((notification2.flags & 2) != 0).setOnlyAlertOnce((notification2.flags & 8) != 0).setAutoCancel((notification2.flags & 16) != 0).setDefaults(notification2.defaults).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence4).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification2.deleteIntent);
            if ((notification2.flags & 128) != 0) {
                pendingIntent3 = pendingIntent2;
                z5 = true;
            } else {
                pendingIntent3 = pendingIntent2;
            }
            r0.b = deleteIntent.setFullScreenIntent(pendingIntent3, z5).setLargeIcon(bitmap).setNumber(i).setUsesChronometer(z2).setPriority(i4).setProgress(i2, i3, z);
            r0.mExtras = new Bundle();
            if (bundle2 != null) {
                r0.mExtras.putAll(bundle2);
            }
            if (z3) {
                r0.mExtras.putBoolean(NotificationCompatExtras.EXTRA_LOCAL_ONLY, true);
            }
            if (str3 != null) {
                r0.mExtras.putString(NotificationCompatExtras.EXTRA_GROUP_KEY, str3);
                if (z4) {
                    r0.mExtras.putBoolean(NotificationCompatExtras.EXTRA_GROUP_SUMMARY, true);
                } else {
                    r0.mExtras.putBoolean(NotificationManagerCompat.EXTRA_USE_SIDE_CHANNEL, true);
                }
            }
            if (str4 != null) {
                r0.mExtras.putString(NotificationCompatExtras.EXTRA_SORT_KEY, str4);
            }
            r0.mContentView = remoteViews2;
            r0.mBigContentView = remoteViews3;
        }

        public void addAction(Action action) {
            this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, action));
        }

        public android.app.Notification.Builder getBuilder() {
            return this.b;
        }

        public Notification build() {
            Notification build = this.b.build();
            Bundle extras = NotificationCompatJellybean.getExtras(build);
            Bundle bundle = new Bundle(this.mExtras);
            for (String str : this.mExtras.keySet()) {
                if (extras.containsKey(str)) {
                    bundle.remove(str);
                }
            }
            extras.putAll(bundle);
            SparseArray buildActionExtrasMap = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
            if (buildActionExtrasMap != null) {
                NotificationCompatJellybean.getExtras(build).putSparseParcelableArray(NotificationCompatExtras.EXTRA_ACTION_EXTRAS, buildActionExtrasMap);
            }
            if (this.mContentView != null) {
                build.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                build.bigContentView = this.mBigContentView;
            }
            return build;
        }
    }

    NotificationCompatJellybean() {
    }

    public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, CharSequence charSequence3) {
        notificationBuilderWithBuilderAccessor = new BigTextStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence).bigText(charSequence3);
        if (z) {
            notificationBuilderWithBuilderAccessor.setSummaryText(charSequence2);
        }
    }

    public static void addBigPictureStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, Bitmap bitmap, Bitmap bitmap2, boolean z2) {
        notificationBuilderWithBuilderAccessor = new BigPictureStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence).bigPicture(bitmap);
        if (z2) {
            notificationBuilderWithBuilderAccessor.bigLargeIcon(bitmap2);
        }
        if (z) {
            notificationBuilderWithBuilderAccessor.setSummaryText(charSequence2);
        }
    }

    public static void addInboxStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, ArrayList<CharSequence> arrayList) {
        notificationBuilderWithBuilderAccessor = new InboxStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence);
        if (z) {
            notificationBuilderWithBuilderAccessor.setSummaryText(charSequence2);
        }
        charSequence = arrayList.iterator();
        while (charSequence.hasNext()) {
            notificationBuilderWithBuilderAccessor.addLine((CharSequence) charSequence.next());
        }
    }

    public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> list) {
        int size = list.size();
        SparseArray<Bundle> sparseArray = null;
        for (int i = 0; i < size; i++) {
            Bundle bundle = (Bundle) list.get(i);
            if (bundle != null) {
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                }
                sparseArray.put(i, bundle);
            }
        }
        return sparseArray;
    }

    public static Bundle getExtras(Notification notification) {
        synchronized (sExtrasLock) {
            if (sExtrasFieldAccessFailed) {
                return null;
            }
            try {
                if (sExtrasField == null) {
                    Field declaredField = Notification.class.getDeclaredField(KEY_EXTRAS);
                    if (Bundle.class.isAssignableFrom(declaredField.getType())) {
                        declaredField.setAccessible(true);
                        sExtrasField = declaredField;
                    } else {
                        Log.e(TAG, "Notification.extras field is not of type Bundle");
                        sExtrasFieldAccessFailed = true;
                        return null;
                    }
                }
                Bundle bundle = (Bundle) sExtrasField.get(notification);
                if (bundle == null) {
                    bundle = new Bundle();
                    sExtrasField.set(notification, bundle);
                }
                return bundle;
            } catch (Notification notification2) {
                Log.e(TAG, "Unable to access notification extras", notification2);
                sExtrasFieldAccessFailed = true;
                return null;
            } catch (Notification notification22) {
                Log.e(TAG, "Unable to access notification extras", notification22);
                sExtrasFieldAccessFailed = true;
                return null;
            }
        }
    }

    public static Action readAction(Factory factory, RemoteInput.Factory factory2, int i, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle) {
        boolean z;
        RemoteInput[] remoteInputArr;
        RemoteInput[] remoteInputArr2;
        if (bundle != null) {
            RemoteInput[] fromBundleArray = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, NotificationCompatExtras.EXTRA_REMOTE_INPUTS), factory2);
            factory2 = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, EXTRA_DATA_ONLY_REMOTE_INPUTS), factory2);
            z = bundle.getBoolean(EXTRA_ALLOW_GENERATED_REPLIES);
            remoteInputArr = factory2;
            remoteInputArr2 = fromBundleArray;
        } else {
            z = false;
            remoteInputArr2 = null;
            remoteInputArr = remoteInputArr2;
        }
        return factory.build(i, charSequence, pendingIntent, bundle, remoteInputArr2, remoteInputArr, z);
    }

    public static Bundle writeActionAndGetExtras(android.app.Notification.Builder builder, Action action) {
        builder.addAction(action.getIcon(), action.getTitle(), action.getActionIntent());
        builder = new Bundle(action.getExtras());
        if (action.getRemoteInputs() != null) {
            builder.putParcelableArray(NotificationCompatExtras.EXTRA_REMOTE_INPUTS, RemoteInputCompatJellybean.toBundleArray(action.getRemoteInputs()));
        }
        if (action.getDataOnlyRemoteInputs() != null) {
            builder.putParcelableArray(EXTRA_DATA_ONLY_REMOTE_INPUTS, RemoteInputCompatJellybean.toBundleArray(action.getDataOnlyRemoteInputs()));
        }
        builder.putBoolean(EXTRA_ALLOW_GENERATED_REPLIES, action.getAllowGeneratedReplies());
        return builder;
    }

    public static int getActionCount(Notification notification) {
        synchronized (sActionsLock) {
            notification = getActionObjectsLocked(notification);
            notification = notification != null ? notification.length : null;
        }
        return notification;
    }

    public static Action getAction(Notification notification, int i, Factory factory, RemoteInput.Factory factory2) {
        synchronized (sActionsLock) {
            try {
                Object[] actionObjectsLocked = getActionObjectsLocked(notification);
                if (actionObjectsLocked != null) {
                    Bundle bundle;
                    Object obj = actionObjectsLocked[i];
                    notification = getExtras(notification);
                    if (notification != null) {
                        notification = notification.getSparseParcelableArray(NotificationCompatExtras.EXTRA_ACTION_EXTRAS);
                        if (notification != null) {
                            bundle = (Bundle) notification.get(i);
                            notification = readAction(factory, factory2, sActionIconField.getInt(obj), (CharSequence) sActionTitleField.get(obj), (PendingIntent) sActionIntentField.get(obj), bundle);
                            return notification;
                        }
                    }
                    bundle = null;
                    notification = readAction(factory, factory2, sActionIconField.getInt(obj), (CharSequence) sActionTitleField.get(obj), (PendingIntent) sActionIntentField.get(obj), bundle);
                    return notification;
                }
            } catch (Notification notification2) {
                Log.e(TAG, "Unable to access notification actions", notification2);
                sActionsAccessFailed = true;
            }
        }
        return null;
    }

    private static Object[] getActionObjectsLocked(Notification notification) {
        synchronized (sActionsLock) {
            if (ensureActionReflectionReadyLocked()) {
                try {
                    Object[] objArr = (Object[]) sActionsField.get(notification);
                    return objArr;
                } catch (Notification notification2) {
                    Log.e(TAG, "Unable to access notification actions", notification2);
                    sActionsAccessFailed = true;
                    return null;
                }
            }
            return null;
        }
    }

    private static boolean ensureActionReflectionReadyLocked() {
        if (sActionsAccessFailed) {
            return false;
        }
        try {
            if (sActionsField == null) {
                sActionClass = Class.forName("android.app.Notification$Action");
                sActionIconField = sActionClass.getDeclaredField(KEY_ICON);
                sActionTitleField = sActionClass.getDeclaredField(KEY_TITLE);
                sActionIntentField = sActionClass.getDeclaredField(KEY_ACTION_INTENT);
                sActionsField = Notification.class.getDeclaredField("actions");
                sActionsField.setAccessible(true);
            }
        } catch (Throwable e) {
            Log.e(TAG, "Unable to access notification actions", e);
            sActionsAccessFailed = true;
        } catch (Throwable e2) {
            Log.e(TAG, "Unable to access notification actions", e2);
            sActionsAccessFailed = true;
        }
        return true ^ sActionsAccessFailed;
    }

    public static Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> arrayList, Factory factory, RemoteInput.Factory factory2) {
        if (arrayList == null) {
            return null;
        }
        Action[] newArray = factory.newArray(arrayList.size());
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = getActionFromBundle((Bundle) arrayList.get(i), factory, factory2);
        }
        return newArray;
    }

    private static Action getActionFromBundle(Bundle bundle, Factory factory, RemoteInput.Factory factory2) {
        Bundle bundle2 = bundle.getBundle(KEY_EXTRAS);
        boolean z = false;
        if (bundle2 != null) {
            z = bundle2.getBoolean(EXTRA_ALLOW_GENERATED_REPLIES, false);
        }
        return factory.build(bundle.getInt(KEY_ICON), bundle.getCharSequence(KEY_TITLE), (PendingIntent) bundle.getParcelable(KEY_ACTION_INTENT), bundle.getBundle(KEY_EXTRAS), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, KEY_REMOTE_INPUTS), factory2), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, KEY_DATA_ONLY_REMOTE_INPUTS), factory2), z);
    }

    public static ArrayList<Parcelable> getParcelableArrayListForActions(Action[] actionArr) {
        if (actionArr == null) {
            return null;
        }
        ArrayList<Parcelable> arrayList = new ArrayList(actionArr.length);
        for (Action bundleForAction : actionArr) {
            arrayList.add(getBundleForAction(bundleForAction));
        }
        return arrayList;
    }

    private static Bundle getBundleForAction(Action action) {
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putInt(KEY_ICON, action.getIcon());
        bundle2.putCharSequence(KEY_TITLE, action.getTitle());
        bundle2.putParcelable(KEY_ACTION_INTENT, action.getActionIntent());
        if (action.getExtras() != null) {
            bundle = new Bundle(action.getExtras());
        } else {
            bundle = new Bundle();
        }
        bundle.putBoolean(EXTRA_ALLOW_GENERATED_REPLIES, action.getAllowGeneratedReplies());
        bundle2.putBundle(KEY_EXTRAS, bundle);
        bundle2.putParcelableArray(KEY_REMOTE_INPUTS, RemoteInputCompatJellybean.toBundleArray(action.getRemoteInputs()));
        return bundle2;
    }
}
