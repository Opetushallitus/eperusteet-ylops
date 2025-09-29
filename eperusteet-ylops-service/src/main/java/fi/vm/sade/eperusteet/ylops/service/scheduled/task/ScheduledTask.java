package fi.vm.sade.eperusteet.ylops.service.scheduled.task;

public interface ScheduledTask {
    // Mitä suurempi arvo, sen tärkeämpi
    int getPriority();
    String getName();
    void execute();
    void executeAsync();
}
