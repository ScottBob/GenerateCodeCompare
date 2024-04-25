<pre style="color: gray">
 public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
    List&lt;Customer> seniorCustomers = <b style="color: red">database.getSeniorCustomers(); </b>
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
<b style="color: green">    Loader&lt;List&lt;Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers(); </b>
    List&lt;Customer> seniorCustomers = <b style="color: green">seniorCustomerLoader.load(); </b><s style="color: red">database.getSeniorCustomers(); </s> 
    for (Customer customer : seniorCustomers) {
        Discount seniorDiscount = getSeniorDiscount();
        String message = generateDiscountMessage(customer, seniorDiscount);
        mailServer.sendMessage(customer, message);
    }
 }
</pre>
