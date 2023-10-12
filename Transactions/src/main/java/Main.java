import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Main
{
    public static void main(String[] args)
    {
        // ����� ����� ������� � ����������
        final int threadsCount = 4;
        final int transactionsCount = 1000;
        // ������� ��������� ����� 50_000
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

        // ��������� ��� � ��������� ������ ����
        delta.setAccounts(map);

        // ��������� ����� ����� ����� �� ���� ������ � �����
        System.out.println("Initial Bank Balance: " + delta.getBankBalance());

        // ������� ���� � �������� ������
        ArrayList<String> accNumbers = new ArrayList<>();
        delta.getAccounts().forEach((number, value) -> accNumbers.add(number));

        // ������� ���� ������� � ��������� ������ � �����, � ������ ������� ��������� ���� ����������
        ArrayList<Thread> threads = new ArrayList<>();

        // ��������� ���� � ������ ��������
        for (int k = 0; k < threadsCount; k++) {
            // ��������� � ����� ������� ����� �����
            threads.add(new Thread(() -> {
                for ( int j = 0; j < transactionsCount; j++) {
                    // � ������ ���������� ��������� ����� � ����� ����������
                    String fromAccountNum = accNumbers.get(random.nextInt(accNumbers.size()));
                    String toAccountNum = accNumbers.get(random.nextInt(accNumbers.size()));
                    long sumToTransfer = (long) (Math.random() * 10_000_000);
                    // ��������� ����� ���������� - ���� ����� 50_000, �� ����������� ������� �� 1
                    if (sumToTransfer > 50_000) {
                        bigSumCounter.incrementAndGet();
                        // ���� ������� ������ 5% �� ���������� ���� ����������, ��
                        if (bigSumCounter.get() > transactionsCount * 0.05) {
                            // ��������� ����� ����������, ����� ����� ����� ���� ���� �� ����� 5%
                            sumToTransfer = (long) (sumToTransfer * 0.0045);
                        }
                    }
                    // ��������� ����� ���������� ����� ����
                    try {
                        delta.transfer(fromAccountNum, toAccountNum, sumToTransfer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        // ��������� ������ �����
        threads.forEach(Thread::start);

        // ������� ����� ��������� join() � ��������������� ���� �� ���������� �����, ��������� ����� transfer()
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // ��������� ������ ����� ����� ���� ����������
        System.out.println("TotalBankBalance after transactions: " + delta.getBankBalance());
    }
}

