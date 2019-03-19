package com.deploy.coremanager;

import com.deploy.Manager.AlertManager;
import com.deploy.Manager.ApiManager;
import com.deploy.Manager.Impl.AlertManagerImpl;
import com.deploy.Manager.DeviceManager;
import com.deploy.Manager.Impl.ApiManagerImpl;
import com.deploy.Manager.Impl.DeviceManagerImpl;
import com.deploy.Manager.Impl.InternetManagerImpl;
import com.deploy.Manager.Impl.UrlManagerImpl;
import com.deploy.Manager.Impl.ValidatioManagerImpl;
import com.deploy.Manager.InternetManager;
import com.deploy.Manager.UrlManager;
import com.deploy.Manager.ValidationManager;
import com.deploy.api.CenesCommonAPI;
import com.deploy.api.GatheringAPI;
import com.deploy.application.CenesApplication;
import com.deploy.backendManager.CenesCommonAPIManager;
import com.deploy.backendManager.GatheringApiManager;
import com.deploy.backendManager.HomeScreenApiManager;
import com.deploy.backendManager.LocationApiManager;
import com.deploy.backendManager.MeTimeApiManager;
import com.deploy.backendManager.NotificationAPiManager;
import com.deploy.backendManager.UserApiManager;
import com.deploy.database.impl.AlarmManagerImpl;
import com.deploy.database.impl.UserManagerImpl;
import com.deploy.database.manager.AlarmManager;
import com.deploy.database.manager.UserManager;

/**
 * Created by puneet on 11/8/17.
 */

public class CoreManager {
    CenesApplication cenesApplication = null;

    public UserApiManager userAppiManager = null;
    public AlarmManager alarmManager = null;
    public InternetManager internetManager = null;
    public ValidationManager validatioManager=null;
    public AlertManager alertManager = null;
    public UrlManager urlManager = null;
    public DeviceManager deviceManager = null;
    public ApiManager apiManager = null;
    public UserManager userManager = null;
    public HomeScreenApiManager homeScreenApiManager = null;
    public LocationApiManager locationApiManager = null;
    public GatheringApiManager gatheringApiManager = null;
    public NotificationAPiManager notificationAPiManager = null;
    public MeTimeApiManager meTimeApiManager = null;
    public CenesCommonAPIManager cenesCommonAPIManager = null;

    public CoreManager(CenesApplication cenesApplication){
        this.cenesApplication = cenesApplication;
        this.userManager = new UserManagerImpl(cenesApplication);
        this.alarmManager = new AlarmManagerImpl(cenesApplication);
        this.internetManager = new InternetManagerImpl(cenesApplication);
        this.validatioManager = new ValidatioManagerImpl(cenesApplication);
        this.alertManager = new AlertManagerImpl(cenesApplication);
        this.urlManager = new UrlManagerImpl(cenesApplication);
        this.deviceManager = new DeviceManagerImpl(cenesApplication);
        this.apiManager = new ApiManagerImpl(cenesApplication);
        this.userAppiManager = new UserApiManager(cenesApplication);
        this.homeScreenApiManager = new HomeScreenApiManager(cenesApplication);
        this.locationApiManager = new LocationApiManager(cenesApplication);
        this.gatheringApiManager = new GatheringApiManager(cenesApplication);
        this.notificationAPiManager = new NotificationAPiManager(cenesApplication);
        this.meTimeApiManager = new MeTimeApiManager(cenesApplication);
        this.cenesCommonAPIManager = new CenesCommonAPIManager(cenesApplication);
    }

    public UserManager getUserManager(){
        return this.userManager;
    }
    public AlarmManager getAlarmManager(){
        return this.alarmManager;
    }

    public InternetManager getInternetManager(){
        return this.internetManager;
    }

    public ValidationManager getValidatioManager(){
        return this.validatioManager;
    }

    public AlertManager getAlertManager(){
        return this.alertManager;
    }

    public UrlManager getUrlManager(){
        return this.urlManager;
    }

    public DeviceManager getDeviceManager(){
        return this.deviceManager;
    }

    public ApiManager getApiManager(){
        return this.apiManager;
    }

    public UserApiManager getUserAppiManager() {
        return this.userAppiManager;
    }

    public HomeScreenApiManager getHomeScreenApiManager() {
        return  this.homeScreenApiManager;
    }

    public LocationApiManager getLocationApiManager() {
        return this.locationApiManager;
    }

    public GatheringApiManager getGatheringApiManager() {
        return this.gatheringApiManager;
    }

    public NotificationAPiManager getNotificationAPiManager() {
        return this.notificationAPiManager;
    }

    public MeTimeApiManager getMeTimeApiManager() {
        return this.meTimeApiManager;
    }

    public CenesCommonAPIManager getCenesCommonAPIManager() {
        return this.cenesCommonAPIManager;
    }
}
