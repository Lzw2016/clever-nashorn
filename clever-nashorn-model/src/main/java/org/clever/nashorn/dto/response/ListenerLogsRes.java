package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/28 15:15 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListenerLogsRes extends ConsoleLogRes {

    public ListenerLogsRes(String level, String log, List<Object> logs) {
        super(level, log, logs);
    }
}
