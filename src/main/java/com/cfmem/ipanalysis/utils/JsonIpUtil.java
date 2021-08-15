package com.cfmem.ipanalysis.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cfmem.ipanalysis.dto.JsonIp;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class JsonIpUtil {

    private final String URL = "https://jsonip.com/";

    public Optional<String> getPublicIp () {
        HttpResponse response = HttpUtil.createGet(URL).execute();
        if (response.getStatus() == HttpStatus.HTTP_OK) {
            String json = response.body();
            if (StrUtil.isNotBlank(json)) {
                JsonIp jsonIp = JSONUtil.toBean(json, JsonIp.class);
                if (null != jsonIp) {
                    return Optional.of(jsonIp.getIp());
                }
            }
        }
        return Optional.empty();
    }

}
