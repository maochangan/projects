package com.shhy.demo.util;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class JsonResult {
    private int status;

    private String message;

    private Map<String, Object> extend = new HashMap<String, Object>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

    public static JsonResult success() {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setStatus(1);
        jsonResult.setMessage("success");
        return jsonResult;
    }

    public static JsonResult fail() {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setStatus(0);
        jsonResult.setMessage("fail");
        return jsonResult;
    }

    public JsonResult add(String key, Object value) {
        this.extend.put(key, value);
        return this;
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> exceptionResult(Exception exception) {
        exception.printStackTrace();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 500);
        result.put("message", "系统异常，请稍后再试！");
        return result;
    }
}
