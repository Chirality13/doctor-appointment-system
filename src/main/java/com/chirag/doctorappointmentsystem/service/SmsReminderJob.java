package com.chirag.doctorappointmentsystem.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsReminderJob implements Job {

    @Autowired
    private SmsService smsService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String mobile = context.getMergedJobDataMap().getString("mobile");
        String message = context.getMergedJobDataMap().getString("message");
        smsService.sendSms(mobile, message);
    }
}
