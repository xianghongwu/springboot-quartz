package com.xhw.springbootquartz.dao;

import java.util.List;

import com.xhw.springbootquartz.entity.JobAndTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface JobAndTriggerMapper {
	@Select("select \n" +
			"\t\t\tQRTZ_JOB_DETAILS.JOB_NAME,\n" +
			"\t\t\tQRTZ_JOB_DETAILS.JOB_GROUP,\n" +
			"\t\t\tQRTZ_JOB_DETAILS.JOB_CLASS_NAME,\n" +
			"\t\t\tQRTZ_TRIGGERS.TRIGGER_NAME,\n" +
			"\t\t\tQRTZ_TRIGGERS.TRIGGER_GROUP,\n" +
			"\t\t\tQRTZ_CRON_TRIGGERS.CRON_EXPRESSION,\n" +
			"\t\t\tQRTZ_CRON_TRIGGERS.TIME_ZONE_ID,\n" +
			"\t\t\tQRTZ_TRIGGERS.TRIGGER_STATE,\n" +
			"\t\t\tQRTZ_JOB_DETAILS.DESCRIPTION\n" +
			"\t\t\tfrom   QRTZ_JOB_DETAILS\n" +
			"\t\t\tleft join QRTZ_TRIGGERS  on QRTZ_JOB_DETAILS.JOB_NAME=QRTZ_TRIGGERS.JOB_NAME and QRTZ_JOB_DETAILS.JOB_GROUP=QRTZ_TRIGGERS.JOB_GROUP\n" +
			"\t\t\tleft join QRTZ_CRON_TRIGGERS on QRTZ_CRON_TRIGGERS.TRIGGER_NAME=QRTZ_TRIGGERS.TRIGGER_NAME and QRTZ_CRON_TRIGGERS.TRIGGER_GROUP=QRTZ_TRIGGERS.TRIGGER_GROUP")
	List<JobAndTrigger> getJobAndTriggerDetails();
}
