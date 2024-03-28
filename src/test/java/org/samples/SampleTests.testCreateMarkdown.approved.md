<pre>
public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
  List&lt;Customer> seniorCustomers = <b>database.getSeniorCustomers()</b>;
  for (Customer customer : seniorCustomers) {
    Discount seniorDiscount = getSeniorDiscount();
    String message = generateDiscountMessage(customer, seniorDiscount);
    mailServer.sendMessage(customer, message);
  }
}
</pre>
# ⇓
<pre>
public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
<b>  Loader&lt;List&lt;Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers();</b>
  List&lt;Customer> seniorCustomers = <s>database.getSeniorCustomers()</s> <b>seniorCustomerLoader.load()</b>;
  for (Customer customer : seniorCustomers) {
    Discount seniorDiscount = getSeniorDiscount();
    String message = generateDiscountMessage(customer, seniorDiscount);
    mailServer.sendMessage(customer, message);
  }
}
</pre>
