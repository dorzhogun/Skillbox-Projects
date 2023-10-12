import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Main
{
    public static void main(String[] args)
    {
        // общее число потоков и транзакций
        final int threadsCount = 4;
        final int transactionsCount = 1000;
        // счетчик переводов свыше 50_000
        AtomicLong bigSumCounter = new AtomicLong();

        Random random = new Random();
        Bank delta = new Bank();
        Hashtable<String, Account> map = new Hashtable<>();

        delta.setAccounts(delta.getRandomBankAccounts(map));

        Account account = new Account("Number1", 100_000_000, false);
        String accNum = account.getAccNumber();
        map.put(accNum, account);

        Account account1 = new Account("Number2", 100_000_000, false);
        String accNum1 = account1.getAccNumber();
        map.put(accNum1, account1);

        // добавляем мап в экземпляр класса банк
        delta.setAccounts(map);

        // проверяем общую сумму денег на всех счетах в банке
        System.out.println("Initial Bank Balance: " + delta.getBankBalance());

        // создаем лист с номерами счетов
        ArrayList<String> accNumbers = new ArrayList<>();
        delta.getAccounts().forEach((number, value) -> accNumbers.add(number));

        // создаем лист потоков и запускаем потоки в цикле, а внутри потоков запускаем цикл транзакций
        ArrayList<Thread> threads = new ArrayList<>();

        // запускаем цикл с новыми потоками
        for (int k = 0; k < threadsCount; k++) {
            // добавляем к листу потоков новый поток
            threads.add(new Thread(() -> {
                for ( int j = 0; j < transactionsCount; j++) {
                    // в потоке определяем случайные счета и сумму транзакции
                    String fromAccountNum = accNumbers.get(random.nextInt(accNumbers.size()));
                    String toAccountNum = accNumbers.get(random.nextInt(accNumbers.size()));
                    long sumToTransfer = (long) (Math.random() * 10_000_000);
                    // проверяем сумму транзакции - если более 50_000, то увеличиваем счетчик на 1
                    if (sumToTransfer > 50_000) {
                        bigSumCounter.incrementAndGet();
                        // если счетчик больше 5% от количества всех транзакций, то
                        if (bigSumCounter.get() > transactionsCount * 0.05) {
                            // уменьшаем сумму транзакции, чтобы всего таких сумм было не более 5%
                            sumToTransfer = (long) (sumToTransfer * 0.0045);
                        }
                    }
                    // запускаем метод транзакции класс Банк
                    try {
                        delta.transfer(fromAccountNum, toAccountNum, sumToTransfer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        // запускаем потоки листа
        threads.forEach(Thread::start);

        // главный поток выполняет join() и останавливается пока не завершится поток, вызвавший метод transfer()
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // проверяем баланс банка после всех транзакций
        System.out.println("TotalBankBalance after transactions: " + delta.getBankBalance());
    }
}

