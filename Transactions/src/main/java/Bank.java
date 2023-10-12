import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Bank {
    private final AtomicInteger blockedAccountsCounter = new AtomicInteger();

    private final Map<String, Account> accounts = new Hashtable<>();
    private final Random random = new Random();

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами. Если сумма транзакции > 50000,
     * то после совершения транзакции, она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка счетов (как – на ваше
     * усмотрение)
     */
    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        if (accounts.get(fromAccountNum).compareTo(accounts.get(toAccountNum)) > 0) {
            // сравниваем аккаунты и синхронизируем в первую очередь по аккаунту, который больше
            synchronized (accounts.get(fromAccountNum)) {
                System.out.println(Thread.currentThread().getName() + " take first mutex");
                synchronized (accounts.get(toAccountNum)) {
                    System.out.println(Thread.currentThread().getName() + " take second mutex");
                    doTransaction(fromAccountNum, toAccountNum, amount);
                }
            }
        } else {
            // иначе синхронизируем сначала по аккаунту, который меньше
            synchronized (accounts.get(toAccountNum)) {
                System.out.println(Thread.currentThread().getName() + " take first mutex");
                synchronized (accounts.get(fromAccountNum)) {
                    System.out.println(Thread.currentThread().getName() + " take second mutex");
                    doTransaction(fromAccountNum, toAccountNum, amount);
                }
            }
        }
    }

    // метод для осуществления транзакции
    public void doTransaction(String fromAccountNum, String toAccountNum, long amount) {
        if (checkAccounts(fromAccountNum, toAccountNum, amount)) {
            // если ответ все ок,то осуществляем перевод между счетами
            // фиксируем остатки на счетах
            long depositAmount = accounts.get(fromAccountNum).getMoney();
            long debitAmount = accounts.get(toAccountNum).getMoney();

            System.out.println("Account " + fromAccountNum + ", currency before: " + depositAmount);
            System.out.println("Account " + toAccountNum + ", currency before: " + debitAmount);

            // устанавливаем новые значения остатков на счетах
            accounts.get(fromAccountNum).setMoney(depositAmount - amount);
            accounts.get(toAccountNum).setMoney(debitAmount + amount);

            System.out.println("Account " + fromAccountNum + ", currency after: "
                    + accounts.get(fromAccountNum).getMoney());
            System.out.println("Account " + toAccountNum + ", currency after: "
                    + accounts.get(toAccountNum).getMoney());
        }
    }

    // метод проверки счетов и сумм перед осуществлением транзакции
    private boolean checkAccounts(String fromAccountNum, String toAccountNum, long amount) {
        // индикатор проверки счетов
        boolean allRight = true;
        // проверяем достаточно ли средств для перевода, не заблокированы ли счета, не на свой ли счет перевод
        if ((accounts.get(fromAccountNum).getMoney() < amount)) {
            System.out.println("Transaction is not possible! Not enough money at Account N " + fromAccountNum
                    + ". Current currency: "
                    + accounts.get(fromAccountNum).getMoney() + " is less than transaction amount: " + amount);
            allRight = false;
        } else if (accounts.get(fromAccountNum).getIsBlocked()) {
            System.out.println("Account " + fromAccountNum + " is blocked. Transaction is not possible!");
            allRight = false;
        } else if (accounts.get(toAccountNum).getIsBlocked()) {
            System.out.println("Account " + toAccountNum + " is blocked. Transaction is not possible!");
            allRight = false;
        } else if (accounts.get(fromAccountNum).equals(accounts.get(toAccountNum))) {
            System.out.println("Transaction is not possible! You're trying to send money from your account "
                    + accounts.get(fromAccountNum).getAccNumber() + " to your own account "
                    + accounts.get(toAccountNum).getAccNumber());
            allRight = false;
        } else if (amount > 50_000) {
            try {
                System.out.println("Transaction amount: " + amount + " from " + fromAccountNum + " to "
                        + toAccountNum + " is more than 50_000. Please wait a moment - we're checking up...");

                // запускаем метод работы Службы Безопасности
                if (isFraud(fromAccountNum, toAccountNum, amount)) {
                    // если ответ пришел положительный результат (подозрение подтвердилось), то блокируем оба счета
                    accounts.get(fromAccountNum).setBlocked();
                    accounts.get(toAccountNum).setBlocked();
                    blockedAccountsCounter.addAndGet(2);
                    System.out.println("Accounts " + fromAccountNum + " and "
                            + toAccountNum + " were blocked on suspicion of fraud - please contact the bank");
                    System.out.println("Blocked accounts total quantity: " + blockedAccountsCounter.get());
                    allRight = false;
                } else {
                    System.out.println("Big amount transaction is passed by Bank in " + Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return allRight;
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public long getBalance(String accNumber) {
        return accounts.get(accNumber).getMoney();
    }

    public void setAccounts(Hashtable<String, Account> map) {
        accounts.putAll(map);
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    // метод возвращает сумму денег на всех счетах банка
    public long getBankBalance() {
        AtomicLong bankBalance = new AtomicLong();
        for (Account account : accounts.values()) {
            bankBalance.addAndGet(account.getMoney());
        }
        return bankBalance.get();
    }

    // метод создания счетов банка случайным образом
    public Hashtable<String, Account> getRandomBankAccounts(Hashtable<String, Account> map) {
        // создаем map из 100 аккаунтов с номером счета и суммой случайным образом, по умолчанию счета не заблокированы
        // ключ - номер счета, значение - аккаунт (где ключ номер счета, значение - сумма денег на счете)
        for (int i = 0; i < 100; i++) {
            int randomNumber = (int) (Math.random() * 100_000_000);
            String number = String.valueOf(randomNumber);
            long deposit = (long) (Math.random() * 100_000_000 + 100_000);
            boolean isBlocked = false;
            Account account = new Account(number, deposit, isBlocked);
            String accNum = account.getAccNumber();
            map.put(accNum, account);
        }
        return map;
    }

}
