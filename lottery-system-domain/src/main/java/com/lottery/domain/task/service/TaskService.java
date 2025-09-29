package com.lottery.domain.task.service;

import com.lottery.domain.task.model.entity.TaskEntity;
import com.lottery.domain.task.repository.ITaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService implements ITaskService {

    @Autowired
    private ITaskRepository taskRepository;

    /**
     * 查询没有发送成功的Task
     *
     * @return
     */
    @Override
    public List<TaskEntity> queryNoSendMessageTaskList(LocalDateTime updateTimeDeadline) {
        return taskRepository.queryNoSendMessageTaskList(updateTimeDeadline);
    }

    @Override
    public void sendMessage(String queue,String message) {
        taskRepository.sendMessage(queue,message);
    }

    @Override
    public void updateTaskSendMessageCompleted(String messageId) {
        taskRepository.updateTaskSendMessageCompleted(messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String messageId) {
        taskRepository.updateTaskSendMessageFail(messageId);
    }


}
