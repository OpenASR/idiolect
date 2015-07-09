package com.jetbrains.idear;

public interface ASRService {

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
