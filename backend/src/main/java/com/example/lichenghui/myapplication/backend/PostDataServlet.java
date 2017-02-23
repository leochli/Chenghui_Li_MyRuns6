package com.example.lichenghui.myapplication.backend;


import com.example.lichenghui.myapplication.backend.data.mDataStore;
import com.example.lichenghui.myapplication.backend.data.mEntry;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class PostDataServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        //Get the data from request
        String jArrayString = req.getParameter("result");
        String regID = req.getParameter("regId");
        ArrayList<mEntry> result = new ArrayList<>();

        //Delete all the entries in the data store
        for( mEntry data: mDataStore.ReadEntry()){
            mDataStore.delete(data.mID);
        }

        // Get JSON array
        JSONArray dataArray = null;
        try {
            dataArray = new JSONArray(jArrayString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get back the entry data from JSON object
        for(int i = 0; i < dataArray.length(); i++) {

            try {
                // Get json object back
                JSONObject downobject = dataArray.getJSONObject(i);

                String id = (String) downobject.get(mEntry.FIELD_NAME_ID);
                String input = (String) downobject.get(mEntry.FIELD_NAME_INPUT_TYPE);
                String activity = (String) downobject.get(mEntry.FIELD_NAME_ACTIVITY_TYPE);
                String datetime = (String) downobject.get(mEntry.FIELD_NAME_DATETIME);
                String duration = (String) downobject.get(mEntry.FIELD_NAME_DURATION);
                String distance = (String) downobject.get(mEntry.FIELD_NAME_DISTANCE);
                String avgspeed = (String) downobject.get(mEntry.FIELD_NAME_AVGSPEED);
                String calories = (String) downobject.get(mEntry.FIELD_NAME_CALORIES);
                String climb = (String) downobject.get(mEntry.FIELD_NAME_CLIMB);
                String heartrate = (String) downobject.get(mEntry.FIELD_NAME_HEARTRATE);
                String comment = (String) downobject.get(mEntry.FIELD_NAME_COMMENT);

                mEntry entry = new mEntry(id, input, activity, datetime, duration, distance,
                        avgspeed, calories, climb, heartrate, comment);

                if (mDataStore.add(entry)) {
                    result.add(entry);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        req.setAttribute("result", result);
        getServletContext().getRequestDispatcher("/MyServlet.do").forward(
                req, resp);
    }

    /**
     * doPost to call doGet
     * @param req
     * @param resp
     * @throws IOException
     * @throws ServletException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}