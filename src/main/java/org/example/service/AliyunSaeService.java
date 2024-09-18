package org.example.service;

import com.aliyun.sae20190506.models.ListApplicationsResponse;
import com.aliyun.sae20190506.models.ListApplicationsResponseBody;

import java.util.List;

/**
 * aliyun cloud product sae  service
 */
public class AliyunSaeService {

    /**
     * init aliyun sae client,need aliyun ak & sk
     */
    private com.aliyun.sae20190506.Client createClient(String regionId) {
        try {
            com.aliyun.teaopenapi.models.Config config
                    = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                    .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
            // endpoint refer  https://api.aliyun.com/product/ARMS
            config.endpoint = String.format("sae.%s.aliyuncs.com",regionId);
            return new com.aliyun.sae20190506.Client(config);
        } catch (Exception e) {
            System.err.println("init sae aliyun client error:"+e);
        }
        return null;
    }

    /**
     * get aliyun sae app list
     */
    public List<ListApplicationsResponseBody.ListApplicationsResponseBodyDataApplications> listApplications(String regionId){
        return listApplications(regionId,null,null);
    }

    /**
     * get aliyun sae app list
     */
    public List<ListApplicationsResponseBody.ListApplicationsResponseBodyDataApplications> listApplications(String regionId,String appName,String namespaceId){
        com.aliyun.sae20190506.Client client = this.createClient(regionId);
        if(client ==  null){
            System.err.println("[sae] listApplications fail with regionId is:" + regionId);
        }
        com.aliyun.sae20190506.models.ListApplicationsRequest listApplicationsRequest = new com.aliyun.sae20190506.models.ListApplicationsRequest();
        if(appName != null && appName.length()!=0){
            listApplicationsRequest.setAppName(appName);
        }
        if(namespaceId != null && namespaceId.length()!=0){
            listApplicationsRequest.setNamespaceId(namespaceId);
        }
        listApplicationsRequest.setPageSize(10000);//强制设置为10000,这里其实不是非常严谨
        try {
            ListApplicationsResponse listApplicationsResponse = client.listApplications(listApplicationsRequest);
            if(listApplicationsResponse != null && listApplicationsResponse.getBody()!=null && listApplicationsResponse.getBody().data!=null){
                List<ListApplicationsResponseBody.ListApplicationsResponseBodyDataApplications>  apps = listApplicationsResponse.getBody().
                        data.getApplications();
                return apps;
            }
        } catch (Throwable e) {
            System.out.println("get aliyun sae  app list error:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        AliyunSaeService saeEntry = new AliyunSaeService();
        saeEntry.listApplications("cn-hangzhou");
    }
}


