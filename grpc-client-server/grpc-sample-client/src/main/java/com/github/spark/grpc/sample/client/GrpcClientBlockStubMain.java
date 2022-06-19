package com.github.spark.grpc.sample.client;

import com.github.spark.account.*;
import com.github.spark.grpc.proto.util.DecimalUtil;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class GrpcClientBlockStubMain {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
                .usePlaintext().build();
        AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(channel);

        //single request
        System.out.println("---------------------single request-------------------------");
        CreateAccountRequest.Builder builder = CreateAccountRequest.newBuilder();
        builder.setAccountType(AccountType.CURRENCY)
                .setAvailableBalance(DecimalUtil.fromBigDecimal(new BigDecimal(100)))
                .setTotalBalance(DecimalUtil.fromBigDecimal(new BigDecimal(100)))
                .setLockBalance(DecimalUtil.fromBigDecimal(new BigDecimal(0)))
                .setUserId(1)
                .build();
        CreateAccountResponse response = stub.createAccount(builder.build());
        System.out.println(response.toString());


        //server stream
        System.out.println("---------------------server stream-------------------------");
        QueryAccountRequest.Builder builder1 = QueryAccountRequest.newBuilder();
        builder1.setAccountType(AccountType.CURRENCY)
                .setUserId(1)
                .build();
        Iterator<QueryAccountResponse> response1 = stub.queryAccount(builder1.build());
        while(response1.hasNext()) {
            System.out.println(response1.next().toString());
        }


        // client stream
        System.out.println("---------------------client stream-------------------------");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        StreamObserver<QueryAccountResponse> streamObserver = new StreamObserver<>() {
            @Override
            public void onNext(QueryAccountResponse queryAccountResponse) {
                System.out.println("receivd resp: " + queryAccountResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("server return completed...");
                countDownLatch.countDown();
            }
        };

        AccountServiceGrpc.AccountServiceStub stubAsyn = AccountServiceGrpc.newStub(channel);
        StreamObserver<QueryAccountRequest> requestStreamObserver = stubAsyn.queryAccountBatch(streamObserver);
        for (int i = 0; i < 10; i++) {
            QueryAccountRequest.Builder builder2 = QueryAccountRequest.newBuilder();
            builder2.setAccountType(AccountType.CURRENCY)
                    .setUserId(i)
                    .build();
            requestStreamObserver.onNext(builder2.build());
        }
        requestStreamObserver.onCompleted();
        countDownLatch.await();

        //client & server stream
        System.out.println("---------------------client & server stream-------------------------");
        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        StreamObserver<QueryAccountResponse> streamObserver1 = new StreamObserver<>() {
            @Override
            public void onNext(QueryAccountResponse queryAccountResponse) {
                System.out.println("receivd resp: " + queryAccountResponse.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("server return completed...");
                countDownLatch1.countDown();
            }
        };

        AccountServiceGrpc.AccountServiceStub stubAsyn1 = AccountServiceGrpc.newStub(channel);
        StreamObserver<QueryAccountRequest> requestStreamObserver1 = stubAsyn1.queryAccountStream(streamObserver1);
        for (int i = 0; i < 10; i++) {
            QueryAccountRequest.Builder builder2 = QueryAccountRequest.newBuilder();
            builder2.setAccountType(AccountType.CURRENCY)
                    .setUserId(i)
                    .build();
            requestStreamObserver1.onNext(builder2.build());
        }
        requestStreamObserver1.onCompleted();
        countDownLatch1.await();
    }
}
