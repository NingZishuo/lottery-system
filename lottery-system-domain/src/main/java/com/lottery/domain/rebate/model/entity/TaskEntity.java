package com.lottery.domain.rebate.model.entity;

import com.lottery.domain.rebate.event.SendRebateMessageEvent;
import com.lottery.domain.rebate.model.valobj.TaskStateVO;
import com.lottery.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户返利发放sku和积分任务实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    /**
     * 消息主题
     */
    private String queue;
    /**
     * 消息编号
     */
    private String messageId;
    /**
     * 消息主体
     */
    private BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> message;
    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private TaskStateVO state;

}
