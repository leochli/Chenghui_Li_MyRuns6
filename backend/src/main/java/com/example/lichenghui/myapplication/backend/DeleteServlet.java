package com.example.lichenghui.myapplication.backend;

import com.example.lichenghui.myapplication.backend.data.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lichenghui on 23/2/2017.
 */

public class DeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String id = req.getParameter("id");
        mDataStore.delete(id);
        //Send message to my device
        MessagingEndpoint deleteMsg = new MessagingEndpoint();
        deleteMsg.sendMessage(id);
        resp.sendRedirect("/MyServlet.do");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
