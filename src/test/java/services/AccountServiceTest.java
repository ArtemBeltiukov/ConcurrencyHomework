package services;

import model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
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

    @BeforeEach
    public void clearFolder() {
        if (FILE.exists())
            Arrays.stream(Objects.requireNonNull(FILE.listFiles())).forEach(File::delete);
    }

    @ParameterizedTest
    @CsvSource({"10", "20", "30"})
    void testCreateAndSerializeAccountsCheckCount(int count) {
        //When
        for (int i = 0; i < count; i++) {
            Account account = ACCOUNT_FACTORY.generateAccount();
            ACCOUNT_SERVICE.createAccount(account);
        }
        //Then
        assertEquals(Objects.requireNonNull(FILE.listFiles()).length, count);
    }


    @Test
    void fillCache() {
        assertDoesNotThrow(() -> Arrays.stream(Objects.requireNonNull(FILE.listFiles())).forEach(ACCOUNT_SERVICE::deserializeAccount));
    }

    @Test
    void getSummaryBalanceDoesNotThrow() {
        ACCOUNT_SERVICE.createAndSerializeAccounts(10);
        assertDoesNotThrow(ACCOUNT_SERVICE::getSummaryBalance);
    }

    @Test
    void getSummaryBalanceEqual() {
        // Given
        ACCOUNT_SERVICE.createAndSerializeAccounts(10);
        long actual = 0;
        // When
        try {
            actual = ACCOUNT_SERVICE.getSummaryBalance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long expected = Arrays.stream(Objects.requireNonNull(FILE.listFiles()))
                .map(ACCOUNT_SERVICE::deserializeAccount).mapToLong(Account::getBalance).sum();
        // Then
        assertEquals(expected, actual);
    }

    @Test
    void deserializeAccount() throws IOException, ClassNotFoundException {
        //Given
        ACCOUNT_SERVICE.createAndSerializeAccounts(1);
        File accFile = Objects.requireNonNull(new File(PATH).listFiles())[0];
        Account expected;
        // when
        try (FileInputStream fileInputStream = new FileInputStream(accFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            expected = (Account) objectInputStream.readObject();
        }
        Account actual = ACCOUNT_SERVICE.deserializeAccount(accFile);
        //then
        assertEquals(expected, actual);
    }

    @Test
    void getAccountsFromFilesThrewException() {
        assertThrows(FileNotFoundException.class, ACCOUNT_SERVICE::getAccountsFromFiles);
    }

    @Test
    void getAccountsFromFiles() throws FileNotFoundException {
        // Given
        ACCOUNT_SERVICE.createAndSerializeAccounts(1);
        //When
        List<Account> expected = Arrays.stream(Objects.requireNonNull(FILE.listFiles()))
                .map(ACCOUNT_SERVICE::deserializeAccount).collect(Collectors.toList());
        List<Account> actual = ACCOUNT_SERVICE.getAccountsFromFiles();
        //Then
        assertEquals(expected, actual);
    }

}