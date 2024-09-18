Cloud Assistant Tool,please use JDK 11+

# 1. 对SAE应用进行批量打标
## 1.1 自主维护SAE应用的标签csv文件
csv的格式严格与下面的设计保持一致

| aliyun_app_name | aliyun_regionId | aliyun_namespaceId | chagee_env | chagee_business | chagee_project |
|-----------------|-----------------|--------------------|------------|-----------------|----------------|
| sae-acos        | cn-hangzhou     | 默认                 | test       | B端              | 大促             |
| sae-acos-basic  | cn-hangzhou     | cn-hangzhou:acos               | test       | B端              | 大促             |
| sae-acos-basic  | cn-hangzhou     | cn-hangzhou:acos   | uat        | C端              | 大促             |

（1）必须填写列为“应用名称”&“地区”&“命名空间”，

| 字段      | 必填 | 说明                                                                  | 
|---------|----|---------------------------------------------------------------------|
| aliyun_app_name     | Y  | 按照应用在SAE上的实际名称填写即可                                                  |
| aliyun_regionId        | Y  | 必须填写合法的regionId字段，比如cn-hangzhou                                     | 
| aliyun_namespaceId | Y  | 如果是默认的命名空间，请填写“default”，否则填写对应的命名空间ID, 命名空间ID以regionId开始，如cn-hangzhou:acos | 

（2）其他列为标签Key，用户可以自主配置，程序中需要强制置顶标签列Key才会自动设置tag.

## 1.2 设置环境变量
按照个人习惯（~/.bash_profile, ~/.bash_profile等等）
```
export ALIBABA_CLOUD_USERID='******'
export ALIBABA_CLOUD_ACCESS_KEY_ID='******'
export ALIBABA_CLOUD_ACCESS_KEY_SECRET='******'
```

## 1.3 运行程序
run SAETagsTool main
```
SAETagsTool saeTagsTool = new SAETagsTool();
String csvFile = "/***/***/***.csv";
saeTagsTool.AddTagsToSae(csvFile,false,"chagee_env","chagee_business","chagee_project");
```

## 1.4 进阶-将SAE应用的标同步至ARMS
```
SAETagsTool saeTagsTool = new SAETagsTool();
String csvFile = "/***/***/***.csv";
saeTagsTool.AddTagsToSae(csvFile,true,"chagee_env","chagee_business","chagee_project");
```