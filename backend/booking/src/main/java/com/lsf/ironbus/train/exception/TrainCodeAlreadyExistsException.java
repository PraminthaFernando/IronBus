package com.lsf.ironbus.train.exception;

import com.lsf.ironbus.shared.error.DomainException;

public class TrainCodeAlreadyExistsException extends DomainException {

    public TrainCodeAlreadyExistsException(String code) {
        super(
                "TRAIN_CODE_ALREADY_EXISTS",
                "A train already exists with code: " + code
        );
    }
}