package com.lottery.types.model;

import com.lottery.types.enums.ResponseCode;
import com.lottery.types.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private String code;
    private String info;
    private T data;

    public static <T> Response<T> success() {
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .build();
    }

    public static <T> Response<T> success(T object) {
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(object)
                .build();
    }

    public static <T> Response<T> fail() {
        return Response.<T>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }

    public static <T> Response<T> fail(AppException ex) {
        return Response.<T>builder()
                .code(ex.getCode())
                .info(ex.getInfo())
                .build();
    }

    public static <T> Response<T> fail(T object) {
        return Response.<T>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(object)
                .build();
    }

}
