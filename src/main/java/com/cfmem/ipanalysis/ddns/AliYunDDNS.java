package com.cfmem.ipanalysis.ddns;

import cn.hutool.core.collection.CollUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.cfmem.ipanalysis.config.DomainConfig;
import com.cfmem.ipanalysis.utils.JsonIpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliYunDDNS {

    private final DomainConfig domainConfig;
    private final static String DefaultRegionId = "cn-beijing";

    /**
     * 获取主域名的所有解析记录列表
     */
    private DescribeDomainRecordsResponse describeDomainRecords(DescribeDomainRecordsRequest request, IAcsClient client){
        try {
            // 调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            // 发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    /**
     * 修改解析记录
     */
    private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client){
        try {
            // 调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            // 发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    public void refresh () {
        Map<String, List<String>> domainMap = domainConfig.getDomain();
        if (CollUtil.isNotEmpty(domainMap)) {
            domainMap.forEach(this::updateApi);
        }
    }

    private void updateApi (String domain, List<String> prefix) {
        if (CollUtil.isNotEmpty(prefix)) {
            prefix.forEach(p -> this.updateApi(domain, p));
        }
    }

    public void updateApi(String domain, String domainPrefix) {

        log.info("更新 domain: {}, prefix: {}", domain, domainPrefix);

        // 设置鉴权参数，初始化客户端
        DefaultProfile profile = DefaultProfile.getProfile(
                DefaultRegionId,// 地域ID
                domainConfig.getAccessKeyId(),// 您的AccessKey ID
                domainConfig.getSecret());// 您的AccessKey Secret
        IAcsClient client = new DefaultAcsClient(profile);

        // 查询指定二级域名的最新解析记录
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        // 主域名
        describeDomainRecordsRequest.setDomainName(domain);
        // 主机记录
        describeDomainRecordsRequest.setRRKeyWord(domainPrefix);
        // 解析记录类型
        describeDomainRecordsRequest.setType("A");
        DescribeDomainRecordsResponse describeDomainRecordsResponse = describeDomainRecords(describeDomainRecordsRequest, client);
        List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();

        if (CollUtil.isNotEmpty(domainRecords)) {
            DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
            // 记录ID
            String recordId = record.getRecordId();
            // 记录值
            String recordsValue = record.getValue();

            Optional<String> ipOptional = JsonIpUtil.getPublicIp();
            if (ipOptional.isPresent()) {
                String ip = ipOptional.get();
                log.info("域名为：{}， 主机记录为：{}， 公网 ip 为：{}， 原记录 ip 为：{}", domain, domainPrefix, ip, recordsValue);
                if (!ip.equals(recordsValue)) {
                    // 修改解析记录
                    UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                    // 主机记录
                    updateDomainRecordRequest.setRR(domainPrefix);
                    // 记录ID
                    updateDomainRecordRequest.setRecordId(recordId);
                    // 将主机记录值改为当前主机IP
                    updateDomainRecordRequest.setValue(ip);
                    // 解析记录类型
                    updateDomainRecordRequest.setType("A");
                    UpdateDomainRecordResponse updateDomainRecordResponse = updateDomainRecord(updateDomainRecordRequest, client);
                    log.info("更新 ddns 完成！{}", updateDomainRecordResponse);
                } else {
                    log.info("记录相等不需要修改！");
                }
            }
        }
    }
}
