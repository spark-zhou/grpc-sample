package com.github.spark.grpc.sample.client;

import com.github.spark.account.*;
import com.github.spark.grpc.proto.util.DecimalUtil;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.math.BigDecimal;
import java.util.Iterator;

public class GrpcClientMain {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
                .usePlaintext().build();
        AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(channel);

        CreateAccountRequest.Builder builder = CreateAccountRequest.newBuilder();
        builder.setAccountType(AccountType.CURRENCY)
                .setAvailableBalance(DecimalUtil.fromBigDecimal(new BigDecimal(100)))
                .setTotalBalance(DecimalUtil.fromBigDecimal(new BigDecimal(100)))
                .setLockBalance(DecimalUtil.fromBigDecimal(new BigDecimal(0)))
                .setUserId(1)
                .build();
        CreateAccountResponse response = stub.createAccount(builder.build());
        System.out.println(response.toString());


        QueryAccountRequest.Builder builder1 = QueryAccountRequest.newBuilder();
        builder1.setAccountType(AccountType.CURRENCY)
                .setUserId(1)
                .build();
        Iterator<QueryAccountResponse> response1 = stub.queryAccount(builder1.build());
        while(response1.hasNext()) {
            System.out.println(response1.next().toString());
        }


//        for (int i = 0; i < 10; i++) {
//            QueryAccountRequest.Builder builder2 = QueryAccountRequest.newBuilder();
//            builder2.(AccountType.CURRENCY)
//                    .setUserId(1)
//                    .build();
//        }
//        Iterator<QueryAccountResponse> response2 = stub.(builder2.build());
    }
}
