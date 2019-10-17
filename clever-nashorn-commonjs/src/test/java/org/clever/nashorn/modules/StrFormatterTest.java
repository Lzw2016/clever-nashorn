package org.clever.nashorn.modules;

import lombok.extern.slf4j.Slf4j;
import org.clever.nashorn.utils.StrFormatter;
import org.junit.Test;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/10/17 14:19 <br/>
 */
@Slf4j
public class StrFormatterTest {

    @Test
    public void t1() {
        log.info("---> {}", StrFormatter.underlineToCamel("audit_status"));
        log.info("---> {}", StrFormatter.underlineToCamel("send_each_user"));
        log.info("---> {}", StrFormatter.underlineToCamel("audit__status"));
        log.info("---> {}", StrFormatter.underlineToCamel("_audit_status"));
        log.info("---> {}", StrFormatter.underlineToCamel("__audit_status"));
    }
}
