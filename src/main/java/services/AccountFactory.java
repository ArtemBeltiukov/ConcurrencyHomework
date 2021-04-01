package services;

import data.Cities;
import data.Names;
import model.Account;

import java.util.concurrent.ThreadLocalRandom;

public class AccountFactory {

    public Account generateAccount() {
        long balance = ThreadLocalRandom.current().nextLong(0, 1000000);
        int randNameNum = ThreadLocalRandom.current().nextInt(0, Names.values().length - 1);
        String name = String.valueOf(Names.values()[randNameNum]);
        int randCityNum = ThreadLocalRandom.current().nextInt(0, Cities.values().length - 1);
        String city = String.valueOf(Cities.values()[randCityNum]);
        return new Account(balance, name, city);
    }

}
