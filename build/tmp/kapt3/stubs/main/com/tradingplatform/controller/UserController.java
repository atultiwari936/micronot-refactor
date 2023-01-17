package com.tradingplatform.controller;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0003\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0005\u0018\u00010\u00042\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\b\u0001\u0010\b\u001a\u00020\tH\u0007J&\u0010\n\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0005\u0018\u00010\u00042\b\b\u0001\u0010\u0006\u001a\u00020\u000b2\b\b\u0001\u0010\b\u001a\u00020\tH\u0007J\"\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\b\b\u0001\u0010\u0006\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\tH\u0007J\"\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\t0\r2\b\b\u0001\u0010\u0006\u001a\u00020\t2\b\b\u0001\u0010\u0010\u001a\u00020\tH\u0007J\"\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\r2\b\b\u0001\u0010\u0006\u001a\u00020\t2\b\b\u0001\u0010\u0010\u001a\u00020\tH\u0007J\u001e\u0010\u0013\u001a\u000e\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u0001\u0018\u00010\u00042\b\b\u0001\u0010\u0006\u001a\u00020\u0014H\u0007\u00a8\u0006\u0015"}, d2 = {"Lcom/tradingplatform/controller/UserController;", "", "()V", "addInventory", "Lio/micronaut/http/MutableHttpResponse;", "Ljava/io/Serializable;", "body", "Lcom/tradingplatform/model/QuantityInput;", "userName", "", "addWallet", "Lcom/tradingplatform/model/WalletInput;", "createOrder", "Lio/micronaut/http/HttpResponse;", "Lcom/tradingplatform/model/OrderOutput;", "Lcom/tradingplatform/model/OrderInput;", "user_name", "getAccountInformation", "getOrder", "register", "Lcom/tradingplatform/model/Register;", "esop_trading_platform"})
@io.micronaut.http.annotation.Controller(value = "/user")
public final class UserController {
    
    public UserController() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    @io.micronaut.http.annotation.Post(value = "/register", consumes = {"application/json"}, produces = {"application/json"})
    public final io.micronaut.http.MutableHttpResponse<? extends java.lang.Object> register(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    com.tradingplatform.model.Register body) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Post(value = "/{user_name}/order")
    public final io.micronaut.http.HttpResponse<com.tradingplatform.model.OrderOutput> createOrder(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    com.tradingplatform.model.OrderInput body, @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.QueryValue
    java.lang.String user_name) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Get(value = "/{user_name}/accountInformation")
    public final io.micronaut.http.HttpResponse<java.lang.String> getAccountInformation(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    java.lang.String body, @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.QueryValue
    java.lang.String user_name) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    @io.micronaut.http.annotation.Post(value = "/{userName}/inventory")
    public final io.micronaut.http.MutableHttpResponse<? extends java.io.Serializable> addInventory(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    com.tradingplatform.model.QuantityInput body, @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.PathVariable(name = "userName")
    java.lang.String userName) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    @io.micronaut.http.annotation.Post(value = "/{userName}/wallet")
    public final io.micronaut.http.MutableHttpResponse<? extends java.io.Serializable> addWallet(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    com.tradingplatform.model.WalletInput body, @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.PathVariable(name = "userName")
    java.lang.String userName) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Get(value = "/{user_name}/order")
    public final io.micronaut.http.HttpResponse<java.lang.String> getOrder(@org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.Body
    java.lang.String body, @org.jetbrains.annotations.NotNull
    @io.micronaut.http.annotation.QueryValue
    java.lang.String user_name) {
        return null;
    }
}