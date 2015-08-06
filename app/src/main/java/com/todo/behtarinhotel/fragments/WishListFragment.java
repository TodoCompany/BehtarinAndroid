package com.todo.behtarinhotel.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;
import com.todo.behtarinhotel.R;
import com.todo.behtarinhotel.adapters.MainActivityMainListAdapter;
import com.todo.behtarinhotel.adapters.WishListAdapter;
import com.todo.behtarinhotel.simpleobjects.SearchResultSO;
import com.todo.behtarinhotel.supportclasses.AppState;
import com.todo.behtarinhotel.supportclasses.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishListFragment extends Fragment {

    ArrayList<Integer> wishListItems = new ArrayList<>();
    ArrayList<SearchResultSO> hotels = new ArrayList<>();
    ListView listView;
    WishListAdapter adapter;
    View rootView;

    private static final String API_KEY = "7tuermyqnaf66ujk2dk3rkfk";
    private static final String CID = "55505";



    String url;
    String apiKey = "&apiKey=";
    String cid = "&cid=";
    String locale = "&locale=enUS";
    String customerSessionID = "&customerSessionID=1";
    String customerIpAddress = "&customerIpAddress=193.93.219.63";
    String currencyCode = "&currencyCode=USD";
    String sig = "&sig=" + AppState.getMD5EncryptedString(apiKey + "RyqEsq69" + System.currentTimeMillis() / 1000L);
    String minorRev = "&minorRev=30";
    String holelIdList = "&hotelIdList=";


    GsonBuilder gsonBuilder;
    Gson gson;



    public WishListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_wish_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.lv_wish_list_wish_list_fragment);
        wishListItems = AppState.getWishList();
        if(wishListItems!=null){
            loadHotelsFromExpedia();
        }
        return rootView;
    }

    private void loadHotelsFromExpedia(){
        url = "http://api.ean.com/ean-services/rs/hotel/v3/list?" +
                apiKey + API_KEY +
                cid + CID +
                sig +
                customerIpAddress +
                currencyCode +
                customerSessionID +
                minorRev +
                locale +
                holelIdList + makeHotelIdString()
        ;


        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ExpediaRequest", "First hotels loading from: " + url);


                        JSONArray arr = null;
                        JSONObject obj = null;
                        try {
                            arr = response.getJSONObject("HotelListResponse").getJSONObject("HotelList").getJSONArray("HotelSummary");
                        } catch (JSONException e) {
                            try {
                                obj = response.getJSONObject("HotelListResponse").getJSONObject("HotelList").getJSONObject("HotelSummary");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                        Type listOfTestObject = new TypeToken<ArrayList<SearchResultSO>>() {
                        }.getType();

                        if (getActivity() != null) {
                            if(arr != null){
                                hotels = gson.fromJson(arr.toString(), listOfTestObject);
                                Log.d("ExpediaRequest", "There was " + hotels.size() + " hotels");
                                adapter = new WishListAdapter(getActivity(),hotels);
                                listView.setAdapter(adapter);
                            }else if(obj!=null){
                                hotels.clear();
                                SearchResultSO so = gson.fromJson(obj.toString(),SearchResultSO.class);
                                hotels.add(so);
                                Log.d("ExpediaRequest", "There was " + hotels.size() + " hotels");
                                adapter = new WishListAdapter(getActivity(),hotels);
                                listView.setAdapter(adapter);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }
        );
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private String makeHotelIdString(){
        String hotelSting = "";
        for(Integer item : wishListItems){
            hotelSting = hotelSting + item + ",";
        }
        return hotelSting;
    }


}