package com.atviettelsolutions.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kpi_log")
public class Log {

    @Id
    private String id;
    private String applicationCode;
    private String serviceCode;
    private String sessionId;
    private String ipPortParentNode;
    private String ipPortCurrentNode;
    @Column(length = 3000)
    private String requestContent;
    @Column(length = 3000)
    private String responseContent;
    private String startTime;
    private String endTime;
    private String duration;
    private String errorCode;
    @Column(length = 3000)
    private String errorDescription;
    private Integer transactionStatus;
    private String actionName;
    private String username;
    private String account;
}
