package org.clever.nashorn.dto.response;

import lombok.Data;
import org.clever.common.model.response.BaseResponse;

@Data
public class CodeRunLogStatusSummaryRes extends BaseResponse {

    private Integer status;

    private Integer count;
}
