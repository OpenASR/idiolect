package com.jetbrains.idear.asr;

public interface ASRService {
    void init();
    void dispose();
    boolean activate();
    boolean deactivate();
}
