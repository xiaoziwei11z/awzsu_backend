package com.awzsu.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.awzsu.sms.utils.SmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "sms")
@Slf4j
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;
    @Value("${aliyun.sms.sign_name}")
    private String sign_name;
    @Value("${aliyun.sms.template_code}")
    private String template_code;

    @RabbitHandler
    public void sendSms(Map<String,String> message){
        String phone = message.get("phone");
        String smscode = message.get("smscode");

        try {
            smsUtil.sendSms(phone,template_code,sign_name,"{\"code\":\""+smscode+"\"}");
        } catch (ClientException e) {
            log.error("Exception:",e);
        }
    }
}
