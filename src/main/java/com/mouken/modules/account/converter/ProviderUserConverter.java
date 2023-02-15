package com.mouken.modules.account.converter;

public interface ProviderUserConverter<T, R> {
    R convert(T t);
}
