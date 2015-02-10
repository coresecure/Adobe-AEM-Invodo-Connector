package com.coresecure.invodo.aem;

import java.util.List;

public interface ConfigurationService {
    public String getAffiliateId();
    public String getApiKey();
    public String getStoragePath();
    public String getCRON();
    public Boolean isCronActive();
    public Boolean isAdminRefreshAllowed();
    public String[] getRefresherGroups();
    public List<String> getRefresherGroupsList();
    public Boolean isGroupAllowed(String groupName);
}
