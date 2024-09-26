package com.lostmc.hungergames.scheduler;

import com.lostmc.hungergames.constructor.ScheduleArgs;

public interface Schedule {

    void pulse(ScheduleArgs args);
}
