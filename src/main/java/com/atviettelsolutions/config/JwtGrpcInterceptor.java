package com.atviettelsolutions.config;

import io.grpc.*;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@AllArgsConstructor
public class JwtGrpcInterceptor implements ServerInterceptor{
    private final JwtDecoder jwtDecoder;
    private final GrpcContext grpcContext;
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        Metadata.Key<String> authKey = Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
        String authHeader = headers.get(authKey);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return next.startCall(call, headers);
        }
        String token = authHeader.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(token);
            grpcContext.setJwt(jwt);
            Context ctx = Context.current();
            return Contexts.interceptCall(ctx, call, headers, next);
        } catch (JwtException exception) {
            call.close(Status.UNAUTHENTICATED.withDescription("Token validation failed: " + exception.getMessage()), headers);
            return new ServerCall.Listener<>() {};
        }
    }
}
