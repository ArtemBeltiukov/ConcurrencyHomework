package services;

import model.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    private static final String PATH = System.getProperty("user.dir") + "\\accounts";
    private static final File FILE = new File(PATH);
    private static final AccountService ACCOUNT_SERVICE = new AccountService();
    private static final AccountFactory ACCOUNT_FACTORY = new AccountFactory();
    private static List<Account> accounts = new ArrayList<>();




}