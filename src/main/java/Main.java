import services.AccountOperationService;
import services.AccountService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int NUMBER_OF_OPERATIONS = 150000;

    public static void main(String[] args) {

        final int ACC_COUNT = 10;
        final int NUMBER_OF_TREADS = 20;
        // Нужно ли создавать акки
        boolean needToCreateAccs = true;
        AccountService accountService = new AccountService();
        AccountOperationService accountOperationService = new AccountOperationService();
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_TREADS);
        long summaryBalance = 0;
        long summaryBalanceAtEnd = 0;

        // Создавалка новых файлов, с генерацией
        if (needToCreateAccs)
            accountService.createAndSerializeAccounts(ACC_COUNT);

        accountService.fillCache();

        try {
            summaryBalance = accountService.getSummaryBalance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // запускает операции
        runTasks(NUMBER_OF_OPERATIONS, executorService, accountOperationService);

        // завершаем потоки
        executorService.shutdown();

        accountService.writeAccounts();
        try {
            summaryBalanceAtEnd = accountService.getSummaryBalance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (summaryBalance == summaryBalanceAtEnd)
            System.out.println("summary balances are equal");
        else
            System.out.println("summary balances are not equal");

        System.out.println("Total operation count: "
                + accountOperationService.getTotalOperationCount());
        System.out.println("Successful operation count: "
                + accountOperationService.getSuccessfulOperationCount());

    }

    public static void runTasks(int count, ExecutorService executorService, AccountOperationService accountOperationService) {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(Executors.callable(accountOperationService));
        }
        //ждем завершения
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (NUMBER_OF_OPERATIONS - accountOperationService.getSuccessfulOperationCount().get() > 0) {
            runTasks(NUMBER_OF_OPERATIONS - accountOperationService.getSuccessfulOperationCount().get(), executorService, accountOperationService);
        }
    }
}
