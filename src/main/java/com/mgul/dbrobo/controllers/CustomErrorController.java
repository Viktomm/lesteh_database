package com.mgul.dbrobo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public Map<String, String> handleError(HttpServletRequest httpServletRequest) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("error exception", (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION));
        map.put("error exception type", (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE));
        map.put("error message", (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        map.put("error request uri", (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        map.put("error servlet name", (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME));
//        map.put("error status code", (Integer) httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        map.put("method", httpServletRequest.getMethod());
        map.put("forward request uri", (String) httpServletRequest.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI));
        return map;
    }

}
