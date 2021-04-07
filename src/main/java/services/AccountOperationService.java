package services;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AccountOperationService implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(AccountOperationService.class.getName());
    private final AccountService accountService = new AccountService();
    private static final AtomicInteger totalOperationCount = new AtomicInteger(0);
    private static final AtomicInteger successfulOperationCount = new AtomicInteger(0);

    public AtomicInteger getTotalOperationCount() {
        return totalOperationCount;
    }

    public AtomicInteger getSuccessfulOperationCount() {
        return successfulOperationCount;
    }

    @Override
    public void run() {
        int from;
        int to;
        long amount = ThreadLocalRandom.current().nextLong(0, 100000);
        from = ThreadLocalRandom.current().nextInt(1, 11);
        to = ThreadLocalRandom.current().nextInt(1, 11);
        if (accountService.transact(from, to, amount)) {
            LOGGER.log(Level.INFO, "Transact {0}", String.format("%s amount from id %s to id %s successful",
                    amount, from, to));
            successfulOperationCount.incrementAndGet();
        } else
            LOGGER.log(Level.INFO, "Transact {0}", String.format("%s amount from id %s to id %s failed \n" +
                            "It have not enough amount",
                    amount, from, to));
        totalOperationCount.incrementAndGet();
    }
}


