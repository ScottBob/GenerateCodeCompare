package org.samples;


import org.approvaltests.Approvals;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTests
{
  @Test
  public void testCompareCode()
  {
    var snippet1 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              List<Customer> seniorCustomers = database.getSeniorCustomers();
              for (Customer customer : seniorCustomers) {
                  Discount seniorDiscount = getSeniorDiscount();
                  String message = generateDiscountMessage(customer, seniorDiscount);
                  mailServer.sendMessage(customer, message);
              }
          } 
          """;
    var snippet2 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              Loader<List<Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers();
              List<Customer> seniorCustomers = database.getSeniorCustomers() seniorCustomerLoader.load();
              for (Customer customer : seniorCustomers) {
                  Discount seniorDiscount = getSeniorDiscount();
                  String message = generateDiscountMessage(customer, seniorDiscount);
                  mailServer.sendMessage(customer, message);
              }
           }
          """;
    Approvals.verify(CodeCompare.generateMarkdown(snippet1, snippet2));
  }
  @Test
  @UseReporter(QuietReporter.class)
  public void testCreateMarkdown()
  {
    var comparison = List.of(
           Line.of("public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {"),
              Line.add("  Loader<List<Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers();"),
              Line.of("  List<Customer> seniorCustomers = ").remove("database.getSeniorCustomers()").replace("seniorCustomerLoader.load()").and(";"),
              Line.of("  for (Customer customer : seniorCustomers) {"),
              Line.of("    Discount seniorDiscount = getSeniorDiscount();"),
              Line.of("    String message = generateDiscountMessage(customer, seniorDiscount);"),
              Line.of("    mailServer.sendMessage(customer, message);"),
              Line.of("  }"),
              Line.of("}"));
    Approvals.verify(CodeCompare.generateMarkdown(comparison));
  }
}