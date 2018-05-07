package com.jaindoodhbhandaaran.appfragment.customerfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.dashboard.CustomerDashboardActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeftFrameLayout extends Fragment {
    public static CustomerDashboardActivity dashboardActivity;
    public static ListView mDrawerList;
    String TAG = getClass().getName();
    Bundle bundle;
    TextView drawer_dueBalance;
    TextView drawer_firstName;
    CircleImageView drawer_userProfile;
    Context mContext;
    View view;

    public Context getmContext() {
        return this.mContext;
    }

    public void setmContext(Context context) {
        this.mContext = context;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.view = LayoutInflater.from(getActivity()).inflate(R.layout.left_frame_layout, null);
        mDrawerList = (ListView) this.view.findViewById(R.id.drawer_list);
        this.drawer_firstName = (TextView) this.view.findViewById(R.id.drawer_firstName);
        this.drawer_dueBalance = (TextView) this.view.findViewById(R.id.drawer_dueBalance);
        try {
            layoutInflater = this.mContext;
        } catch (LayoutInflater layoutInflater2) {
            viewGroup = this.TAG;
            bundle = new StringBuilder();
            bundle.append("");
            bundle.append(layoutInflater2.toString());
            Log.e(viewGroup, bundle.toString());
        }
        return this.view;
    }
}
