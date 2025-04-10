package com.atviettelsolutions.config;

import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.services.KpiLogService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class LoggingDispatcherServlet extends DispatcherServlet {
    private final KpiLogService kpiLogService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Log logger = LogFactory.getLog(getClass());
    private final ApplicationInfo applicationInfo;
    private final Gson gson;
    private final KpiLogInfo kpiLogInfo;
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};
    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (kpiLogInfo.getIgnoreRestRoutes() != null && !kpiLogInfo.getIgnoreRestRoutes().isEmpty() && kpiLogInfo.getIgnoreRestRoutes().contains(new KpiLogInfo.IgnoreRoute(request.getRequestURI(), request.getMethod()))) {
            super.doDispatch(request, response);
            return;
        }
        LocalDateTime startTime = LocalDateTime.now();
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        HandlerExecutionChain handler = getHandler(request);

        try {
            super.doDispatch(request, response);
        } finally {
            log(request, response, handler, startTime);
            updateResponse(response);
        }
    }
    private void log(HttpServletRequest requestToCache, HttpServletResponse responseToCache, HandlerExecutionChain handler,
                     LocalDateTime startTime) {
        try {
            KpiLog kpiLog = new KpiLog();

            String hostAddress = "localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.warn("The host name could not be determined, using `localhost` as fallback");
            }
            kpiLog.setApplicationCode(applicationInfo.getApplicationCode());
            kpiLog.setServiceCode(applicationInfo.getServiceCode());
            kpiLog.setAccount(requestToCache.getRemoteUser());
            kpiLog.setIpPortParentNode(hostAddress);
            kpiLog.setIpPortCurrentNode(getClientIpAddress(requestToCache));
            kpiLog.setTransactionStatus(Integer.valueOf(String.valueOf(responseToCache.getStatus())));
            kpiLog.setSessionId(requestToCache.getSession().getId());
            kpiLog.setUsername(requestToCache.getRemoteUser());
            kpiLog.setActionName(requestToCache.getServletPath());
            if (handler.getHandler() != null && (handler.getHandler() instanceof HandlerMethod)) {
                kpiLog.setActionName(((HandlerMethod) handler.getHandler()).toString());
            }
            kpiLog.setResponseContent(gson.toJson(getResponsePayload(responseToCache)));
            kpiLog.setRequestContent(gson.toJson(getRequestPayload(requestToCache)));
            kpiLog.setStartTime(startTime.format(formatter));
            var endTime = LocalDateTime.now();
            kpiLog.setEndTime(endTime.format(formatter));
            var duration = Duration.between(startTime, endTime).toMillis();
            kpiLog.setDuration(String.valueOf(duration));
            kpiLog.setSessionId(requestToCache.getRequestedSessionId());
            if (responseToCache.getStatus() != 200) {
                kpiLog.setErrorCode(String.valueOf(responseToCache.getStatus()));
                kpiLog.setErrorDescription(getResponsePayload(responseToCache));
            }
            kpiLogService.save(kpiLog);
        } catch (Exception e) {
            logger.error("Logging process is not fine. Skipping!");
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
        }
        return "[unknown]";
    }
    private String getRequestPayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {

            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 5120);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    // NOOP
                }
            }
        }
        return "[unknown]";
    }
    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

}