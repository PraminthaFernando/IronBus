package com.lsf.ironbus.shared.persistence;

import org.hibernate.exception.ConstraintViolationException;

public final class ConstraintViolationInspector {

    private ConstraintViolationInspector() {
    }

    public static boolean causedByConstraint(
            Throwable throwable,
            String expectedConstraint
    ) {
        Throwable current = throwable;

        while (current != null) {
            if (current instanceof ConstraintViolationException violation) {
                return expectedConstraint.equals(
                        violation.getConstraintName()
                );
            }

            current = current.getCause();
        }

        return false;
    }
}