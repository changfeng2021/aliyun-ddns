package com.cfmem.ipanalysis.schedule;

import com.cfmem.ipanalysis.ddns.AliYunDDNS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpRefreshScheduled {

    private final AliYunDDNS ddns;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshIpAddress () {
        log.info("refresh server ip address beginning");
        ddns.refresh();
        log.info("refresh server ip address of end");
    }
}
