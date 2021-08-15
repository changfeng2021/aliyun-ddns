package com.cfmem.ipanalysis.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JsonIp {

    private String ip;
    private String geoIp;
    private String help;
}
