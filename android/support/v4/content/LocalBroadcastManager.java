package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class LocalBroadcastManager {
    private static final boolean DEBUG = false;
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static LocalBroadcastManager mInstance;
    private static final Object mLock = new Object();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap();
    private final Context mAppContext;
    private final Handler mHandler;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList();
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers = new HashMap();

    private static final class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent intent, ArrayList<ReceiverRecord> arrayList) {
            this.intent = intent;
            this.receivers = arrayList;
        }
    }

    private static final class ReceiverRecord {
        boolean broadcasting;
        boolean dead;
        final IntentFilter filter;
        final BroadcastReceiver receiver;

        ReceiverRecord(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
            this.filter = intentFilter;
            this.receiver = broadcastReceiver;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder(128);
            stringBuilder.append("Receiver{");
            stringBuilder.append(this.receiver);
            stringBuilder.append(" filter=");
            stringBuilder.append(this.filter);
            if (this.dead) {
                stringBuilder.append(" DEAD");
            }
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    public static LocalBroadcastManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            context = mInstance;
        }
        return context;
    }

    private LocalBroadcastManager(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    super.handleMessage(message);
                } else {
                    LocalBroadcastManager.this.executePendingBroadcasts();
                }
            }
        };
    }

    public void registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        synchronized (this.mReceivers) {
            ReceiverRecord receiverRecord = new ReceiverRecord(intentFilter, broadcastReceiver);
            ArrayList arrayList = (ArrayList) this.mReceivers.get(broadcastReceiver);
            if (arrayList == null) {
                arrayList = new ArrayList(1);
                this.mReceivers.put(broadcastReceiver, arrayList);
            }
            arrayList.add(receiverRecord);
            for (broadcastReceiver = null; broadcastReceiver < intentFilter.countActions(); broadcastReceiver++) {
                String action = intentFilter.getAction(broadcastReceiver);
                ArrayList arrayList2 = (ArrayList) this.mActions.get(action);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList(1);
                    this.mActions.put(action, arrayList2);
                }
                arrayList2.add(receiverRecord);
            }
        }
    }

    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        synchronized (this.mReceivers) {
            ArrayList arrayList = (ArrayList) this.mReceivers.remove(broadcastReceiver);
            if (arrayList == null) {
                return;
            }
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ReceiverRecord receiverRecord = (ReceiverRecord) arrayList.get(size);
                receiverRecord.dead = true;
                for (int i = 0; i < receiverRecord.filter.countActions(); i++) {
                    String action = receiverRecord.filter.getAction(i);
                    ArrayList arrayList2 = (ArrayList) this.mActions.get(action);
                    if (arrayList2 != null) {
                        for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                            ReceiverRecord receiverRecord2 = (ReceiverRecord) arrayList2.get(size2);
                            if (receiverRecord2.receiver == broadcastReceiver) {
                                receiverRecord2.dead = true;
                                arrayList2.remove(size2);
                            }
                        }
                        if (arrayList2.size() <= 0) {
                            this.mActions.remove(action);
                        }
                    }
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean sendBroadcast(Intent intent) {
        Intent intent2 = intent;
        synchronized (this.mReceivers) {
            try {
                String str;
                StringBuilder stringBuilder;
                String action = intent.getAction();
                String resolveTypeIfNeeded = intent2.resolveTypeIfNeeded(r1.mAppContext.getContentResolver());
                Uri data = intent.getData();
                String scheme = intent.getScheme();
                Set categories = intent.getCategories();
                Object obj = (intent.getFlags() & 8) != 0 ? 1 : null;
                if (obj != null) {
                    str = TAG;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Resolving type ");
                    stringBuilder.append(resolveTypeIfNeeded);
                    stringBuilder.append(" scheme ");
                    stringBuilder.append(scheme);
                    stringBuilder.append(" of intent ");
                    stringBuilder.append(intent2);
                    Log.v(str, stringBuilder.toString());
                }
                ArrayList arrayList = (ArrayList) r1.mActions.get(intent.getAction());
                if (arrayList != null) {
                    ArrayList arrayList2;
                    int match;
                    if (obj != null) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Action list: ");
                        stringBuilder.append(arrayList);
                        Log.v(str, stringBuilder.toString());
                    }
                    ArrayList arrayList3 = null;
                    int i = 0;
                    while (i < arrayList.size()) {
                        int i2;
                        ArrayList arrayList4;
                        String str2;
                        String str3;
                        Uri uri;
                        ReceiverRecord receiverRecord = (ReceiverRecord) arrayList.get(i);
                        if (obj != null) {
                            str = TAG;
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Matching against filter ");
                            stringBuilder2.append(receiverRecord.filter);
                            Log.v(str, stringBuilder2.toString());
                        }
                        if (receiverRecord.broadcasting) {
                            if (obj != null) {
                                Log.v(TAG, "  Filter's target already added");
                            }
                            i2 = i;
                            arrayList4 = arrayList;
                            str2 = action;
                            str3 = resolveTypeIfNeeded;
                            uri = data;
                            arrayList2 = arrayList3;
                        } else {
                            ReceiverRecord receiverRecord2 = receiverRecord;
                            String str4 = action;
                            str2 = action;
                            arrayList2 = arrayList3;
                            i2 = i;
                            arrayList4 = arrayList;
                            str3 = resolveTypeIfNeeded;
                            uri = data;
                            ReceiverRecord receiverRecord3 = receiverRecord2;
                            match = receiverRecord.filter.match(str4, resolveTypeIfNeeded, scheme, data, categories, TAG);
                            StringBuilder stringBuilder3;
                            if (match >= 0) {
                                if (obj != null) {
                                    str4 = TAG;
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("  Filter matched!  match=0x");
                                    stringBuilder3.append(Integer.toHexString(match));
                                    Log.v(str4, stringBuilder3.toString());
                                }
                                arrayList3 = arrayList2 == null ? new ArrayList() : arrayList2;
                                arrayList3.add(receiverRecord3);
                                receiverRecord3.broadcasting = true;
                                i = i2 + 1;
                                action = str2;
                                arrayList = arrayList4;
                                resolveTypeIfNeeded = str3;
                                data = uri;
                            } else if (obj != null) {
                                switch (match) {
                                    case -4:
                                        str = "category";
                                        break;
                                    case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                                        str = "action";
                                        break;
                                    case -2:
                                        str = "data";
                                        break;
                                    case -1:
                                        str = "type";
                                        break;
                                    default:
                                        str = "unknown reason";
                                        break;
                                }
                                str4 = TAG;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("  Filter did not match: ");
                                stringBuilder3.append(str);
                                Log.v(str4, stringBuilder3.toString());
                            }
                        }
                        arrayList3 = arrayList2;
                        i = i2 + 1;
                        action = str2;
                        arrayList = arrayList4;
                        resolveTypeIfNeeded = str3;
                        data = uri;
                    }
                    arrayList2 = arrayList3;
                    if (arrayList2 != null) {
                        for (match = 0; match < arrayList2.size(); match++) {
                            ((ReceiverRecord) arrayList2.get(match)).broadcasting = false;
                        }
                        r1.mPendingBroadcasts.add(new BroadcastRecord(intent2, arrayList2));
                        if (!r1.mHandler.hasMessages(1)) {
                            r1.mHandler.sendEmptyMessage(1);
                        }
                    }
                }
                return false;
            } catch (Throwable th) {
                Throwable th2 = th;
            }
        }
    }

    public void sendBroadcastSync(Intent intent) {
        if (sendBroadcast(intent) != null) {
            executePendingBroadcasts();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executePendingBroadcasts() {
        while (true) {
            synchronized (this.mReceivers) {
                int size = this.mPendingBroadcasts.size();
                if (size <= 0) {
                    return;
                }
                BroadcastRecord[] broadcastRecordArr = new BroadcastRecord[size];
                this.mPendingBroadcasts.toArray(broadcastRecordArr);
                this.mPendingBroadcasts.clear();
            }
        }
    }
}
