package ru.codeunited.ignite.proto;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataGridCachesGrpcService extends DataGridCachesGrpc.DataGridCachesImplBase {

    @Autowired
    private Ignite ignite;

    @Override
    public void availableCaches(AvailableCachesRequest request, StreamObserver<AvailableCachesReply> responseObserver) {
        AvailableCachesReply.Builder builder = AvailableCachesReply.newBuilder();
        ignite.cacheNames().forEach(builder::addCache);
        AvailableCachesReply reply = builder.build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
