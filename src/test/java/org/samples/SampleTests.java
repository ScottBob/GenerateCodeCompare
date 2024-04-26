package org.samples;


import com.spun.util.io.FileUtils;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.reporters.DiffMergeReporter;
import org.approvaltests.reporters.QuietReporter;
import org.approvaltests.reporters.UseReporter;
import org.approvaltests.reporters.windows.BeyondCompareReporter;
import org.junit.jupiter.api.Test;

import java.util.List;

@UseReporter(QuietReporter.class)
public class SampleTests
{
  @Test
  public void testCompareCode()
  {
    var snippet1 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              List<Customer> seniorCustomers = database.getSeniorCustomers(); // *
              for (Customer customer : seniorCustomers) {
                  Discount seniorDiscount = getSeniorDiscount();
                  String message = generateDiscountMessage(customer, seniorDiscount);
                  mailServer.sendMessage(customer, message);
              }
          }
          """;
    var snippet2 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              Loader<List<Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers(); // +
              List<Customer> seniorCustomers = seniorCustomerLoader.load(); // *
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
  @UseReporter(BeyondCompareReporter.class)
  public void testDiffCode()
  {
    var snippet1 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              List<Customer> seniorCustomers = database.getSeniorCustomers(); // *
              for (Customer customer : seniorCustomers) {
                  Discount seniorDiscount = getSeniorDiscount();
                  String message = generateDiscountMessage(customer, seniorDiscount);
                  mailServer.sendMessage(customer, message);
              }
           } 
          """;
    var snippet2 = """
           public void sendOutSeniorDiscounts(DataBase database, MailServer mailServer) {
              Loader<List<Customer>> seniorCustomerLoader = () -> database.getSeniorCustomers(); // +
              List<Customer> seniorCustomers = seniorCustomerLoader.load(); // *
              for (Customer customer : seniorCustomers) {
                  Discount seniorDiscount = getSeniorDiscount();
                  String message = generateDiscountMessage(customer, seniorDiscount);
                  mailServer.sendMessage(customer, message);
              }
           }
          """;
    var diff = Diff.diffStrings(snippet1, snippet2);
    Approvals.verify(CodeCompare.generateMarkdown(diff));
  }
  @Test
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

  @Test
  @UseReporter(DiffMergeReporter.class)
  public void testModifiedLine() {
    var expected = """
        List<Customer> seniorCustomers =  (CONSTANT)
      database.getSeniorCustomers(); (REMOVED)
      seniorCustomerLoader.load(); (ADDED)
      """;
    String start = "  List<Customer> seniorCustomers = database.getSeniorCustomers();";
    String end = "  List<Customer> seniorCustomers = seniorCustomerLoader.load();";
    //Line result = Line.of("  List<Customer> seniorCustomers = ").remove("database.getSeniorCustomers();").replace("seniorCustomerLoader.load();");
    Line actual = Diff.createModifiedLine(start, end);
    Approvals.verify(actual.toString(), new Options().inline(expected));
  }

//  @Test
  public void testCreateTemp() {
    Approvals.verify(CodeCompare.generateMarkdown(FileUtils.readFile("src/test/java/org/samples/a.txt"), FileUtils.readFile("src/test/java/org/samples/b.txt")));
  }



}
