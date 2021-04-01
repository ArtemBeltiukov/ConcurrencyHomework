package services;

import dao.AccountController;
import model.Account;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountService extends AccountController {

    private final String PATH = System.getProperty("user.dir") + "/accounts";
    private final File folder = new File(PATH);

    public void createAndSerializeAccounts(int count) {
        AccountFactory accountFactory = new AccountFactory();
        for (int i = 0; i < count; i++) {
            Account account = accountFactory.generateAccount();
            createAccount(account);
        }
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

        return Arrays.stream(folder.listFiles())
                .map(x -> {
                    Account account = deserializeAccount(x);
                    System.out.println("Balance of account with id "
                            + account.getId()
                            + " is "
                            + account.getBalance());
                    return account;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean transact(int fromID, int toID, long amount) {
        Account from = getAccountByID(fromID);
        Account to = getAccountByID(toID);

        if (from != null && to != null) {
            if (from.getBalance() >= amount) {
                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);
                return updateAccount(from) && updateAccount(to);
            } else {
                return false;
            }
        } else {
            System.out.println("Can`t get access to account(s) with id:");
            if (from == null) {
                System.out.println(fromID);
            }
            if (to == null) {
                System.out.println(toID);
            }
            return false;
        }
    }

    @Override
    public boolean transact(Account from, Account to, long amount) {
        if (from != null && to != null) {
            if (from.getBalance() >= amount) {
                from.setBalance(from.getBalance() - amount);
                to.setBalance(to.getBalance() + amount);
                return updateAccount(from) & updateAccount(to);
            } else {
                return false;
            }
        } else {
            System.out.println("Can`t get access to account(s)");
            return false;
        }
    }

    @Override
    public Account getAccountByID(int id) {

        File file = new File(PATH + "/" + id + ".acc");
        Account account = null;
        if (file.exists())
            account = deserializeAccount(file);
        return account;
    }

    @Override
    public boolean createAccount(Account account) {

        if (!folder.exists())
            folder.mkdir();

        try (FileOutputStream outputStream = new FileOutputStream(PATH + "/" + account.getId() + ".acc");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAccount(Account account) {
        try (FileOutputStream outputStream = new FileOutputStream(PATH + "/" + account.getId() + ".acc");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
