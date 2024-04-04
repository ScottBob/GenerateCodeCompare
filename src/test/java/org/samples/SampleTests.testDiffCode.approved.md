<pre style="color: gray">
 public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
<b style="color: red">    List&lt;Customer> seniorCustomers = database.getSeniorCustomers();</b>
    for (Customer customer : seniorCustomers) {
        Discount seniorDiscount = getSeniorDiscount();
        String message = generateDiscountMessage(customer, seniorDiscount);
        mailServer.sendMessage(customer, message);
    }
 }
</pre>
# ⇓
<pre style="color: gray">
 public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
<s style="color: red">    List&lt;Customer> seniorCustomers = database.getSeniorCustomers();</s> 
<b style="color: green">    Loader&lt;List&lt;Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers();</b>
<b style="color: green">    List&lt;Customer> seniorCustomers = database.getSeniorCustomers() seniorCustomerLoader.load();</b>
    for (Customer customer : seniorCustomers) {
        Discount seniorDiscount = getSeniorDiscount();
        String message = generateDiscountMessage(customer, seniorDiscount);
        mailServer.sendMessage(customer, message);
    }
 }
</pre>
