package com.shhy.demo.util;


public class MapResult {

    private Object data ;

    private Integer status ;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static MapResult success(){
        MapResult result = new MapResult();
        result.setStatus(1);
        return result;
    }

    public static MapResult fail(){
        MapResult result = new MapResult();
        result.setStatus(0);
        return result;
    }

    public MapResult add(Object value) {
        this.setData(value);
        return this;
    }

}
