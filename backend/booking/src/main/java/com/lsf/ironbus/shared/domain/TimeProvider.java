package com.lsf.ironbus.shared.domain;

import java.time.Instant;

public interface TimeProvider {

    Instant now();
}