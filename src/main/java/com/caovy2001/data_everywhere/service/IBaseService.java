package com.caovy2001.data_everywhere.service;

import lombok.NonNull;

import java.util.Map;

public interface IBaseService {
    Object responseExceptionForAPI(@NonNull String exceptionCode);
}
