package dao;

import model.Account;

import java.io.FileNotFoundException;

public abstract class AccountController {

    public abstract boolean transact(int fromID, int toID, long amount);

    public abstract boolean transact(Account from, Account to, long amount);

    public abstract Account getAccountByID(int id) throws FileNotFoundException;

    public abstract boolean createAccount(Account account);

    public abstract boolean updateAccount(Account account);

}
