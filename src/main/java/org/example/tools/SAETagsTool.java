package org.example.tools;

import com.aliyun.arms20190808.models.GetTraceAppResponseBody;
import com.aliyun.sae20190506.models.ListApplicationsResponseBody;
import com.google.common.base.Strings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.service.AliyunArmsService;
import org.example.service.AliyunSaeService;
import org.example.service.AliyunTagService;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

public class SAETagsTool {

    private static final String DEFAULT = "default";
    private AliyunSaeService aliyunSaeService = new AliyunSaeService();

    private AliyunTagService aliyunTagService = new AliyunTagService();

    private static AliyunArmsService aliyunArmsService = new AliyunArmsService();

    public static void main(String[] args) throws Exception {
        SAETagsTool saeTagsTool = new SAETagsTool();
        String csvFile = "/***/***.csv";
        saeTagsTool.AddTagsToSae(csvFile,true,"chagee_env","chagee_business","chagee_project");
    }

    public void AddTagsToSae(String csvFile,boolean isForceToArms,String ...tagKeys){
        String userId = System.getenv("ALIBABA_CLOUD_USERID");
        if(userId == null || userId.isEmpty()){
            System.err.println("[SAETagsTool] skip total due to ALIBABA_CLOUD_USERID not set!");
            return;
        }
        System.out.println("[SAETagsTool] start!");
        try {
            Reader reader = new FileReader(csvFile, Charset.forName("GBK"));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim(true).withIgnoreSurroundingSpaces(true));
                for (CSVRecord csvRecord : csvParser) {
                    if(!checkCsvFormat(csvRecord,tagKeys)){
                        System.err.println("[SAETagsTool] skip csvRecord due to format error!");
                    }else{
                        String appId = getSaeAppId(csvRecord);
                        if (appId == null || appId.isEmpty()) {
                            System.err.println("[SAETagsTool] skip csvRecord due to can not find appId in sae console with aliyunRegionId is:"+csvRecord.get("aliyun_regionId") + " and app is:" + csvRecord.get("aliyun_app") + " and namespaceId is:" + csvRecord.get("aliyun_namespaceId"));
                            continue;
                        }
                        System.out.println("[SAETagsTool]  csvRecord  find appId in sae console with aliyunRegionId is:"+csvRecord.get("aliyun_regionId") + " and app is:" + csvRecord.get("aliyun_app") + " and namespaceId is:" + csvRecord.get("aliyun_namespaceId") + " and appId is:" + appId);
                        String aliyunRegionId = csvRecord.get("aliyun_regionId");
                        for(String tagKey:tagKeys){
                            aliyunTagService.tagResourcesForSaeApp(aliyunRegionId,userId,appId,tagKey,csvRecord.get(tagKey));
                        }
                        if(isForceToArms){
                            GetTraceAppResponseBody.GetTraceAppResponseBodyTraceApp traceAppResponseBodyTraceApp = aliyunArmsService.getTraceApp(aliyunRegionId,appId);
                            if(traceAppResponseBodyTraceApp!= null){
                                for(String tagKey:tagKeys){
                                    aliyunTagService.tagResourcesForArmsApm(aliyunRegionId,userId,appId,tagKey,csvRecord.get(tagKey));
                                }
                            }
                        }
                        System.out.println("[SAETagsTool]  csvRecord  tag success with app is:" + csvRecord.get("aliyun_app"));
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[SAETagsTool] complete!");
    }

    /**
     * 检查CSV的格式是否符合要求
     */
    private boolean  checkCsvFormat(CSVRecord csvRecord,String ...tagKeys){
        String aliyunApp = csvRecord.get("aliyun_app");
        String aliyunRegionId = csvRecord.get("aliyun_regionId");
        String aliyunNamespaceId = csvRecord.get("aliyun_namespaceId");
        if(Strings.isNullOrEmpty(aliyunApp) || Strings.isNullOrEmpty(aliyunRegionId)  || Strings.isNullOrEmpty(aliyunNamespaceId) ){
            return false;
        }
        if(!(aliyunNamespaceId.startsWith(aliyunRegionId+":") || DEFAULT.equals(aliyunNamespaceId))){
            return false;
        }
        if(tagKeys == null || tagKeys.length == 0){
            return false;
        }
        for(String tagKey:tagKeys){
            if(Strings.isNullOrEmpty(csvRecord.get(tagKey))){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取SAE应用的pid
     */
    private String getSaeAppId(CSVRecord csvRecord){
        String aliyunApp = csvRecord.get("aliyun_app");
        String aliyunRegionId = csvRecord.get("aliyun_regionId");
        String aliyunNamespaceId = csvRecord.get("aliyun_namespaceId");

        List<ListApplicationsResponseBody.ListApplicationsResponseBodyDataApplications> apps =
                aliyunSaeService.listApplications(aliyunRegionId, aliyunApp, DEFAULT.equals(aliyunNamespaceId) ? null : aliyunNamespaceId);
        String appId = ""; // 更具描述性的变量名

        for (ListApplicationsResponseBody.ListApplicationsResponseBodyDataApplications app : apps) {
            if (DEFAULT.equals(aliyunNamespaceId)) {
                if (Strings.isNullOrEmpty(app.getNamespaceId()) && aliyunApp.equalsIgnoreCase(app.getAppName())) {
                    appId = app.getAppId();
                    break;
                }
            } else if (aliyunNamespaceId.equalsIgnoreCase(app.getNamespaceId()) && aliyunApp.equalsIgnoreCase(app.getAppName())) {
                appId = app.getAppId();
                break;
            }
        }
        return appId;
    }
}
