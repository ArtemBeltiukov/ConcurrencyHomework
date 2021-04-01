package services;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountOperationService implements Runnable {

    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final AccountService accountService = new AccountService();
    private final Lock lock = new ReentrantLock();
    private static AtomicInteger operationCount = new AtomicInteger(1);
    private static AtomicInteger totalOperationCount = new AtomicInteger(0);

    public static AtomicInteger getTotalOperationCount() {
        return totalOperationCount;
    }

    @Override
    public void run() {

        int from;
        int to;
        long amount = 100000;
        boolean locked = false;

        try {
            while (!locked)
                locked = lock.tryLock(1000, TimeUnit.MILLISECONDS);

            if (operationCount.get() == 10) {
                from = 10;
                to = 1;
                operationCount.set(0);
            } else {
                from = operationCount.get();
                to = operationCount.get() + 1;
            }

            if (accountService.transact(from, to, amount))
                log.log(Level.INFO, "Transact {0}", String.format("%s amount from id %s to id %s successful",
                        amount, from, to));
            else
                log.log(Level.INFO, "Transact {0}", String.format("%s amount from id %s to id %s failed \n" +
                                "It have only %s amount",
                        amount, from, to, accountService.getAccountByID(from).getBalance()));
            operationCount.incrementAndGet();
            totalOperationCount.incrementAndGet();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (locked) {
                lock.unlock();

            }
        }
    }
}

