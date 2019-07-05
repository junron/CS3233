package q2;

public class SavingAccount extends BankAccount {
  private double interestRate = 0.1;
  public SavingAccount(String name){
    this(name,0);
  }
  public SavingAccount(String name, double balance){
    super(balance,name);
  }
  public void addInterest(){
    super.deposit(super.getAccountBalance()*interestRate);
  }
  @Override
  public String toString() {
    return super.toString().replace("Bank ",super.getOwnerName()+" Saving ");
  }
}
