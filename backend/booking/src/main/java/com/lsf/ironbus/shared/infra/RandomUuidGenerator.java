package com.lsf.ironbus.shared.infra;

import com.lsf.ironbus.shared.domain.UuidGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomUuidGenerator implements UuidGenerator {

    @Override
    public UUID generate() {
        return UUID.randomUUID();
    }
}