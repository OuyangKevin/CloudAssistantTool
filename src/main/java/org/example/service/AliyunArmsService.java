package org.example.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.arms20190808.models.GetTraceAppResponse;
import com.aliyun.arms20190808.models.GetTraceAppResponseBody;
import com.aliyun.arms20190808.models.ListTraceAppsResponse;
import com.aliyun.arms20190808.models.ListTraceAppsResponseBody;

import java.util.List;

/**
 * aliyun cloud product arms  service
 */
public class AliyunArmsService {

    /**
     * init aliyun sae client,need aliyun ak & sk
     */
    private com.aliyun.arms20190808.Client createClient(String regionId){
        try {
            com.aliyun.teaopenapi.models.Config config
                    = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                    .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
            // endpoint refer  https://api.aliyun.com/product/ARMS
            config.endpoint = String.format("arms.%s.aliyuncs.com",regionId);
            return new com.aliyun.arms20190808.Client(config);
        } catch (Exception e) {
            System.err.println("init arms aliyun client error:"+e);
        }
        return null;
    }

    /**
     * get aliyun arms trace app
     */
    public List<ListTraceAppsResponseBody.ListTraceAppsResponseBodyTraceApps> listTraceApps(String regionId) throws Exception{
        com.aliyun.arms20190808.Client client = this.createClient(regionId);
        com.aliyun.arms20190808.models.ListTraceAppsRequest listTraceAppsRequest = new com.aliyun.arms20190808.models.ListTraceAppsRequest()
                .setRegionId(regionId);
        try {
            ListTraceAppsResponse listTraceAppsResponse = client.listTraceApps(listTraceAppsRequest);
            if(listTraceAppsResponse != null && listTraceAppsResponse.getBody() != null && listTraceAppsResponse.getBody().getTraceApps() != null){
                List<ListTraceAppsResponseBody.ListTraceAppsResponseBodyTraceApps> traceApps = listTraceAppsResponse.getBody().getTraceApps();
                for(ListTraceAppsResponseBody.ListTraceAppsResponseBodyTraceApps traceApp:traceApps){
                    System.out.println(JSON.toJSONString(traceApp));
                }
                return null;
            }else{
                System.out.println("get aliyun arms trace app list with result empty!");
                return null;
            }

        } catch (Throwable e) {
            System.out.println("get aliyun arms trace app list error:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get aliyun arms trace app
     */
    public GetTraceAppResponseBody.GetTraceAppResponseBodyTraceApp getTraceApp(String regionId, String pid){
        com.aliyun.arms20190808.Client client = this.createClient(regionId);
        com.aliyun.arms20190808.models.GetTraceAppRequest getTraceAppRequest = new com.aliyun.arms20190808.models.GetTraceAppRequest();
        getTraceAppRequest.setRegionId(regionId);
        getTraceAppRequest.setPid(pid);
        try {
            GetTraceAppResponse getTraceAppResponse = client.getTraceApp(getTraceAppRequest);
            if(getTraceAppResponse != null && getTraceAppResponse.getBody() != null && getTraceAppResponse.getBody().getTraceApp() != null){
                return getTraceAppResponse.getBody().getTraceApp();
            }else{
                System.out.println("get aliyun arms trace app  with result empty!");
                return null;
            }

        } catch (Throwable e) {
            System.out.println("get aliyun arms trace app  error:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        AliyunArmsService aliyunArmsService = new AliyunArmsService();
        aliyunArmsService.listTraceApps("cn-hangzhou");
    }
}


