package org.example.service;

import com.alibaba.fastjson.JSON;
import com.aliyun.tag20180828.models.TagResourcesResponse;

import java.util.ArrayList;
import java.util.List;
/**
 * aliyun cloud product tag  service
 */
public class AliyunTagService {

    /**
     * init aliyun tag client,need aliyun ak & sk
     */
    private com.aliyun.tag20180828.Client createClient(String regionId) {
        try {
            com.aliyun.teaopenapi.models.Config config
                    = new com.aliyun.teaopenapi.models.Config()
                    .setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                    .setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
            config.endpoint = String.format("tag.%s.aliyuncs.com",regionId);// Endpoint 请参考 https://api.aliyun.com/product/Tag
            return new com.aliyun.tag20180828.Client(config);
        } catch (Exception e) {
            System.err.println("init tag aliyun client error:"+e);
        }
        return null;
    }

    /**
     * add tag 原子方法
     */
    private void tagResources(String ownerId, String regionId, String tags, List<String> resourceARN) {
        com.aliyun.tag20180828.Client client = this.createClient(regionId);
        if(client ==  null){
            System.err.println("[tag] tag resource fail with ownerId is:"+ownerId + " and regionId is:"+regionId + " and tags is:"+tags + " and resource is:"+ JSON.toJSONString(resourceARN));
        }
        com.aliyun.tag20180828.models.TagResourcesRequest tagResourcesRequest =
                new com.aliyun.tag20180828.models.TagResourcesRequest()
                        .setRegionId(regionId);
        tagResourcesRequest.setTags(tags);
        tagResourcesRequest.setRegionId(regionId);
        tagResourcesRequest.setOwnerId(Long.parseLong(ownerId));
        tagResourcesRequest.setResourceOwnerAccount(ownerId);
        tagResourcesRequest.setResourceARN(resourceARN);
        try {
            // 复制代码运行请自行打印 API 的返回值
            TagResourcesResponse tagResourcesResponse = client.tagResources(tagResourcesRequest);
            System.out.println("[tag] tag resource success with ownerId is:"+ownerId + " and regionId is:"+regionId + " and tags is:"+tags + " and resource is:"+JSON.toJSONString(resourceARN));
        } catch (Throwable e) {
            System.err.println("[tag] tag resource fail with ownerId is:"+ownerId + " and regionId is:"+regionId + " and tags is:"+tags + " and resource is:"+JSON.toJSONString(resourceARN));
            System.err.println("aliyun tag resource error:"+e.getMessage());
        }
    }

    /**
     * 为ARMS应用监控打标签,该方法一次只支持打一个标,可以自主扩展
     * 资源格式：arn:acs:arms:{regionId}:{userId}:application/{pid}
     */
    public void tagResourcesForArmsApm(String regionId,String userId,String pid,String tagKey,String tagValue){
        String tags = "{\""+tagKey+"\":\""+tagValue+"\"}";
        String resourceARN = "arn:acs:arms:"+regionId+":"+userId+":application/"+pid;
        List<String> resourceARNs = new ArrayList<>();
        resourceARNs.add(resourceARN);
        tagResources(userId, regionId, tags, resourceARNs);
    }

    /**
     * 为SAE应用打标签,该方法一次只支持打一个标,可以自主扩展
     * 资源格式：arn:acs:sae:{regionId}:{userId}:application/{pid}"
     */
    public void tagResourcesForSaeApp(String regionId,String userId,String pid,String tagKey,String tagValue){
        String tags = "{\""+tagKey+"\":\""+tagValue+"\"}";
        String resourceARN = "arn:acs:sae:"+regionId+":"+userId+":application/"+pid;
        List<String> resourceARNs = new ArrayList<>();
        resourceARNs.add(resourceARN);
        tagResources(userId, regionId, tags, resourceARNs);
    }

    public static void main(String[] args) throws Exception {
        AliyunTagService aliyunTagService = new AliyunTagService();
        aliyunTagService.tagResourcesForArmsApm("cn-hangzhou","***","***","dev","test");
    }
}
