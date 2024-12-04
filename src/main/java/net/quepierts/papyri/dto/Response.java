package net.quepierts.papyri.dto;

import net.quepierts.papyri.model.ResponseState;

public record Response<T>(ResponseState state, String message, T content) {
    public static <T> Response<T> success(T content) {
        return new Response<>(ResponseState.SUCCESS, "", content);
    }

    public static <T> Response<T> failed(String reason) {
        return new Response<>(ResponseState.FAILED, reason, null);
    }

    public static <T> Response<T> notnull(T content) {
        if (content == null) {
            return new Response<>(ResponseState.FAILED, "Required Not Null", null);
        } else {
            return new Response<>(ResponseState.SUCCESS, "", content);
        }
    }

    public static <T> Response<T> error(String reason) {
        return new Response<>(ResponseState.ERROR, reason, null);
    }

    public static Response<Void> flag(Response<?> response) {
        return new Response<>(response.state,  response.message, null);
    }

    public static <T> Response<T> bi(boolean flag, T success, String fail) {
        if (flag) {
            return success(success);
        } else {
            return failed(fail);
        }
    }

    public boolean success() {
        return state == ResponseState.SUCCESS;
    }

    public boolean failed() {
        return state == ResponseState.FAILED;
    }

    public boolean error() {
        return state == ResponseState.ERROR;
    }
}
