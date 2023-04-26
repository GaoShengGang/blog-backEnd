package com.gsg.blog.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gsg.blog.config.RabbitMQConfig;
import com.gsg.blog.dto.ChatRequestDTO;
import com.gsg.blog.model.ChatMsg;
import com.gsg.blog.ex.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shuaigang
 * @since JDK 11.0.10
 * ClassName: CsChatUtils
 * date: 2022/8/31 16:44
 */
@Slf4j
@Component
public class ChatUtils {

    String queuePrefix = "mqtt-subscription-";
    String queueSuffix = "qos1";

    /**
     * TODO 发送消息
     * @author shuaigang
     * @date  2022/8/31 13:C1
     */
    public void publishMsg(ChatMsg chatMsg) {
        // 向MQ中发送信息
        String msgContentEncode = BaseUtil.encode(chatMsg);

        ChatRequestDTO chatRequestDto = new ChatRequestDTO();
        chatRequestDto.setData(msgContentEncode);

        String chatRequestJsonStr;
        try {
            chatRequestJsonStr = JacksonUtils.obj2json(chatRequestDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw ServiceException.errorParams("result.internalError");
        }

        assert chatRequestJsonStr != null;
        RabbitMQConfig.rabbitMqChatClient.publish(queuePrefix + chatMsg.getUserId() + queueSuffix, chatRequestJsonStr);
    }

    /**
     * TODO 接收消息
     * @author shuaigang
     * @date  2022/8/31 13:34
     */
    public List<Object> getMsg(String queueName) {
        return RabbitMQConfig.rabbitMqChatClient.getMsg(queuePrefix + queueName + queueSuffix);
    }

}
