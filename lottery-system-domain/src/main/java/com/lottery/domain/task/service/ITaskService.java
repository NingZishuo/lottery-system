package com.lottery.domain.task.service;


import com.lottery.domain.task.model.entity.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息任务服务接口
 */
public interface ITaskService {

    //注意 domain往外抛的对象只能是Entity 不能是底层的task数据交互对象

    /**
     * 查询没有发送成功的Task
     * @return
     */
    List<TaskEntity> queryNoSendMessageTaskList(LocalDateTime updateTimeDeadline);

    /**
     * 发送消息
     */
    void sendMessage(String queue,String message);

    /**
     * 发送成功
     * @param messageId
     */
    void updateTaskSendMessageCompleted(String messageId);

    /**
     * 发送失败
     * @param messageId
     */
    void updateTaskSendMessageFail(String messageId);


}
