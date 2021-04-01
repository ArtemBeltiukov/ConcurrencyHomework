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
        boolean needToCreateAccs = false;
        AccountService accountService = new AccountService();
        AccountOperationService accountOperationService = new AccountOperationService();
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_TREADS);
        List<Account> accountListStart = new LinkedList<>();
        List<Account> accountListFinal = new LinkedList<>();
        List<Account> accountListDifferences = new LinkedList<>();
        int count = 0;
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
        for (int i = 0; i < NUMBER_OF_OPERATIONS; i++)
            executorService.execute(accountOperationService);
        //ждем завершения
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //получаем измененный список аккаунтов+ пишем их в переменную
        try {
            accountListFinal = accountService.getAccountsFromFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // завершаем потоки
        executorService.shutdown();
        // сравниваем списки аккаунтов было\стало
        for (Account accountStart : accountListStart) {
            Account accountFinal = accountListFinal.get(count);
            if (accountStart.getBalance() != accountFinal.getBalance()) {
                accountListDifferences.add(accountFinal);
            }
            count++;
        }

        System.out.println("Total operation count: "
                + AccountOperationService.getTotalOperationCount());
        if (accountListDifferences.isEmpty()) {
            System.out.println("Lists are equal");
        } else
            System.out.println(accountListDifferences);
    }
}
