package com.caovy2001.data_everywhere.command.user;

import com.caovy2001.data_everywhere.entity.UserEntity;
import com.caovy2001.data_everywhere.model.ResponseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandLoginFromThirdPartyResponse {
    private String secretKey;
    private UserEntity user;
    private ResponseModel.Status status;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        @Builder.Default
        private HttpStatus httpStatus = HttpStatus.OK;
        private String exceptionCode;
    }
}
