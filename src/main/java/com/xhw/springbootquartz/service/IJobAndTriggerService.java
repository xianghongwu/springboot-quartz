package com.xhw.springbootquartz.service;


import com.github.pagehelper.PageInfo;
import com.xhw.springbootquartz.entity.JobAndTrigger;

public interface IJobAndTriggerService {
	public PageInfo<JobAndTrigger> getJobAndTriggerDetails(int pageNum, int pageSize);
}
