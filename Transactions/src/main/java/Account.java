public class Account
{

    private long money;
    private final String accNumber;
    private boolean isBlocked;

    public Account(String accNumber, long money, boolean isBlocked)
    {
        this.money = money;
        this.accNumber = accNumber;
        this.isBlocked = isBlocked;
    }

    public boolean getIsBlocked() { return isBlocked; }

    public void setBlocked() { isBlocked = true; }

    public String getAccNumber() { return accNumber; }

    public void setMoney(long money) { this.money = money; }

    public long getMoney() { return money; }

    public int compareTo(Account account) { return this.getAccNumber().compareTo(account.getAccNumber());
    }
}
