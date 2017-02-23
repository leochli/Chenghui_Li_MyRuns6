/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.lichenghui.myapplication.backend;

import com.example.lichenghui.myapplication.backend.data.mDataStore;
import com.example.lichenghui.myapplication.backend.data.mEntry;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        ArrayList<mEntry> resultList = mDataStore.ReadEntry();

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.print("<html><body>");
        out.print("<head>");
        out.print("  <title>Chenghui_Li_MyRuns6</title>");
        out.print("</head>");

        out.print("<h2>History Entries for your device:</h2>");

        String status = (String) req.getAttribute("status");
        if (status != null) {
            out.print(status);
        }
        out.print("<table border=\"1\">");
        out.print("<tr>");
        out.print("<td>ID</td>");
        out.print("<td>InputType</td>");
        out.print("<td>ActivityType</td>");
        out.print("<td>DateTime</td>");
        out.print("<td>Duration</td>");
        out.print("<td>Distance</td>");
        out.print("<td>AvgSpeed</td>");
        out.print("<td>Calorie</td>");
        out.print("<td>Climb</td>");
        out.print("<td>Heartrate</td>");
        out.print("<td>Comment</td>");
        out.print("<td/>");
        out.print("</tr>");

        if (resultList != null) {
            for (mEntry entry : resultList) {
                out.print("<tr>");
                out.print("<td>"+entry.mID+"</td>");
                out.print("<td>"+entry.mInputType+"</td>");
                out.print("<td>"+entry.mActivityType+"</td>");
                out.print("<td>"+ entry.mDateTime +"</td>");
                out.print("<td>" + entry.mDuration + "</td>");
                out.print("<td>"+ entry.mDistance + "</td>");
                out.print("<td>"+entry.mAverageSpeed+"</td>");
                out.print("<td>"+entry.mCalories+"</td>");
                out.print("<td>"+entry.mClimb+"</td>");
                out.print("<td>"+entry.mHeartRate+"</td>");
                out.print("<td>"+entry.mComment+"</td>");
                out.print("<td>" +
                        "<input type=\"button\" onclick=\"location.href='/delete.do?id="+
                        entry.mID+
                        "'\" value=\"Delete\">" +
                        "</td>");
                out.print("</tr>");
            }
            out.print("</table>");
            out.print("</body></html>");
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        doGet(req,resp);
    }
}
