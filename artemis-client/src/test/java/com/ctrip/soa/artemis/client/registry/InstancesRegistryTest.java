package com.ctrip.soa.artemis.client.registry;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.socket.WebSocketMessage;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.client.test.utils.Instances;
import com.ctrip.soa.caravan.common.concurrent.Threads;

/**
 * Created by fang_j on 10/07/2016.
 */
public class InstancesRegistryTest {
    private static final AtomicLong sendHeartbeatCounts = new AtomicLong(0);
    private static final AtomicLong acceptHeartbeatCounts = new AtomicLong(0);
    private static final InstanceRepository repository = new InstanceRepository(ArtemisClientConstants.RegistryClientConfig);

    private static final InstanceRegistry registry = new InstanceRegistry(repository, ArtemisClientConstants.RegistryClientConfig){
        @Override
        protected void sendHeartbeat() {
            super.sendHeartbeat();
            sendHeartbeatCounts.incrementAndGet();
        }

        @Override
        protected void acceptHeartbeat(final WebSocketMessage<?> message) {
            super.acceptHeartbeat(message);
            acceptHeartbeatCounts.incrementAndGet();
        }
    };

    @BeforeClass
    public static void init() {
        Threads.sleep(100);
        repository.register( Instances.newInstances(2));
    }

    @Test(timeout=10000)
    public void testHeartbeat() {
        while(true) {
            System.out.println("send hearts: " + sendHeartbeatCounts.get());
            System.out.println("accept hearts: " + acceptHeartbeatCounts.get());
            if ((sendHeartbeatCounts.get() > 0) && (acceptHeartbeatCounts.get() > 0)) {
                break;
            }
            Threads.sleep(1000);
        }
    }

    public void testRegister() {
        registry.registerToServicesRegistry(null);
    }

}
