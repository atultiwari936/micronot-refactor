package com.tradingplatform.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\'\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010*\u001a\u00020\u0005H\u00d6\u0001J\t\u0010+\u001a\u00020\u0003H\u00d6\u0001R2\u0010\b\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00050\u000b0\n0\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0010\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u001a\u0010\u0019\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u0011\u0010\u001e\u001a\u00020\u001f\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001b\u00a8\u0006,"}, d2 = {"Lcom/tradingplatform/model/Order;", "", "type", "", "qty", "", "price", "(Ljava/lang/String;II)V", "filled", "Ljava/util/ArrayList;", "", "Lkotlin/Pair;", "getFilled", "()Ljava/util/ArrayList;", "setFilled", "(Ljava/util/ArrayList;)V", "filledQty", "getFilledQty", "()I", "setFilledQty", "(I)V", "id", "getId", "getPrice", "getQty", "status", "getStatus", "()Ljava/lang/String;", "setStatus", "(Ljava/lang/String;)V", "timestamp", "", "getTimestamp", "()J", "getType", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "esop_trading_platform"})
public final class Order {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String type = null;
    private final int qty = 0;
    private final int price = 0;
    @org.jetbrains.annotations.NotNull
    private java.lang.String status = "unfilled";
    @org.jetbrains.annotations.NotNull
    private java.util.ArrayList<kotlin.Pair<java.lang.String, java.lang.Integer>[]> filled;
    private final int id = 0;
    private final long timestamp = 0L;
    private int filledQty = 0;
    
    @org.jetbrains.annotations.NotNull
    public final com.tradingplatform.model.Order copy(@org.jetbrains.annotations.NotNull
    java.lang.String type, int qty, int price) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public Order(@org.jetbrains.annotations.NotNull
    java.lang.String type, int qty, int price) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getType() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int getQty() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int getPrice() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getStatus() {
        return null;
    }
    
    public final void setStatus(@org.jetbrains.annotations.NotNull
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.ArrayList<kotlin.Pair<java.lang.String, java.lang.Integer>[]> getFilled() {
        return null;
    }
    
    public final void setFilled(@org.jetbrains.annotations.NotNull
    java.util.ArrayList<kotlin.Pair<java.lang.String, java.lang.Integer>[]> p0) {
    }
    
    public final int getId() {
        return 0;
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    public final int getFilledQty() {
        return 0;
    }
    
    public final void setFilledQty(int p0) {
    }
}