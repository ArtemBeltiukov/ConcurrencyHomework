package model;

import java.io.Serializable;
import java.util.Objects;
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

    public Account(long balance, String name, String city) {
        this.balance = balance;
        this.name = name;
        this.city = city;
        this.id = count.incrementAndGet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return balance == account.balance && id == account.id && name.equals(account.name) && city.equals(account.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance, name, city, lock, id);
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

    public long getId() {
        return id;
    }

    public boolean getLock() throws InterruptedException {
        return lock.tryLock(1, TimeUnit.SECONDS);
    }

    public void unlock() {
        if (lock.isLocked())
            lock.unlock();
    }

    public static void setIDCounterToZero(){
        count.set(0);
    }

}
