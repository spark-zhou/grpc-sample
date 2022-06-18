package com.github.spark.grpc.server;

import io.grpc.*;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class GrpcServer {


    public static void main( String[] args ) throws IOException {

        ServerBuilder.forPort(9090)
                .addService(new AccountServiceImpl()).intercept(new ServerInterceptor() {
                    @Override
                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
                        System.out.println("ServerInterceptor.....");
                        return serverCallHandler.startCall(serverCall,metadata);
                    }
                })
                .build()
                .start();

        while (true) {

        }
    }
}
