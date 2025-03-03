package com.abishek.comidapartner.Home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abishek.comidapartner.Home.fragment.adapter.OrderAdapter2;
import com.abishek.comidapartner.Home.fragment.adapter.OrderAdapter3;
import com.abishek.comidapartner.Home.fragment.model.MyOrderModel;
import com.abishek.comidapartner.R;
import com.abishek.comidapartner.commonFiles.LoginSessionManager;
import com.abishek.comidapartner.commonFiles.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.BASE_ORDER_COMPLETED;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.NO_OF_RETRY;
import static com.abishek.comidapartner.commonFiles.CommonVariablesAndFunctions.RETRY_SECONDS;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.ACCESS_TOKEN;
import static com.abishek.comidapartner.commonFiles.LoginSessionManager.TOKEN_TYPE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link History#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = "History";

    private String userId;
    private List<MyOrderModel> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View view;

    public History() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment History.
     */
    // TODO: Rename and change types and number of parameters
    public static History newInstance(String param1, String param2) {
        History fragment = new History();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        progressBar  = view.findViewById(R.id.progress_bar);

        userId = new LoginSessionManager(getContext()).getUserDetailsFromSP().get("user_id");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fetchNewOrders(view);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        fetchNewOrders(view);

        return view;
    }



    public void fetchNewOrders(View view)
    {


        Log.e(TAG, "fetchAllProductList : called");

        progressBar.setVisibility(View.VISIBLE);
        final String URL = BASE_ORDER_COMPLETED+userId;
        orderList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);


                try {
                    JSONObject jsonObject = new JSONObject(response);



                    JSONArray subJson = jsonObject.getJSONArray("data");

                    if(subJson.length()==0)
                    {
                        Toast.makeText(getContext(),"no order",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for(int i=0;i<subJson.length();i++)
                    {
                        JSONObject childJson = subJson.getJSONObject(i);
                        String orderId =childJson.getString("order_id");
                        String deliveredAddress=childJson.getString("delivered_address");
                        String cAddressId = childJson.getString("customer_address_id");
                        String status = childJson.getString("status");
                        String date =childJson.getString("created_at");
                        String totalPrice=childJson.getString("total_price");
                        String partnerId=childJson.getString("partner_id");
                        String Image=childJson.getString("image");



                        orderList.add(new MyOrderModel(orderId,deliveredAddress,cAddressId,status,date,totalPrice,partnerId,Image));
                    }


                    setDataToView(view);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),"server problem",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();

                String tokenType = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(TOKEN_TYPE);
                String accessToken = new LoginSessionManager(getContext()).getUserDetailsFromSP().get(ACCESS_TOKEN);

                header.put("Accept", "application/json");
                header.put("Authorization", tokenType + " " + accessToken);

                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                return super.getParams();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy((RETRY_SECONDS),
                NO_OF_RETRY, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);


    }

    public void setDataToView(View v)
    {
        RecyclerView orderRecyclerView = v.findViewById(R.id.order_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        OrderAdapter3 orderAdapter = new OrderAdapter3(orderList,getContext(),"history");
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        orderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }



    @Override
    public void onResume() {
        super.onResume();

        fetchNewOrders(view);
        Log.e(TAG,"...History..onResume.........");
    }
}