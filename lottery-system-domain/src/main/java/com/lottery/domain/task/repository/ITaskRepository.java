package com.lottery.domain.task.repository;

import com.lottery.domain.task.model.entity.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList(LocalDateTime updateTimeDeadline);

    void sendMessage(String queue,String message);

    void updateTaskSendMessageCompleted( String messageId);

    void updateTaskSendMessageFail(String messageId);

}
