package com.lottery.infrastructure.persistent.dao;

import com.lottery.infrastructure.persistent.po.Task;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务表，发送MQ
 */
@Mapper
public interface ITaskDao {
    /**
     * 综合插入
     * @param task
     */
    void insert(Task task);

    /**
     * 修改state为成功
     * @param task
     */
    void updateTaskSendMessageCompleted(Task task);

    /**
     * 修改state为失败
     * @param task
     */
    void updateTaskSendMessageFail(Task task);

    /**
     * 查询所有fail消息 和 create但是超过更新时间2分钟的消息
     * @return
     */
    List<Task> queryNoSendMessageTaskList(LocalDateTime deadline);

}
