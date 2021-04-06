package model;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account implements Serializable {

    private static final AtomicInteger count = new AtomicInteger(0);
    private long balance;
    private String name;
    private String city;
    private ReentrantLock lock = new ReentrantLock();
    private final int id;
    private boolean locked;

    public Account(long balance, String name, String city) {
        this.balance = balance;
        this.name = name;
        this.city = city;
        this.id = count.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", id=" + id +
                '}';
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public boolean getLock() throws InterruptedException {
        return lock.tryLock(1, TimeUnit.SECONDS);
    }

    public void unlock() {
        if (lock.isHeldByCurrentThread())
            lock.unlock();
    }

    public boolean isLocked() {
        return locked;
    }

}
