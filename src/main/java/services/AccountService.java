package services;

import model.Account;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountService {

    private final String PATH = System.getProperty("user.dir") + "/accounts";
    private final File folder = new File(getPath());
    private static List<Account> accounts = new ArrayList<>();

    public String getPath(){
        return PATH;
    }

    public void createAndSerializeAccounts(int count) {
        AccountFactory accountFactory = new AccountFactory();
        for (int i = 0; i < count; i++) {
            Account account = accountFactory.generateAccount();
            createAccount(account);
        }
    }

    public void fillCache() {
        File file = new File(getPath());
        for (File account : Objects.requireNonNull(file.listFiles())) {
            accounts.add(deserializeAccount(account));
        }
    }

    public long getSummaryBalance() throws FileNotFoundException {
        return getAccountsFromFiles().stream().mapToLong(Account::getBalance).sum();
    }

    public Account deserializeAccount(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Account) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public List<Account> getAccountsFromFiles() throws FileNotFoundException {
        if (!folder.exists() || Objects.requireNonNull(folder.listFiles()).length == 0)
            throw new FileNotFoundException("There is no accounts found");

        return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .map(this::deserializeAccount).collect(Collectors.toList());
    }

    public boolean transact(int fromID, int toID, long amount) {

        try {
            Account from = getAccountByID(fromID);
            Account to = getAccountByID(toID);

            while (!from.getLock() || !to.getLock()) {
                from.unlock();
                to.unlock();
                Thread.sleep(100);
            }
            boolean result;
            try {
                if (from.getBalance() >= amount) {
                    from.setBalance(from.getBalance() - amount);
                    to.setBalance(to.getBalance() + amount);
                    result = updateAccount(from) && updateAccount(to);
                } else {
                    result = false;
                }
            } finally {
                from.unlock();
                to.unlock();
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public Account getAccountByID(int id) {
        return accounts.stream().filter(x -> x.getId() == id).findFirst().orElseThrow();
    }

    public void createAccount(Account account) {

        if (!folder.exists())
            folder.mkdir();

        try (FileOutputStream outputStream = new FileOutputStream(getPath() + "/" + account.getId() + ".acc");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean updateAccount(Account account) {

        accounts = accounts.stream().map(x -> {
            if (x.equals(account))
                return account;
            else
                return x;
        }).collect(Collectors.toList());
        return true;
    }

    public void serializeAccount(Account account) {
        try (FileOutputStream outputStream = new FileOutputStream(getPath() + "/" + account.getId() + ".acc");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(account);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAccounts() {
        accounts.forEach(this::serializeAccount);
    }
}
