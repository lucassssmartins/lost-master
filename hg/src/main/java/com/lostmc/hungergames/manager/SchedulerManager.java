package com.lostmc.hungergames.manager;

import com.lostmc.game.GamePlugin;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.ScheduleArgs;
import com.lostmc.hungergames.scheduler.Schedule;
import com.lostmc.hungergames.scheduler.SchedulerListener;

import java.util.*;

public class SchedulerManager extends Management {

    private Map<String, Schedule> schedules;

    public SchedulerManager(GamePlugin plugin) {
        super(plugin);
        this.schedules = new HashMap<>();
    }

    @Override
    public void onEnable() {
        registerListener(new SchedulerListener((HungerGames) getPlugin()));
    }

    public void pulse() {
        Iterator<Schedule> iterator = new ArrayList<>(schedules.values()).iterator();
        while (iterator.hasNext()) {
            Schedule schedule = iterator.next();
            try {
                schedule.pulse(new ScheduleArgs(getPlugin().getGameType(), ((HungerGames) getPlugin()).getGameStage(),
                        ((HungerGames) getPlugin()).getTimer()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addScheduler(String id, Schedule schedule) {
        if (schedules.containsKey(id))
            return;
        schedules.put(id, schedule);
    }

    public void cancelScheduler(String id) {
        schedules.remove(id);
    }

    public Schedule getScheduler(String id) {
        return schedules.get(id);
    }

    public Collection<Schedule> getSchedules() {
        return schedules.values();
    }

    @Override
    public void onDisable() {

    }
}
