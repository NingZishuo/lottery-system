package com.lottery.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务表，发送MQ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 消息主题:队列叫什么名字
     */
    private String queue;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息主体
     */
    private String message;
    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private String state;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
