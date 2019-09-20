package com.xhw.springbootquartz.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.xhw.springbootquartz.entity.JobAndTrigger;
import com.xhw.springbootquartz.job.BaseJob;
import com.xhw.springbootquartz.service.IJobAndTriggerService;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;


@RestController
@RequestMapping(value="/job")
public class JobController 
{
	@Autowired
	private IJobAndTriggerService iJobAndTriggerService;

	//加入Qulifier注解，通过名称注入bean
	//Quartz定时任务核心的功能实现类
	@Autowired
	private Scheduler scheduler;


	private static Logger log = LoggerFactory.getLogger(JobController.class);  
	

	@PostMapping(value="/addjob")
	public void addjob(@RequestParam(value="jobClassName")String jobClassName,
					   @RequestParam(value="jobName")String jobName,
					   @RequestParam(value="jobDescribe")String jobDescribe,
			@RequestParam(value="jobGroupName")String jobGroupName, 
			@RequestParam(value="cronExpression")String cronExpression) throws Exception
	{
		// 启动调度器
		//scheduler.start();

		//构建job信息
		JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobName, jobGroupName).withDescription(jobDescribe).build();

		//表达式调度构建器(即任务执行的时间)
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = LocalDateTime.of(2019, 9, 20, 9, 25, 0).atZone(zoneId);
        Date date = Date.from(zonedDateTime.toInstant());

        //按新的cronExpression表达式构建一个新的trigger
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-"+jobName, jobGroupName)
				.withSchedule(scheduleBuilder).startAt(date).build();

		try {
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {
			System.out.println("创建定时任务失败"+e);
			throw new Exception("创建定时任务失败");
		}
	}
	

	//暂停
	@PostMapping(value="/pausejob")
	public void pausejob(@RequestParam(value="jobName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
	{
		scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
	}

	//恢复
	@PostMapping(value="/resumejob")
	public void resumejob(@RequestParam(value="jobName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
	{			
		scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
	}

	//更新
	@PostMapping(value="/reschedulejob")
	public void rescheduleJob(@RequestParam(value="jobName")String jobName,
			@RequestParam(value="jobGroupName")String jobGroupName,
			@RequestParam(value="cronExpression")String cronExpression) throws Exception
	{
		try {
			TriggerKey triggerKey = TriggerKey.triggerKey("trigger-"+jobName, jobGroupName);
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			System.out.println("更新定时任务失败"+e);
			throw new Exception("更新定时任务失败");
		}
	}
	

	//删除
	@PostMapping(value="/deletejob")
	public void deletejob(@RequestParam(value="jobName")String jobName, @RequestParam(value="jobGroupName")String jobGroupName) throws Exception
	{
		scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroupName));
		scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroupName));
		scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
	}
	

	@GetMapping(value="/queryjob")
	public Map<String, Object> queryjob(@RequestParam(value="pageNum")Integer pageNum, @RequestParam(value="pageSize")Integer pageSize) 
	{			
		PageInfo<JobAndTrigger> jobAndTrigger = iJobAndTriggerService.getJobAndTriggerDetails(pageNum, pageSize);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("JobAndTrigger", jobAndTrigger);
		map.put("number", jobAndTrigger.getTotal());
		return map;
	}
	
	public static BaseJob getClass(String classname) throws Exception
	{
		Class<?> class1 = Class.forName(classname);
		return (BaseJob)class1.newInstance();
	}
	
	
}
