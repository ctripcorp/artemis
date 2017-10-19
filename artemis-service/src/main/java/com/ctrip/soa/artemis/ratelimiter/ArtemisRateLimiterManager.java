package com.ctrip.soa.artemis.ratelimiter;

import com.ctrip.soa.artemis.config.ArtemisConfig;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterManager;
import com.ctrip.soa.caravan.util.ratelimiter.RateLimiterManagerConfig;

/**
 * Created by Qiang Zhao on 10/07/2016.
 */
public class ArtemisRateLimiterManager extends RateLimiterManager {

    public static final ArtemisRateLimiterManager Instance = new ArtemisRateLimiterManager();

    private ArtemisRateLimiterManager() {
        super("artemis.service", new RateLimiterManagerConfig(ArtemisConfig.properties()));
    }

}
