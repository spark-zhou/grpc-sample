package com.github.spark.grpc.server;


import com.github.spark.account.*;
import io.grpc.stub.StreamObserver;

public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {

        System.out.println("---------------------single request-------------------------");
        System.out.println("createAccount params as : " + request.toString());
        CreateAccountResponse.Builder respBuilder = CreateAccountResponse.newBuilder();
        respBuilder.setAccountId(123123)
                .setSuccess(true);
        responseObserver.onNext(respBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void queryAccount(QueryAccountRequest request, StreamObserver<QueryAccountResponse> responseObserver) {

        System.out.println("---------------------server stream-------------------------");
        System.out.println("queryAccount params as : " + request.toString());

        for (int i = 0; i < 10; i++) {
            QueryAccountResponse.Builder respBuilder = QueryAccountResponse.newBuilder();
            respBuilder.setAccountId(123123 + i)
                    .setSuccess(true);
            responseObserver.onNext(respBuilder.build());

        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<QueryAccountRequest> queryAccountBatch(StreamObserver<QueryAccountResponse> responseObserver) {
        System.out.println("---------------------client stream-------------------------");
        return new StreamObserver<>() {
            @Override
            public void onNext(QueryAccountRequest queryAccountRequest) {
                System.out.println("received client stream as :" + queryAccountRequest.toString());
//                responseObserver.onNext(respBuilder.build());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(QueryAccountResponse.newBuilder().setSuccess(true).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<QueryAccountRequest> queryAccountStream(StreamObserver<QueryAccountResponse> responseObserver) {
        System.out.println("---------------------client & server stream-------------------------");

        return new StreamObserver<>() {
            @Override
            public void onNext(QueryAccountRequest queryAccountRequest) {
                System.out.println("received client & server  stream as :" + queryAccountRequest.toString());
                responseObserver.onNext(QueryAccountResponse.newBuilder().setSuccess(true)
                        .setAccountId(System.currentTimeMillis()).build());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
