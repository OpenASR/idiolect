package com.jetbrains.idear.asr;

public interface ASRService {

    void init();

    void dispose();

    enum Status {
        INIT,
        ACTIVE,
        INACTIVE,
        TERMINATED
    }

    Status getStatus();

    Status activate();
    Status deactivate();

}
