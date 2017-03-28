package com.dms.beinone.application.stayapply;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dms.beinone.application.R;
import com.dms.beinone.application.dmsview.DMSButton;
import com.dms.beinone.application.dmsview.DMSRadioButton;
import com.dms.beinone.application.utils.DateUtils;
import com.dms.boxfox.networking.HttpBox;
import com.dms.boxfox.networking.datamodel.Request;
import com.dms.boxfox.networking.datamodel.Response;
import com.samsistemas.calendarview.widget.CalendarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.dms.beinone.application.stayapply.StayApplyUtils.FRIDAY_GO;
import static com.dms.beinone.application.stayapply.StayApplyUtils.SATURDAY_COME;
import static com.dms.beinone.application.stayapply.StayApplyUtils.SATURDAY_GO;
import static com.dms.beinone.application.stayapply.StayApplyUtils.STAY;

/**
 * Created by BeINone on 2017-01-14.
 */

public class StayApplyFragment extends Fragment {

    private TextView mDefaultStatusTV;
    private TextView mSelectedWeekTV;
    private TextView mSelectedWeekStatusTV;

    private SharedPreferences mAccountPrefs;
    private SharedPreferences mDefaultStatusPrefs;

    private Date mSelectedDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stayapply, container, false);
        init(view);

        return view;
    }

    /**
     * 초기화, 달력 날짜 클릭 이벤트 설정, 신청 버튼 클릭 이벤트 설정
     *
     * @param rootView 필요한 뷰를 찾을 최상위 뷰
     */
    private void init(View rootView) {
        getActivity().setTitle(R.string.nav_stayapply);
        mAccountPrefs = getActivity()
                .getSharedPreferences(getString(R.string.PREFS_ACCOUNT), MODE_PRIVATE);
        mDefaultStatusPrefs = getActivity()
                .getSharedPreferences(getString(R.string.PREFS_DEFAULTSTATUS), MODE_PRIVATE);

        mDefaultStatusTV = (TextView) rootView.findViewById(R.id.tv_stayapply_defaultstatus);
        mSelectedWeekTV = (TextView) rootView.findViewById(R.id.tv_stayapply_selectedweek);
        mSelectedWeekStatusTV = (TextView) rootView.findViewById(R.id.tv_stayapply_selectedweekstatus);
        final CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendar_stayapply);

        // load and display default stay status of user
        new LoadDefaultStayStatusTask().execute();

        Button changeDefaultStatusBtn = (Button) rootView.findViewById(R.id.btn_stayapply_changedefaultstatus);
        changeDefaultStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeDefaultStatusDialog.newInstance(
                        getContext(),
                        mDefaultStatusPrefs.getInt(getString(R.string.PREFS_DEFAULTSTATUS_DEFAULTSTATUS), STAY),
                        new ChangeDefaultStatusDialog.ChangeDefaultStatusListener() {
                            @Override
                            public void onChangeDefaultStatus(int defaultStatus) {
                                mDefaultStatusPrefs.edit()
                                        .putInt(getString(R.string.PREFS_DEFAULTSTATUS_DEFAULTSTATUS), defaultStatus)
                                        .apply();
                                setDefaultStatusTV(StayApplyUtils.getStringFromStayStatus(defaultStatus));
                            }
                        })
                        .show(getChildFragmentManager(), null);
            }
        });

        // display selected week at initially
        setSelectedWeekTV(calendarView.getLastSelectedWeekString());

        // load and display stay status of selected week at initially
        mSelectedDate = new Date();
        new LoadStayStatusTask().execute(DateUtils.dateToWeekDateString(mSelectedDate));

        calendarView.setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date date) {
                mSelectedDate = date;
                setSelectedWeekTV(calendarView.getLastSelectedWeekString());

                setSelectedWeekStatusTV(null);

                // load and display stay status of selected week
                new LoadStayStatusTask().execute(DateUtils.dateToWeekDateString(mSelectedDate));
            }
        });

        final DMSRadioButton fridayGoRB =
                (DMSRadioButton) rootView.findViewById(R.id.rb_stayapply_fridaygo);
        final DMSRadioButton saturdayGoRB =
                (DMSRadioButton) rootView.findViewById(R.id.rb_stayapply_saturdaygo);
        final DMSRadioButton saturdayComeRB =
                (DMSRadioButton) rootView.findViewById(R.id.rb_stayapply_saturdaycome);
        final DMSRadioButton stayRB = (DMSRadioButton) rootView.findViewById(R.id.rb_stayapply_stay);

        // apply stay status when apply button is clicked
        DMSButton applyBtn = (DMSButton) rootView.findViewById(R.id.btn_stayapply_apply);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fridayGoRB.isChecked()) {
                    new ApplyStayStatusTask()
                            .execute(FRIDAY_GO, DateUtils.dateToWeekDateString(mSelectedDate));
                } else if (saturdayGoRB.isChecked()) {
                    new ApplyStayStatusTask()
                            .execute(SATURDAY_GO, DateUtils.dateToWeekDateString(mSelectedDate));
                } else if (saturdayComeRB.isChecked()) {
                    new ApplyStayStatusTask()
                            .execute(SATURDAY_COME, DateUtils.dateToWeekDateString(mSelectedDate));
                } else if (stayRB.isChecked()) {
                    new ApplyStayStatusTask()
                            .execute(STAY, DateUtils.dateToWeekDateString(mSelectedDate));
                } else {
                    Toast.makeText(getContext(), R.string.stayapply_nochecked, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSelectedWeekTV(String week) {
        mSelectedWeekTV.setText(week);
    }

    private void setDefaultStatusTV(String stayState) {
        mDefaultStatusTV.setText(stayState);
    }

    private void setSelectedWeekStatusTV(String stayState) {
        mSelectedWeekStatusTV.setText(stayState);
    }

    /**
     * load stay status of the date from server and display it
     */
    private class LoadStayStatusTask extends AsyncTask<String, Void, int[]> {

        @Override
        protected int[] doInBackground(String... params) {
            int[] values = null;

            try {
                values = loadStayStatus(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return new int[]{-1, -1};
            } catch (JSONException e) {
                e.printStackTrace();
                return new int[]{-1, -1};
            }

            return values;
        }

        @Override
        protected void onPostExecute(int[] values) {
            super.onPostExecute(values);

            int code = values[0];
            int stayStatus = values[1];

            if (code == 200 || code == 204) {
                // success
                setSelectedWeekStatusTV(StayApplyUtils.getStringFromStayStatus(stayStatus));
            } else {
                // error
                Toast.makeText(getContext(), R.string.stayapply_error, Toast.LENGTH_SHORT).show();
            }
        }

        private int[] loadStayStatus(String week) throws IOException, JSONException {
            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("week", week);

            Response response = HttpBox.post(getContext(), "/apply/stay", Request.TYPE_GET)
                    .putBodyData(requestParams)
                    .push();

            int code = response.getCode();
            int stayStatus = -1;
            if (code == 200) {
                stayStatus = response.getJsonObject().getInt("value");
            } else if (code == 204) {
                stayStatus = mDefaultStatusPrefs.getInt(
                        getString(R.string.PREFS_DEFAULTSTATUS_DEFAULTSTATUS), 0);
            }

            return new int[]{response.getCode(), stayStatus};
        }

    }

    /**
     * load default stay status of the user from server and display it
     */
    private class LoadDefaultStayStatusTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int stayStatus = -1;

            try {
                stayStatus = loadDefaultStayStatus();
            } catch (IOException e) {
                return -1;
            } catch (JSONException e) {
                return -1;
            }

            return stayStatus;
        }

        @Override
        protected void onPostExecute(Integer stayStatus) {
            super.onPostExecute(stayStatus);

            if (stayStatus == -1) {
                Toast.makeText(getContext(), R.string.stayapply_default_error, Toast.LENGTH_SHORT).show();
            }

            setDefaultStatusTV(StayApplyUtils.getStringFromStayStatus(stayStatus));
            mDefaultStatusPrefs.edit()
                    .putInt(getString(R.string.PREFS_DEFAULTSTATUS_DEFAULTSTATUS), stayStatus)
                    .apply();
        }

        private int loadDefaultStayStatus() throws IOException, JSONException {
            Map<String, String> requestParams = new HashMap<>();

            Response response = HttpBox.post(getContext(), "/apply/stay/default", Request.TYPE_GET)
                    .putBodyData(requestParams)
                    .push();
            JSONObject stayStatusJSONObject = response.getJsonObject();

            return stayStatusJSONObject.getInt("value");
        }

    }

    private class ApplyStayStatusTask extends AsyncTask<Object, Void, int[]> {

        @Override
        protected int[] doInBackground(Object... params) {
            int[] values = null;

            try {
                int value = (int) params[0];
                String week = params[1].toString();
                values = applyStayStatus(value, week);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return values;
        }

        @Override
        protected void onPostExecute(int[] values) {
            super.onPostExecute(values);

            int code = values[0];
            int stayStatus = values[1];

            if (code == 200) {
                /* succeed */
                Toast.makeText(getContext(), R.string.stayapply_apply_success, Toast.LENGTH_SHORT)
                        .show();
                setSelectedWeekStatusTV(StayApplyUtils.getStringFromStayStatus(stayStatus));
            } else if (code == 204) {
                /* failed */
                Toast.makeText(getContext(), R.string.stayapply_apply_failure, Toast.LENGTH_SHORT)
                        .show();
            } else {
                /* error */
                Toast.makeText(getContext(), R.string.stayapply_apply_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        private int[] applyStayStatus(int value, String week) throws IOException, JSONException {

            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("value", String.valueOf(value));
            requestParams.put("week", week);

            Response response = HttpBox.post(getContext(), "/apply/stay", Request.TYPE_PUT)
                    .putBodyData(requestParams)
                    .push();

            return new int[]{response.getCode(), value};
        }
    }

}