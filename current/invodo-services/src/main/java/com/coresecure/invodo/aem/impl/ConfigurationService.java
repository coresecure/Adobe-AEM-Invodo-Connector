package com.coresecure.invodo.aem.impl;

import com.coresecure.invodo.aem.ConfigurationUtil;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import org.apache.sling.commons.scheduler.Scheduler;

import java.io.Serializable;

@Component(	immediate=true,
        label="Invodo Service",
        description="Invodo Service Configuration",
        name="com.coresecure.invodo.aem.impl.ConfigurationService",
        metatype = true
)
@Service
@Properties({
        @Property(name="affiliate_id", label="Affiliate ID", description="Affiliate ID", value=""),
        @Property(name="api_key", label="Api Key", description="Api Key", value=""),
        @Property(name="storage_path", label="Storage Path", description="Storage Path", value="/etc/storage/invodo"),
        @Property(name="is_cron_active", label="CRON Enable", description="Enable CRON", boolValue=false),
        @Property(name="cron_scheduler", label="CRON Scheduler", description="Scheduler CRON", value="0 5 0 ? * SUN"),
        @Property(name="is_admin_allowed", label="Admin can refresh data", description="Allow AEM 'admin' user to refresh Invodo data", boolValue=false),
        @Property(name="refresher_groups", label="Refresher Groups", description="Groups that are allowed to refresh Invodo data", value={"",""})
})


public class ConfigurationService implements com.coresecure.invodo.aem.ConfigurationService {
    private ComponentContext componentContext;
    private static Logger loggerVar = LoggerFactory.getLogger(ConfigurationService.class);
    private Dictionary<String, Object> prop;
    private Dictionary<String, Object> getProperties() {
        if (prop == null)
            return new Hashtable<String, Object>();
        return prop;
    }
    @Reference
    private Scheduler scheduler;

    @Activate
    void activate(ComponentContext aComponentContext) {
        loggerVar.debug("START");
        this.componentContext=aComponentContext;
        this.prop = componentContext.getProperties();
        scheduleCron("invodo_indexer");
    }

    private void scheduleCron(final String jobName){
        try {
            this.scheduler.removeJob(jobName);
            loggerVar.debug(jobName + " was in the scheduler and it has been removed");
        } catch (NoSuchElementException nj){
            loggerVar.debug(jobName + " was not in the scheduler");
        }
        if (isCronActive()) {
            String schedulingExpression = getCRON();
            loggerVar.info(jobName + " is scheduled with the following CRON: " + schedulingExpression);
            Map<String, Serializable> config = new HashMap<String, Serializable>();
            boolean canRunConcurrently = false;
            final Runnable job1 = new Runnable() {
                public void run() {
                    loggerVar.debug("Runnign " + jobName);
                    ConfigurationUtil.cacheVideos();
                    loggerVar.debug(jobName + " stopped");
                }
            };
            try {
                this.scheduler.addJob(jobName, job1, config, schedulingExpression, canRunConcurrently);
            } catch (Exception e) {
                job1.run();
            }
        }
    }
    public String getAffiliateId() {
        // TODO Auto-generated method stub
        return (String) getProperties().get("affiliate_id");
    }

    public String getApiKey() {
        return (String) getProperties().get("api_key");
    }

    public String getStoragePath() {
        return (String) getProperties().get("storage_path");
    }

    public String getCRON() {
        return (String) getProperties().get("cron_scheduler");
    }

    public Boolean isCronActive() {
        return (Boolean) getProperties().get("is_cron_active");
    }

    public String[] getRefresherGroups() {
        Object p =  getProperties().get("refresher_groups");
        if( p == null) return new String[0];
        if( p instanceof String && ((String) p).trim().length()>0) {
            return new String[] { ((String) p).trim() };
        }

        if( p instanceof String[] ) {
            return cleanStringArray((String[]) p);
        }
        return new String[0];
    }

    public List<String> getRefresherGroupsList() {
        return Arrays.asList(getRefresherGroups());
    }

    public Boolean isGroupAllowed(String groupName){
        return getRefresherGroupsList().contains(groupName);
    }

    private String[] cleanStringArray(String[] input) {
        String[] result = input;
        List<String> list = new ArrayList<String>();

        for(String s : input) {
            if(s != null && s.trim().length() > 0) {
                list.add(s.trim());
            }
        }
        result = list.toArray(new String[list.size()]);
        return result;
    }

    public Boolean isAdminRefreshAllowed() {
        return (Boolean) getProperties().get("is_admin_allowed");
    }
}
