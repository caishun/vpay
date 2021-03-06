package com.aapreneur.vpay.Fragment;

/**
 * Created by Anmol Pratap Singh on 28-01-2018.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.aapreneur.vpay.Resources.Configuration;
import com.aapreneur.vpay.Resources.MyArrayAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.aapreneur.vpay.R;
import com.aapreneur.vpay.Resources.MyDataModel;


public class bank extends Fragment {

    private ListView listView;
    private LinearLayout linearLayout;
    private ArrayList <MyDataModel> list;
    private MyArrayAdapter adapter;


    public bank() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank, container, false);
        list = new ArrayList < > ();
        adapter = new MyArrayAdapter(getActivity(), list);
        listView = (ListView) view.findViewById(R.id.listView);
        linearLayout = (LinearLayout)view.findViewById(R.id.view_empty);
        listView.setAdapter(adapter);
        new ReadData1().execute();

        return view;
    }
    class ReadData1 extends AsyncTask < Void, Void, Void > {

        ProgressDialog dialog;
        int jIndex;
        int x;
        FirebaseAuth mAuth;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            x = list.size();

            if (x == 0)
                jIndex = 0;
            else
                jIndex = x;

            dialog = new ProgressDialog(getActivity());
            dialog.setTitle("Hey Wait Please...");
            dialog.setMessage("Fetching all your orders");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void...params) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            JSONArray jsonArray = Configuration.recentTransaction(user.getUid());
            try {
                /**
                 * Check Whether Its NULL???
                 */
                if (jsonArray != null) {
                    /**
                     * Check Length...
                     */
                    if (jsonArray.length() > 0) {

                        int lenArray = jsonArray.length();
                        if (lenArray > 0) {
                            for (; jIndex < lenArray; jIndex++) {
                                MyDataModel model = new MyDataModel();
                                JSONObject innerObject = jsonArray.getJSONObject(jIndex);

                                String paytmId = innerObject.getString("paytmId");
                                String orderId = innerObject.getString("orderId");
                                String Amount = innerObject.getString("amount");
                                String date = innerObject.getString("date");
                                String time = innerObject.getString("time");
                                String transactionId = innerObject.getString("transactionId");
                                String UTR = innerObject.getString("UTR");

                                model.setUTR(UTR);
                                model.setTransactionId(transactionId);
                                model.setAmount(Amount);
                                model.setDate(date);
                                model.setTime(time);
                                model.setOrderId(orderId);
                                model.setPaytmId(paytmId);
                                //                              model.setImage(image);

                                /**
                                 * Adding name and phone concatenation in List...
                                 */
                                list.add(model);
                            }
                        }
                    }
                } else {

                }
            } catch (JSONException je) {
                //Log.i(Controller.TAG, "" + je.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            /**
             * Checking if List size if more than zero then
             * Update ListView
             */
            if (list.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "No data found", Toast.LENGTH_LONG).show();
                listView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
