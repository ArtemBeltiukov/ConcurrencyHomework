import model.Account;
import services.AccountOperationService;
import services.AccountService;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        final int ACC_COUNT = 10;
        final int NUMBER_OF_TREADS = 20000;
        final int NUMBER_OF_OPERATIONS = 1000;
        // Нужно ли создавать акки
        boolean needToCreateAccs = false;
        AccountService accountService = new AccountService();
        AccountOperationService accountOperationService = new AccountOperationService();
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_TREADS);
        List<Account> accountListStart = new LinkedList<>();
        List<Account> accountListFinal = new LinkedList<>();

        // Создавалка новых файлов, с генерацией
        if (needToCreateAccs)
            accountService.createAndSerializeAccounts(ACC_COUNT);
        //печатает список файлов из папки и записывает их в переменную
        try {
            accountListStart = accountService.getAccountsFromFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // запускает операции
        runTasks(NUMBER_OF_OPERATIONS,executorService, accountOperationService);
        // завершаем потоки
        executorService.shutdown();

        if (accountListStart.stream().mapToLong(Account::getBalance).sum()
            ==accountListFinal.stream().mapToLong(Account::getBalance).sum())
            System.out.println("summary balances are equal");
        else
            System.out.println("summary balances are not equal");

        System.out.println("Total operation count: "
                + AccountOperationService.getTotalOperationCount());
        System.out.println("Successful operation count: "
                + AccountOperationService.getSuccessfulOperationCount());

    }

    public static void runTasks(int count, ExecutorService executorService, AccountOperationService accountOperationService){
        for (int i=0;i<count;i++){
            executorService.execute(accountOperationService);
        }
        //ждем завершения
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (count - AccountOperationService.getSuccessfulOperationCount().get()>0){
            runTasks(count - AccountOperationService.getSuccessfulOperationCount().get(),executorService,accountOperationService);
        }
    }
}
