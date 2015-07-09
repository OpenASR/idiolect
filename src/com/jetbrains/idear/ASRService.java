package com.jetbrains.idear;

public interface ASRService {

    enum Status {
        ACTIVE,
        INACTIVE,

        TERMINATED
    }

    Status getStatus();

    Status activate();
    Status deactivate();

}
