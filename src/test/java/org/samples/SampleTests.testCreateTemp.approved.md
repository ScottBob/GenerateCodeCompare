<pre style="color: gray">
    @Test
    public void senior_customer_list_includes_only_those_over_age_65()
    {
<b style="color: red">      DataBase database = initializeDatabase(); </b>
      MailServer mailServer = initializeMailServer();
      sendOutSeniorDiscounts( mailServer, <b style="color: red">database</b> ); 
      Approvals.verifyAll("", mailServer.getRecipients());
    }
</pre>
# ⇓
<pre style="color: gray">
    @Test
    public void senior_customer_list_includes_only_those_over_age_65()
    {
<b style="color: green">      Loader&lt;List&lt;Customer>> mailingList = () -> List.of(new Customer("Bob"), new Customer("Mary"), new Customer("Tom")); </b>
<s style="color: red">      DataBase database = initializeDatabase(); </s> 
      MailServer mailServer = initializeMailServer();
      sendOutSeniorDiscounts( mailServer, <s style="color: red">database</s> <b style="color: green">mailingList</b> ); 
      Approvals.verifyAll("", mailServer.getRecipients());
    }
</pre>
