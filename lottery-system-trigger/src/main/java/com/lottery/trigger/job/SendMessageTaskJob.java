package com.lottery.trigger.job;

import com.lottery.domain.task.model.entity.TaskEntity;
import com.lottery.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  发送MQ消息任务队列
 */
@Slf4j
@Component
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;
    @Resource
    private ThreadPoolExecutor executor;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList(LocalDateTime.now());
        if (taskEntities.isEmpty()) {
            return;
        }
        // 发送MQ消息
        for (TaskEntity taskEntity : taskEntities) {
            // 开启线程发送，提高发送效率。配置的线程池策略为 CallerRunsPolicy，在 ThreadPoolConfig 配置中有4个策略，面试中容易对比提问。可以检索下相关资料。
            executor.execute(() -> {
                try {
                    log.error("定时任务，发送MQ消息 queueName: {} ,message:{}", taskEntity.getQueue(), taskEntity.getMessage());
                    taskService.sendMessage(taskEntity.getQueue(), taskEntity.getMessage());
                    taskService.updateTaskSendMessageCompleted(taskEntity.getMessageId());
                } catch (Exception e) {
                    log.error("定时任务，发送MQ消息失败 queueName: {} ,message:{}",  taskEntity.getQueue(),taskEntity.getMessage(),e);
                    taskService.updateTaskSendMessageFail(taskEntity.getMessageId());
                }
            });
        }
    }
}
