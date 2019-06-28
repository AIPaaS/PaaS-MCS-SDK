package com.ai.paas.ipaas.mcs.exception;

import com.ai.paas.GeneralRuntimeException;

public class CacheException extends GeneralRuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2196845577057650318L;

    private static final String MCS_MSG = "MCS RUNTIME ERROR";

    public CacheException(Exception ex) {
        super(MCS_MSG, ex);
    }

    public CacheException(String message) {
        super(message);
    }
}
