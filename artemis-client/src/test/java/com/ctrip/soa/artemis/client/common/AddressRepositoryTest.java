package com.ctrip.soa.artemis.client.common;

import org.junit.Assert;
import org.junit.Test;

import com.ctrip.soa.artemis.client.test.utils.ArtemisClientConstants;
import com.ctrip.soa.artemis.config.RestPaths;

/**
 * Created by fang_j on 10/07/2016.
 */
public class AddressRepositoryTest {
    private final AddressRepository _addressRepository = new AddressRepository(ArtemisClientConstants.ClientId, ArtemisClientConstants.ManagerConfig,
            RestPaths.CLUSTER_UP_DISCOVERY_NODES_FULL_PATH);
    @Test
    public void testRefresh() {
        _addressRepository.refresh();
        Assert.assertNotNull(_addressRepository.get());
    }
}
