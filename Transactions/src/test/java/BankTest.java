import junit.framework.TestCase;

import java.util.Hashtable;

public class BankTest extends TestCase
{
    Bank bank = new Bank();
    Hashtable<String, Account> map = new Hashtable<>();

    @Override
    protected void setUp() throws Exception {
        map.put("1",new Account("1", 100_000, false));
        map.put("2",new Account("2", 200_000, false));
        map.put("3",new Account("3", 300_000, false));
        map.put("4",new Account("4", 400_000, false));
        map.put("5",new Account("5", 500_000, false));
        bank.setAccounts(map);
        super.setUp();
    }

    public void testGetBankBalance()
    {
        long actual = bank.getBankBalance();
        long expected = 1_500_000;
        assertEquals(actual, expected);
    }

    public void testGetBalance()
    {
        long actual = bank.getBalance("3");
        long expected = 300_000;
        assertEquals(actual, expected);
    }

    public void testTransfer() throws InterruptedException {
        long actual = bank.getBankBalance();
        bank.transfer("2", "4", 45_000);
        bank.transfer("5", "1", 400_000);
        long expected = bank.getBankBalance();
        assertEquals(actual, expected);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
