package io.jpress.commons;

public class ApiResult {

    private static final String STATE_OK = "ok";
    private static final String STATE_FAIL = "fail";

    private String state;
    private String message;
    private int code;
    private Object data;


    public static ApiResult ok() {
        return new ApiResult().setOk();
    }

    public static ApiResult fail() {
        return new ApiResult().setFail();
    }


    public ApiResult setOk() {
        this.state = STATE_OK;
        return this;
    }

    public ApiResult setFail() {
        this.state = STATE_FAIL;
        return this;
    }


    public String getState() {
        return state;
    }

    public ApiResult setState(String state) {
        this.state = state;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ApiResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ApiResult setCode(int code) {
        this.code = code;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ApiResult setData(Object data) {
        this.data = data;
        return this;
    }

    public boolean isOk() {
        return STATE_OK.equals(state);
    }

    public boolean isFail() {
        return STATE_FAIL.equals(state);
    }
}
