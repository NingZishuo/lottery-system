package com.lottery.infrastructure.persistent.repository;

import com.lottery.domain.task.model.entity.TaskEntity;
import com.lottery.domain.task.repository.ITaskRepository;
import com.lottery.infrastructure.event.EventPublisher;
import com.lottery.infrastructure.persistent.dao.ITaskDao;
import com.lottery.infrastructure.persistent.po.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TaskRepository implements ITaskRepository {

    @Autowired
    private ITaskDao taskDao;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList(LocalDateTime updateTimeDeadline) {
        List<Task> taskList = taskDao.queryNoSendMessageTaskList(updateTimeDeadline);
        return taskList.stream().map(task -> {
            TaskEntity taskEntity = new TaskEntity();
            BeanUtils.copyProperties(task, taskEntity);
            return taskEntity;
        }).collect(Collectors.toList());
    }

    @Override
    public void sendMessage(String queue,String message) {
        eventPublisher.publish(queue,message);
    }

    @Override
    public void updateTaskSendMessageCompleted(String messageId) {
        taskDao.updateTaskSendMessageCompleted(Task.builder()
                .messageId(messageId)
                .build());
    }

    @Override
    public void updateTaskSendMessageFail(String messageId) {
        taskDao.updateTaskSendMessageFail(Task.builder()
                .messageId(messageId)
                .build());
    }

}
