package q2;

public class BankAccount{
  /********************************************************************************
   * DATA MEMBERS
   /********************************************************************************/
  private static int lastAssignedNum = 1;
  private static final double MIN_BALANCE = 100;
  private static final String DEFAULT_NAME = "No Name"; //new constant added
  private int accountNumber;
  private double accountBalance;
  private String ownerName; //new data member added

  /**
   *Overloaded constructor. No parameters
   */
  public BankAccount(){
    accountNumber = lastAssignedNum;
    lastAssignedNum++;
    accountBalance = MIN_BALANCE;
    ownerName =  DEFAULT_NAME;
  }
  /**
   Overloaded constructor.
   @param balance balance
   */
  public BankAccount(double balance){
    accountNumber = lastAssignedNum;
    lastAssignedNum++;
    accountBalance = balance;
    ownerName =  DEFAULT_NAME;
  }
  /**
   Overloaded constructor.
   @param name string
   */
  public BankAccount(String name){
    accountNumber = lastAssignedNum;
    lastAssignedNum++;
    accountBalance = MIN_BALANCE;
    ownerName =  name;
  }
  /**
   Overloaded constructor.
   @param balance double
   @param name String
   */
  public BankAccount(double balance, String name){
    accountNumber = lastAssignedNum;
    lastAssignedNum++;
    accountBalance = balance;
    ownerName = name;
  }
  /* Accessor methods */
  /**
   Gets the minimum balance required of a bank account.
   @return MIN_BALANCE
   */
  public static double getMinBalance(){ //class method to get min balance
    return MIN_BALANCE; // a class variable
  }
  /**
   Gets the account number of the bank account.
   @return accountNumber
   */
  public int getAccountNumber(){
    return accountNumber;
  }
  /**
   Gets the current balance of the bank account.
   @return accountBalance
   */
  public double getAccountBalance(){
    return accountBalance;
  }
  /**
   Gets the name of the owner. (new method added)
   @return ownerName
   */
  public String getOwnerName(){
    return ownerName;
  }
  /* Mutator methods */
  /**
   Deposit money into the bank account.
   @param amount the amount to withdraw
   */
  public void deposit(double amount){
    accountBalance = accountBalance + amount;
  }
  /**
   Withdraw money from the bank account.
   @param amount the amount to withdraw
   */
  public void withdraw(double amount){
    accountBalance = accountBalance - amount;
  }
  public void setAccountBalance(double amount){
    accountBalance = amount;
  }
  /**
   Transfer money from one bank account to another bank account.
   @param amount the amount to transfer
   @param otherAccount the account to transfer amount to
   */
  public void transferTo(double amount, BankAccount otherAccount){
    withdraw(amount); //it is possible to call method of same class. The this keyword is implicit
    otherAccount.deposit(amount);
  }
  public String toString()
  {
    return "Bank Account> account num:"+ accountNumber+",bal:"+accountBalance;
  }
}

