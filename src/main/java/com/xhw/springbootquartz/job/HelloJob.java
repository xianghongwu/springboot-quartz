package com.xhw.springbootquartz.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.xhw.springbootquartz.controller.JobController;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;   
import org.quartz.JobExecutionContext;  
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

public class HelloJob implements BaseJob {  
  
    private static Logger _log = LoggerFactory.getLogger(HelloJob.class);  
     
    public HelloJob() {  
          
    }

    @Autowired
    JobController jobController;

    @Autowired
    DataSource dataSource;
    public void execute(JobExecutionContext context)  
        throws JobExecutionException {  
        _log.error("Hello Job执行时间: " + new Date());
        try {
            //详见https://www.cnblogs.com/youzhibing/p/10056696.html
            Connection connection = DBConnectionManager.getInstance().getConnection("springTxDataSource.clusteredScheduler");
            System.out.println(connection.getClass()+"--------------------------------");
            System.out.println(jobController+"----------------------");
            System.out.println(dataSource.getClass()+"----------------------");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }  
}  
