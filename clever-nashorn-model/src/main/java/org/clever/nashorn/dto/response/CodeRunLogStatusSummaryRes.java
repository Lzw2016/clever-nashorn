package org.clever.nashorn.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.clever.common.model.response.BaseResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeRunLogStatusSummaryRes extends BaseResponse {

    private Integer status;

    private Integer count;
}
